package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import models.User;

public class database {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/firstDatabase";

    // Database credentials
    static final String USER = "sa";
    static final String PASS = "";

    private Connection connection = null;
    private Statement statement = null;

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
                + "email VARCHAR(255) UNIQUE, "
                + "password VARCHAR(255), "
                + "isAdmin BOOLEAN DEFAULT FALSE, "
                + "isUser BOOLEAN DEFAULT FALSE, "
                + "isModerator BOOLEAN DEFAULT FALSE)";
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

    // Register a new user
    public void addUser(String email, String password, boolean isAdmin, boolean isUser, boolean isModerator) throws SQLException {
        String insertUser = "INSERT INTO cse360users (email, password, isAdmin, isUser, isModerator) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setBoolean(3, isAdmin);
            pstmt.setBoolean(4, isUser);
            pstmt.setBoolean(5, isModerator);
            pstmt.executeUpdate();
        }
    }

    // Login method
    public boolean login(String email, String password) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE email = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Check if a user already exists
    public boolean doesUserExist(String email) {
        String query = "SELECT COUNT(*) FROM cse360users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // User exists if count > 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // User does not exist or error occurred
    }

    // Display users by admin
    public void displayUsersByAdmin() throws SQLException {
        String sql = "SELECT * FROM cse360users";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                // Retrieve by column name
                int id  = rs.getInt("id");
                String email = rs.getString("email");
                String password = rs.getString("password");
                boolean isAdmin = rs.getBoolean("isAdmin");
                boolean isUser = rs.getBoolean("isUser");
                boolean isModerator = rs.getBoolean("isModerator");

                // Display values
                System.out.println("ID: " + id + ", Email: " + email + ", Password: " + password 
                    + ", isAdmin: " + isAdmin + ", isUser: " + isUser + ", isModerator: " + isModerator);
            }
        }
    }

    // Display users for a regular user
    public void displayUsersByUser() throws SQLException {
        displayUsersByAdmin(); // Reusing the same method for simplicity
    }

    // Close database connection
    public void closeConnection() {
        try {
            if(statement != null) {
                statement.close();
            }
        } catch(SQLException se2) {
            se2.printStackTrace();
        }
        try {
            if(connection != null) {
                connection.close();
            }
        } catch(SQLException se){
            se.printStackTrace();
        }
    }

    // Update user information in the database
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE cse360users SET email = ?, password = ?, isAdmin = ?, isUser = ?, isModerator = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, new String(user.getPassword()));
            pstmt.setBoolean(3, user.isAdmin());
            pstmt.setBoolean(4, user.isStudent());
            pstmt.setBoolean(5, user.isInstructor());
            pstmt.executeUpdate();
        }
    }

	public void addUser(User newUser) {
		// TODO Auto-generated method stub
		
	}
}
