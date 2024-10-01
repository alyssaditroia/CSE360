package database;

import java.sql.*;
import java.util.UUID;
import java.util.Random; // For OTP generation
import models.User;

public class Database {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/firstDatabase";

    // Database credentials
    static final String USER = "admin";
    static final String PASS = "Password1!";

    private Connection connection = null;
    private Statement statement = null;
    private Random random = new Random();

    // Method to connect to the database
    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();
            createTables();  // Create the necessary tables if they don't exist
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    // Method to create tables
    private void createTables() throws SQLException {
        String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, " 
                + "firstName VARCHAR(255),"
                + "lastName VARCHAR(255),"
                + "preferredName VARCHAR(255),"
                + "email VARCHAR(255) UNIQUE, "
                + "username VARCHAR(255),"
                + "password VARCHAR(255), "
                + "otp VARCHAR(255), " // Add OTP column
                + "isAdmin BOOLEAN DEFAULT FALSE, "
                + "isStudent BOOLEAN DEFAULT FALSE, "
                + "isInstructor BOOLEAN DEFAULT FALSE, "
                + "inviteToken VARCHAR(255),"
                + "otp_expiration TIMESTAMP)";
        statement.execute(userTable);
    }
	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}
    
    public void setupAdministrator(String username, String password, boolean isAdmin) throws SQLException {
    	if (connection == null || connection.isClosed()) {
            connectToDatabase(); // Ensure connection is established
        }
		String insertUser = "INSERT INTO cse360users (username, password, isAdmin) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			pstmt.setBoolean(3, isAdmin);
			pstmt.executeUpdate();
		}
	}
    public boolean validateCredentials(String username, String password) throws SQLException {
    	if (connection == null || connection.isClosed()) {
            connectToDatabase(); // Ensure connection is established
        }
        String query = "SELECT password, otp, otp_expiration FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            // Check if the user exists
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String storedOTP = rs.getString("otp");
                Timestamp expiration = rs.getTimestamp("otp_expiration");

                // Check if the provided password matches the stored password
                boolean isPasswordValid = password.equals(storedPassword); // Replace with hash comparison in production

                // Check if the provided OTP is valid and has not expired
                boolean isOTPValid = false;

                if (storedOTP != null && expiration != null) {
                    isOTPValid = password.equals(storedOTP) && expiration.after(new Timestamp(System.currentTimeMillis()));
                } else {
                    // If there's no OTP, the password is considered valid if it matches the stored password
                    isOTPValid = isPasswordValid;
                }

                // If the password matches or the OTP is valid, return true
                return isPasswordValid || isOTPValid;
            } else {
                // User not found
                return false;
            }
        }
    }



    // Invite a new user (generate invite link with a token)
    public String inviteUser(String email, boolean isAdmin, boolean isStudent, boolean isInstructor) throws SQLException {
        if (doesUserExist(email)) {
            throw new SQLException("User already exists with the provided email.");
        }
        
        // Generate a unique token for the invite link
        String inviteToken = UUID.randomUUID().toString();
        
        String insertInvite = "INSERT INTO cse360users (email, isAdmin, isStudent, isInstructor, inviteToken) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertInvite)) {
            pstmt.setString(1, email);
            pstmt.setBoolean(2, isAdmin);
            pstmt.setBoolean(3, isStudent);
            pstmt.setBoolean(4, isInstructor);
            pstmt.setString(5, inviteToken);
            pstmt.executeUpdate();
        }
        
        // Return the invite link (assuming http://app.com/invite?token=)
        return "inviteCode: " + inviteToken;
    }

    // Generate a one-time password (OTP)
    public String generateOTP() {
        int otp = 100000 + random.nextInt(900000); // Generate a 6-digit OTP
        return String.valueOf(otp);
    }

    // Send OTP to the user's email (Placeholder for actual sending logic)
    public void sendOTP(String email, String otp) {
        // Implement your email sending logic here
        System.out.println("Sending OTP " + otp + " to " + email);
    }

 // Create and send OTP for the user
    public void createAndSendOTP(String email) throws SQLException {
        String otp = generateOTP();
        Timestamp expiration = new Timestamp(System.currentTimeMillis() + 300000); // 5 minutes from now

        // Update the OTP column instead of the password column
        String updateOTP = "UPDATE cse360users SET otp = ?, otp_expiration = ? WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateOTP)) {
            pstmt.setString(1, otp);
            pstmt.setTimestamp(2, expiration);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
        }
        sendOTP(email, otp); // Send OTP to the user
        System.out.println(otp); // print otp
    }

 // Verify the OTP
    public boolean verifyOTP(String username, String otp) throws SQLException {
    	if (connection == null || connection.isClosed()) {
            connectToDatabase(); // Ensure connection is established
        }
        String query = "SELECT otp, otp_expiration FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedOTP = rs.getString("otp");
                    Timestamp expiration = rs.getTimestamp("otp_expiration");

                    // Check if the OTP matches and has not expired
                    if (otp.equals(storedOTP) && expiration != null && expiration.after(new Timestamp(System.currentTimeMillis()))) {
                        // Clear OTP after successful verification
                        String clearOTP = "UPDATE cse360users SET otp = NULL, otp_expiration = NULL WHERE email = ?";
                        try (PreparedStatement clearStmt = connection.prepareStatement(clearOTP)) {
                            clearStmt.setString(1, username);
                            clearStmt.executeUpdate();
                        }
                        return true; // OTP verified
                    }
                }
            }
        }
        return false; // OTP verification failed
    }
    public boolean getOTP(String username, String otp) throws SQLException {
        // Query to retrieve the OTP and its expiration for the given username
        String query = "SELECT otp, otp_expiration FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                // Check if the user exists and has an OTP
                if (rs.next()) {
                    String storedOTP = rs.getString("otp");
                    Timestamp expiration = rs.getTimestamp("otp_expiration");

                    // Check if the stored OTP matches the provided OTP
                    boolean isOTPMatched = otp.equals(storedOTP);
                    
                    // Check if the OTP has not expired
                    boolean isOTPNotExpired = expiration != null && expiration.after(new Timestamp(System.currentTimeMillis()));

                    // Return true if both conditions are met
                    return isOTPMatched && isOTPNotExpired;
                }
            }
        }
        // If no valid OTP found, return false
        return false;
    }


 // Accept the invite and complete the user creation
    public boolean completeInvite(String inviteToken, String username, String password) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE inviteToken = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, inviteToken);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Update the user record with the new details
                    String updateUser = "UPDATE cse360users SET username = ?, password = ?, inviteToken = NULL, otp = NULL, otp_expiration = NULL WHERE inviteToken = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateUser)) {
                        updateStmt.setString(1, username);
                        updateStmt.setString(2, password); // Still using the password field here
                        updateStmt.setString(3, inviteToken);
                        updateStmt.executeUpdate();
                        return true;
                    }
                }
            }
        }
        return false; // Invalid or used invite token
    }

    // Register a new user (for general use)
    public void addUser(String username, String password, boolean isAdmin, boolean isStudent, boolean isInstructor) throws SQLException {
        String insertUser = "INSERT INTO cse360users (username, password, isAdmin, isStudent, isInstructor) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setBoolean(3, isAdmin);
            pstmt.setBoolean(4, isStudent);
            pstmt.setBoolean(5, isInstructor);
            pstmt.executeUpdate();
        }
    }

    // Admin can create a new user directly
    public void createUser(String firstName, String lastName, String preferredName, String email, String username, String password, boolean isAdmin, boolean isStudent, boolean isInstructor) throws SQLException {
        if (doesUserExist(email)) {
            throw new SQLException("User already exists with the provided email.");
        }

        String insertUser = "INSERT INTO cse360users (firstName, lastName, preferredName, email, username, password, isAdmin, isStudent, isInstructor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, preferredName);
            pstmt.setString(4, email);
            pstmt.setString(5, username);
            pstmt.setString(6, password);
            pstmt.setBoolean(7, isAdmin);
            pstmt.setBoolean(8, isStudent);
            pstmt.setBoolean(9, isInstructor);
            pstmt.executeUpdate();
        }
    }

    // Check if a user already exists
    public boolean doesUserExist(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // User exists if count > 0
            }
        }
        return false; // User does not exist or error occurred
    }

    // Close the database connection
    public void close() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

	public String getFirstName(String username) {
	    // SQL query to retrieve the firstName for the given username
	    String query = "SELECT firstName FROM cse360users WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            // Check if a result is returned
	            if (rs.next()) {
	                return rs.getNString("firstName"); // Return the firstName if it exists
	            }
	        }
	    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    // If no firstName found, return null or an empty string
	    return null; // or return ""; based on your preference
	}
    // Method to update the user's password
    public boolean updatePassword(String username, String newPassword) {
        String updateSQL = "UPDATE users SET password = ? WHERE username = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Returns true if a row was updated
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error for debugging
            return false; // Return false in case of an error
        }
    }
    public boolean setupAccount(String username, String firstName, String lastName, String email, String perferredName) {
        String updateSQL = "UPDATE users SET firstName = ?, lastName = ?, email = ?, perferredName = ?, WHERE username = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, perferredName);
            preparedStatement.setString(5, username);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Returns true if a row was updated
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error for debugging
            return false; // Return false in case of an error
        }
    }
    public boolean setupAccount(String inviteToken, String username, String password) {
        String updateSQL = "UPDATE users SET username = ?, password = ?, WHERE inviteToken = ?";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, inviteToken);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0; // Returns true if a row was updated
        } catch (SQLException e) {
            e.printStackTrace(); // Log the error for debugging
            return false; // Return false in case of an error
        }
    }

	public void closeConnection() {
		 if (connection != null) {
	            try {
	                if (!connection.isClosed()) {
	                    connection.close();
	                    System.out.println("Database connection closed successfully.");
	                }
	            } catch (SQLException e) {
	                e.printStackTrace();
	                System.err.println("Failed to close the database connection.");
	            }
	        }
	    }

}

