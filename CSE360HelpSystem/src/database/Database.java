package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FINISHED PAGE DO NOT EDIT
 * Database
 * 
 * This class manages all interactions with the database,
 * including saving users, managing OTP, and updating user details.
 * 
 * To visualize database run commands:
 * cd h2
 * cd bin
 * java -jar h2*.jar
 */
public class Database {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/firstDatabase";

    // Database credentials
    static final String USER = "user";
    static final String PASS = "";
    
    private static Database instance;

    private Connection connection;
    private Statement statement;
    /**
     * connectToDatabase()
     * Method to connect to the database
     * @throws SQLException
     */
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
    public Database() {
        // Private constructor to prevent instantiation
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     *  createTables()
     *  Method to create tables
     * @throws SQLException
     */
    private void createTables() throws SQLException {
    	System.out.println("Creating Tables");
        String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, " 
                + "firstName VARCHAR(255),"
                + "lastName VARCHAR(255),"
                + "preferredName VARCHAR(255),"
                + "email VARCHAR(255) UNIQUE, "
                + "username VARCHAR(255),"
                + "password VARCHAR(255), " 
                + "isAdmin BOOLEAN DEFAULT FALSE, "
                + "isStudent BOOLEAN DEFAULT FALSE, "
                + "isInstructor BOOLEAN DEFAULT FALSE, "
                + "inviteToken VARCHAR(255),"
                + "otp VARCHAR(255), "
                + "otp_expiration TIMESTAMP)";
        statement.execute(userTable);
    }
    
    /**
     * isDatabaseEmpty()
     * Method to check if the database is empty
     * @return boolean True or False
     * @throws SQLException
     */
public boolean isDatabaseEmpty() throws SQLException {
    	connection = DriverManager.getConnection(DB_URL, USER, PASS);
        statement = connection.createStatement();
        String query = "SELECT COUNT(*) AS count FROM cse360users";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
        	boolean dbStatus = resultSet.getInt("count") == 0;
        	System.out.println("Database is empty " + dbStatus);
            return resultSet.getInt("count") == 0; 
        }
        return false;
    }
    /**
     * Connection()
     * This method returns the active connection object
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        return connection;
    }

    /**
     *  Method to setup the first ever user as an administrator user
     *  Uses just the username and password input from the login page to setup first user in database
     * @param username
     * @param password
     * @throws SQLException
     */
public void setupAdministrator(String username, String password) throws SQLException {
    	connection = DriverManager.getConnection(DB_URL, USER, PASS);
        statement = connection.createStatement();
        // Validate input parameters
        if (username == null || username.isEmpty()) {
            System.out.println("Failed to add administrator: username cannot be null or empty.");
            return; // Early exit if username is invalid
        }
        
        if (password == null || password.isEmpty()) {
            System.out.println("Failed to add administrator: password cannot be null or empty.");
            return; // Early exit if password is invalid
        }

        String insertUser = "INSERT INTO cse360users (username, password, isAdmin) VALUES (?, ?, TRUE)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            // Set parameters for the prepared statement
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            // Execute the update
            int rowsAffected = pstmt.executeUpdate();
            
            // Check if the insertion was successful
            if (rowsAffected > 0) {
                System.out.println("Administrator successfully added to Database");
            } else {
                System.out.println("Failed to add administrator: No rows affected.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception occurred while adding administrator: " + e.getMessage());
            throw e; // Rethrow the exception after logging it
        }
    }
    /**
     * Method to update different sections of the user's credentials. 
     * If the field passed through is null, it will not be updated
     * @param username
     * @param firstName
     * @param lastName
     * @param email
     * @throws SQLException
     */
    public void updateUser(String username, String firstName, String lastName, String preferredName, String email) throws SQLException {
    	connection = DriverManager.getConnection(DB_URL, USER, PASS);
        statement = connection.createStatement();
        
    	List<String> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();
        
        if (firstName != null) {
            fields.add("firstName = ?");
            values.add(firstName);
        }
        if (lastName != null) {
            fields.add("lastName = ?");
            values.add(lastName);
        }
        if (preferredName != null) {
            fields.add("preferredName = ?");
            values.add(preferredName);
        }
        if (email != null) {
            fields.add("email = ?");
            values.add(email);
        }

        // Check if there's anything to update
        if (fields.isEmpty()) {
            System.out.println("No fields to update.");
            return;
        }

        String query = "UPDATE cse360users SET " + String.join(", ", fields) + " WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setString(i + 1, values.get(i));
            }
            stmt.setString(values.size() + 1, username); // Username at the end

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No user found with username: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // rethrow the exception after logging it
        }
    }
    
    public boolean updatePassword(String username, String password) {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            // Establishing connection to the database
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            
            // Preparing the SQL query to update the password
            String updateQuery = "UPDATE cse360users SET password = ? WHERE username = ?";

            // Using PreparedStatement to set parameters and execute the update
            pstmt = connection.prepareStatement(updateQuery);
            pstmt.setString(1, password);  // Set the new password
            pstmt.setString(2, username);  // Set the username

            // Execute the update and check if the password was successfully updated
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Returns true if at least one row was updated

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Close the resources in the finally block to avoid resource leaks
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    


    /**
     * Method to create a new user in the database when they are invited
     * Sets the permissions associated with the email that the invite code was sent to
     * User is able to be found using completeInvite() with the invite code
     * @param email
     * @param isAdmin
     * @param isStudent
     * @param isInstructor
     * @return inviteToken
     * @throws SQLException
     */
    public String inviteUser(String inviteCode, String email, boolean isAdmin, boolean isStudent, boolean isInstructor) throws SQLException {
    	connection = DriverManager.getConnection(DB_URL, USER, PASS);
        statement = connection.createStatement();
        if (doesUserExist(inviteCode)) {
            throw new SQLException("User already exists with the provided invite code.");
        }
        String insertInvite = "INSERT INTO cse360users (isAdmin, isStudent, isInstructor, email, inviteToken) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertInvite)) {
            pstmt.setBoolean(1, isAdmin);
            pstmt.setBoolean(2, isStudent);
            pstmt.setBoolean(3, isInstructor);
            pstmt.setString(4,  email);
            pstmt.setString(5, inviteCode);
            pstmt.executeUpdate();
        }
        return inviteCode;
    }

    /**
     * Method to complete the invite by setting the users username and password for that specific invite code
     * Updates the user based on the invite code linked to that user
     * @param inviteToken
     * @param username
     * @param password
     * @return boolean true or false
     * @throws SQLException
     */
    public boolean completeInvite(String inviteToken, String username, String password) throws SQLException {
    	connection = DriverManager.getConnection(DB_URL, USER, PASS);
        statement = connection.createStatement();
    	// Queries database based on invite code
        String query = "SELECT * FROM cse360users WHERE inviteToken = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, inviteToken);
            ResultSet rs = pstmt.executeQuery();
            
            // If the invite code exists then the user is updated accordingly and the invite token is set to NULL
            if (rs.next()) {
                String updateUser = "UPDATE cse360users SET username = ?, password = ?, inviteToken = NULL WHERE inviteToken = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateUser)) {
                    updateStmt.setString(1, username);
                    updateStmt.setString(2, password);
                    updateStmt.setString(3, inviteToken);
                    updateStmt.executeUpdate();
                    return true;
                }
            }
        }
        return false;
    }
    public boolean validateInvite(String inviteToken) throws SQLException {
    	connection = DriverManager.getConnection(DB_URL, USER, PASS);
        statement = connection.createStatement();
    	// Queries database based on invite code
        String query = "SELECT * FROM cse360users WHERE inviteToken = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, inviteToken);
            ResultSet rs = pstmt.executeQuery();
            
            // If the invite code exists then the user is updated accordingly and the invite token is set to NULL
            if (rs.next()) {
                    return true;
                }
            }
        return false;
        }
    /**
     * validateCredentials()
     * Method to validate user credentials to confirm the username and password match a user in the database
     * @param username
     * @param password
     * @return true if the user's username and password match a specific user in the database false if no user is found
     * @throws SQLException
     */
    public boolean validateCredentials(String username, String password) throws SQLException {
    	connection = DriverManager.getConnection(DB_URL, USER, PASS);
        statement = connection.createStatement();
        String query = "SELECT password FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return password.equals(storedPassword);
            }
        }
        return false;
    }
    
    /**
     * getFirstName()
     * Method to get the first name of the user based on the username
     * @param username
     * @return
     */
    public String getFirstName(String username) {
    	try {
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        String query = "SELECT firstName FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedFirstName = rs.getString("firstName");
                return storedFirstName != null ? storedFirstName : "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * getFirstName()
     * Method to get the first name of the user based on the username
     * @param username
     * @return
     */
    public String getEmail(String email) {
    	try {
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        String query = "SELECT firstName FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedEmail = rs.getString("email");
                return storedEmail != null ? storedEmail : "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     *  Check if a user exists by email
     * @param email
     * @return true if they exist false if not
     * @throws SQLException
     */
    private boolean doesUserExist(String email) throws SQLException {
    	connection = DriverManager.getConnection(DB_URL, USER, PASS);
        statement = connection.createStatement();
        String query = "SELECT COUNT(*) FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    /**
     * Closes connection to the database
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Failed to close the database connection.");
                e.printStackTrace();
            }
        } else {
            System.out.println("No connection to close.");
        }
    }


}
