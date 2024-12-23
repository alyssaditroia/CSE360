package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * The {@code Database} class manages all interactions with the database. Primarily the user database. 
 * 
 * @author Alyssa DiTroia
 * @author Justin Faris
 * 
 * To visualize database run commands in your terminal: cd /h2/bin java -jar h2*.jar
 * 
 * Make sure the terminal is closed and database connection is not open before
 * running the application
 * 
 * You can close connection in terminal with control c
 * 
 * If using Windows OS change the DB_URL path:
 * "jdbc:h2:C:\\\\Users\\\\YourUserNameHere\\\\h2\\\\ASUHelpDatabase" If using
 * UNIX or Linux change the DB_URL path: "jdbc:h2:~/ASUHelpDatabase"
 * 
 * To clear contents of the database delete the database file from your computer
 * located in /Users/YourUserNameHere
 */
public class Database {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";

	static final String DB_URL = "jdbc:h2:~/CSE360HelpTest1";
	 
	// Database credentials
	static final String USER = "user";
	static final String PASS = "";

	private static Database instance;

	private Connection connection;
	protected Statement statement;

	/**
	 * Function added by Alyssa DiTroia 
	 * connectToDatabase() Method to connect to the database
	 * 
	 * @throws SQLException
	 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("[Database] Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("[Database] Connection established");
			statement = connection.createStatement();
			System.out.println("[Database] Statement created");
			createTables(); // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("[Database] JDBC Driver not found: " + e.getMessage());
		}
	}

	public Database() {
		// Private constructor to prevent instantiation
	}

	/**
	 * Function added by Alyssa DiTroia 
	 * getInstance() gets the database instance or creates a new one
	 * 
	 * @return instance
	 */
	public static synchronized Database getInstance() {
		if (instance == null) {
			instance = new Database();
		}
		return instance;
	}

	/**
	 * Function added by Alyssa DiTroia 
	 * createTables() Method to create tables
	 * 
	 * @throws SQLException
	 */
	// TODO Add encryption
	public void createTables() throws SQLException {
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
		+ "otpFlag BOOLEAN DEFAULT FALSE, " 
		+ "otpExpiration TIMESTAMP)";
		statement.execute(userTable);
		System.out.println("[Database] Creating Tables");
	}

	/**
	 * Function added by Alyssa DiTroia 
	 * isDatabaseEmpty() Method to check if the database is empty
	 * 
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
			System.out.println("[Database] Database is empty " + dbStatus);
			return resultSet.getInt("count") == 0;
		}
		return false;
	}

	/**
	 * Function added by Alyssa DiTroia 
	 * Connection() This method returns the active connection object
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return connection;
	}

	/**
	 * Function added by Alyssa DiTroia 
	 * Method to setup the first ever user as an administrator user Uses just the
	 * username and password input from the login page to setup first user in
	 * database
	 * 
	 * @param username
	 * @param password
	 * @throws SQLException
	 */
	// TODO add encryption
	public void setupAdministrator(String username, char[] password) throws SQLException {
		connection = DriverManager.getConnection(DB_URL, USER, PASS);
		statement = connection.createStatement();
		// Validate input parameters
		if (username == null || username.isEmpty()) {
			System.out.println("[Database] Failed to add administrator: username cannot be null or empty.");
			return; // Early exit if username is invalid
		}

		if (password == null || password.length == 0) {
			System.out.println("[Database] Failed to add administrator: password cannot be null or empty.");
			return; // Early exit if password is invalid
		}

		String insertUser = "INSERT INTO cse360users (username, password, isAdmin) VALUES (?, ?, TRUE)";

		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			// Set parameters for the prepared statement
			pstmt.setString(1, username);
			pstmt.setString(2, String.valueOf(password));

			// Execute the update
			int rowsAffected = pstmt.executeUpdate();

			// Check if the insertion was successful
			if (rowsAffected > 0) {
				System.out.println("[Database] Administrator successfully added to Database");
			} else {
				System.out.println("[Database] Failed to add administrator: No rows affected.");
			}
		} catch (SQLException e) {
			System.out.println("SQL Exception occurred while adding administrator: " + e.getMessage());
			throw e; // Rethrow the exception after logging it
		}
	}

	/**
	 * Function added by Alyssa DiTroia 
	 * Method to update different sections of the user's credentials. If the field
	 * passed through is null, it will not be updated
	 * 
	 * @param username
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @throws SQLException
	 */
	public void updateUser(String username, String firstName, String lastName, String preferredName, String email)
			throws SQLException {
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
			System.out.println("[Database] No fields to update.");
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
				System.out.println("[Database] No user found with username: " + username);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e; // rethrow the exception after logging it
		}
	}

	/**
	 * Function added by Alyssa DiTroia 
	 * Updates the user's password in the database
	 * @param username
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	// TODO add encryption
	public boolean updatePassword(String username, char[] password) throws SQLException {
		connection = DriverManager.getConnection(DB_URL, USER, PASS);
		statement = connection.createStatement();
		// Queries database based on invite code
		String query = "SELECT * FROM cse360users WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			// If the user exists then the user is updated accordingly and the invite token
			// is set to NULL
			if (rs.next()) {
				String updateUser = "UPDATE cse360users SET password = ? WHERE username = ?";
				try (PreparedStatement updateStmt = connection.prepareStatement(updateUser)) {
					updateStmt.setString(1, String.valueOf(password));
					updateStmt.setString(2, username);
					updateStmt.executeUpdate();
					Arrays.fill(password, '\0');
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Function added by Alyssa DiTroia 
	 * Method to create a new user in the database when they are invited Sets the
	 * permissions associated with the email that the invite code was sent to User
	 * is able to be found using completeInvite() with the invite code
	 * 
	 * @param email
	 * @param isAdmin
	 * @param isStudent
	 * @param isInstructor
	 * @return inviteToken
	 * @throws SQLException
	 */
	public String inviteUser(String inviteCode, String email, boolean isAdmin, boolean isStudent, boolean isInstructor)
			throws SQLException {
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
			pstmt.setString(4, email);
			pstmt.setString(5, inviteCode);
			pstmt.executeUpdate();
		}
		return inviteCode;
	}

	/**
	 * Function added by Alyssa DiTroia 
	 * Method to complete the invite by setting the users username and password for
	 * that specific invite code Updates the user based on the invite code linked to
	 * that user
	 * 
	 * @param inviteToken
	 * @param username
	 * @param password
	 * @return boolean true or false
	 * @throws SQLException
	 */
	// TODO add encryption
	public boolean completeInvite(String inviteToken, String username, char[] password) throws SQLException {
		connection = DriverManager.getConnection(DB_URL, USER, PASS);
		statement = connection.createStatement();
		// Queries database based on invite code
		String query = "SELECT * FROM cse360users WHERE inviteToken = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, inviteToken);
			ResultSet rs = pstmt.executeQuery();

			// If the invite code exists then the user is updated accordingly and the invite
			// token is set to NULL
			if (rs.next()) {
				String updateUser = "UPDATE cse360users SET username = ?, password = ?, inviteToken = NULL WHERE inviteToken = ?";
				try (PreparedStatement updateStmt = connection.prepareStatement(updateUser)) {
					updateStmt.setString(1, username);
					updateStmt.setString(2, String.valueOf(password));
					updateStmt.setString(3, inviteToken);
					updateStmt.executeUpdate();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Function added by Alyssa DiTroia 
	 * @param inviteToken
	 * @return
	 * @throws SQLException
	 */
	public boolean validateInvite(String inviteToken) throws SQLException {
		connection = DriverManager.getConnection(DB_URL, USER, PASS);
		statement = connection.createStatement();
		// Queries database based on invite code
		String query = "SELECT * FROM cse360users WHERE inviteToken = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, inviteToken);
			ResultSet rs = pstmt.executeQuery();

			// If the invite code exists then the user is updated accordingly and the invite
			// token is set to NULL
			if (rs.next()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Function added by Alyssa DiTroia 
	 * validateCredentials() Method to validate user credentials to confirm the
	 * username and password match a user in the database
	 * 
	 * @param username
	 * @param password
	 * @return true if the user's username and password match a specific user in the
	 *         database false if no user is found
	 * @throws SQLException
	 */
	public boolean validateCredentials(String username, char[] password) throws SQLException {
		connection = DriverManager.getConnection(DB_URL, USER, PASS);
		String query = "SELECT password FROM cse360users WHERE username = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) { // Only need to check once
				// Retrieve the stored password as a String
				String storedPasswordString = rs.getString("password");

				// If the stored password is null, return false
				if (storedPasswordString == null) {
					return false;
				}

				// Convert stored password to char array
				char[] storedPassword = storedPasswordString.toCharArray();

				// Check if the lengths match
				if (storedPassword.length != password.length) {
					return false;
				}

				// Compare each character
				for (int i = 0; i < storedPassword.length; i++) {
					if (storedPassword[i] != password[i]) {
						return false; // Passwords do not match
					}
				}

				return true; // Passwords match
			}
		}

		return false; // User not found
	}

	/**
	 * Function added by Alyssa DiTroia 
	 * Deletes a user and any help messages they had alongside any conversations they started from the database
	 * @param email
	 * @throws SQLException
	 */
	public void deleteUser(String email) throws SQLException {
	    // Get user ID first
	    int userId;
	    String getIdSql = "SELECT id FROM cse360users WHERE email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(getIdSql)) {
	        pstmt.setString(1, email);
	        ResultSet rs = pstmt.executeQuery();
	        if (!rs.next()) {
	            return; // User not found
	        }
	        userId = rs.getInt("id");
	    }

	    // Delete from conversation_messages where user is creator
	    String deleteConvMessages = "DELETE FROM conversation_messages WHERE creator_id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteConvMessages)) {
	        pstmt.setInt(1, userId);
	        pstmt.executeUpdate();
	    }

	    // Delete all help messages from this user
	    String deleteMessages = "DELETE FROM help_messages WHERE user_id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteMessages)) {
	        pstmt.setInt(1, userId);
	        pstmt.executeUpdate();
	    }
	    
	    // Finally delete the user
	    String deleteUser = "DELETE FROM cse360users WHERE email = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteUser)) {
	        pstmt.setString(1, email);
	        pstmt.executeUpdate();
	    }
	}

	/**
	 * Function added by Alyssa DiTroia 
	 * getFirstName() Method to get the first name of the user based on the username
	 * 
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
	 * Function added by Alyssa DiTroia 
	 * getEmail() Method to get the email of the user to check if it is there
	 * 
	 * @param email type string
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
			//e.printStackTrace();
		}
		return "";
	}

	/**
	 * Function added by Alyssa DiTroia 
	 * Check if a user exists by email
	 * 
	 * @param email
	 * @return true if they exist false if not
	 * @throws SQLException
	 */
	public boolean doesUserExist(String email) throws SQLException {
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
	 * Function added by Alyssa DiTroia 
	 * Closes the connection to the database
	 */
	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
				System.out.println("[Database] Database connection closed.");
			} catch (SQLException e) {
				System.err.println("[Database] Failed to close the database connection.");
				e.printStackTrace();
			}
		} else {
			System.out.println("[Database] No connection to close.");
		}
	}

	/**
	 * Function added by Justin Faris - 10/3/24
	 * 
	 * Checks if the user with the given username inside the database has admin role
	 * privileges
	 * 
	 * @param username The username of the user that must be checked
	 * @return Boolean True if the user is an admin, or False if the they are not or
	 *         the user can not be found
	 * @throws SQLException If there's an error executing the SQL query.
	 */
	public Boolean isUserAdmin(String username) throws SQLException {
		connection = DriverManager.getConnection(DB_URL, USER, PASS);
		statement = connection.createStatement();
		String query = "SELECT isAdmin FROM cse360users WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					if (rs.getObject("isAdmin") != null) {
						return rs.getBoolean("isAdmin");
					}
				}
			}
		}
		return false; // User not found or role not set
	}

	/**
	 * Function added by Justin Faris - 10/3/24
	 * 
	 * Checks if the user with the given username inside the database has student
	 * role privileges
	 * 
	 * @param username The username of the user that must be checked
	 * @return Boolean True if the user is an admin, or False if the they are not or
	 *         the user can not be found
	 * @throws SQLException If there's an error executing the SQL query.
	 */
	public Boolean isUserStudent(String username) throws SQLException {
		connection = DriverManager.getConnection(DB_URL, USER, PASS);
		statement = connection.createStatement();
		String query = "SELECT isStudent FROM cse360users WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					if (rs.getObject("isStudent") != null) {
						return rs.getBoolean("isStudent");
					}
				}
			}
		}
		return false; // User not found or role not set
	}

	/**
	 * Function added by Justin Faris - 10/3/24
	 * 
	 * Checks if the user with the given username inside the database has instructor
	 * role privileges
	 * 
	 * @param username The username of the user that must be checked
	 * @return Boolean True if the user is an admin, or False if the they are not or
	 *         the user can not be found
	 * @throws SQLException If there's an error executing the SQL query.
	 */
	public Boolean isUserInstructor(String username) throws SQLException {
		connection = DriverManager.getConnection(DB_URL, USER, PASS);
		statement = connection.createStatement();
		String query = "SELECT isInstructor FROM cse360users WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					if (rs.getObject("isInstructor") != null) {
						return rs.getBoolean("isInstructor");
					}
				}
			}
		}
		return false; // User not found or role not set
	}

	/**
	 * Updates user permissions in the database
	 * @param email
	 * @param isAdmin
	 * @param isStudent
	 * @param isInstructor
	 * @throws SQLException
	 */
	public void updateUserPermissions(String email, Boolean isAdmin, Boolean isStudent, Boolean isInstructor)
			throws SQLException {
		String query = "UPDATE cse360users SET isAdmin = ?, isStudent = ?, isInstructor = ? WHERE email = ?";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setBoolean(1, isAdmin);
			stmt.setBoolean(2, isStudent);
			stmt.setBoolean(3, isInstructor);
			stmt.setString(4, email);

			stmt.executeUpdate();
		}
	}

	/**
	 * Retrieve all users from the cse360users table.
	 * 
	 * @return List of type User A list of all users
	 * @throws SQLException
	 */
	public List<Map<String, Object>> getAllUsers() throws SQLException {
		List<Map<String, Object>> usersList = new ArrayList<>();
		String query = "SELECT * FROM cse360users";

		try (PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				Map<String, Object> userMap = new HashMap<>();
				userMap.put("id", resultSet.getInt("id"));
				userMap.put("firstName", resultSet.getString("firstName"));
				userMap.put("lastName", resultSet.getString("lastName"));
				userMap.put("preferredName", resultSet.getString("preferredName"));
				userMap.put("email", resultSet.getString("email"));
				userMap.put("username", resultSet.getString("username"));
				userMap.put("isAdmin", resultSet.getBoolean("isAdmin"));
				userMap.put("isStudent", resultSet.getBoolean("isStudent"));
				userMap.put("isInstructor", resultSet.getBoolean("isInstructor"));
				userMap.put("inviteToken", resultSet.getString("inviteToken"));
				userMap.put("otp", resultSet.getString("otp"));
				userMap.put("otpExpiration", resultSet.getTimestamp("otpExpiration"));
				userMap.put("otpFlag", resultSet.getBoolean("otpFlag"));

				usersList.add(userMap);
			}
		}

		return usersList;
	}

	/**
	 * 
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public Boolean getOTPFlag(String username) throws SQLException {
		// Initialize the connection
		connection = DriverManager.getConnection(DB_URL, USER, PASS);

		// Define the SQL query to select the otpFlag
		String query = "SELECT otpFlag FROM cse360users WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			// Check if a result exists
			if (rs.next()) {
				// Return true if otpFlag is true, false if otpFlag is false
				return rs.getBoolean("otpFlag");
			} else {
				// Return null if no user found
				return null;
			}
		}
	}
	
	
	/**
	 * Gets the user ID from the database based on username
	 * 
	 * @param username The username to look up
	 * @return The user's ID from the database, or -1 if not found
	 * @throws SQLException if database operation fails
	 */
	public int getUserId(String username) throws SQLException {
	    connection = DriverManager.getConnection(DB_URL, USER, PASS);
	    String query = "SELECT id FROM cse360users WHERE username = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getInt("id");
	        }
	        return -1; // User not found
	    }
	}
}

