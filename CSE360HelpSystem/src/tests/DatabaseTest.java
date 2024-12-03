package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import database.Database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
/**
 * A class for testing database functionalities
 * 
 *  @author Alyssa DiTroia
 *  @author Cooper Anderson
 */
class DatabaseTest {
	private Database database;
	private Connection connection;
	private static int testcount = 1;
	private static int testPass = 0;

	@BeforeEach
	void setUp() throws SQLException {
		System.out.println("\n =========== DATABASE TEST ===========\n");
		System.out.printf("Test Group # %d%n", testcount++);
		System.out.println();
		database = Database.getInstance();
		database.connectToDatabase();
		connection = database.getConnection(); // Get the connection for cleanup
		clearTestDatabase(); // Ensure the test database is clean before each test
		database.createTables(); // Ensure tables are created
	}

	@AfterEach
	void tearDown() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
				System.out.println("\nDatabase Connection Closed\n");
			}
		} catch (SQLException e) {
			System.err.println("[DatabaseTest] Error closing the connection: " + e.getMessage());
		}
	}

	@AfterAll
	static void afterAll() {
		System.out.println("\n =========== DATABASE TESTS COMPLETE ===========\n");
    	System.out.println("\nTOTAL TESTS: " + testPass + "\nTESTS PASSED: " + testPass + "\n");
		deleteTestDatabase(); // Clean up the test database after all tests
	}

	private void clearTestDatabase() throws SQLException {
		// Clear the database (truncate or delete rows) before each test
		try (Statement stmt = connection.createStatement()) {
			stmt.execute("TRUNCATE TABLE cse360users"); 
			System.out.println("\n[DatabaseTest] Test database cleared.");
		} catch (SQLException e) {
			System.err.println("[DatabaseTest] Error clearing test database: " + e.getMessage());
		}
	}
	/**
	 * Deletes the test database after testing
	 **/
	private static void deleteTestDatabase() {
		String url = "jdbc:h2:~/CSE360HelpTest";
		String user = "user"; // Default user for H2
		String password = ""; // Default password for H2 (empty)

		try (Connection conn = DriverManager.getConnection(url, user, password);
				Statement stmt = conn.createStatement()) {
			stmt.execute("DROP ALL OBJECTS");
			System.out.println("\nTest database deleted successfully.");
		} catch (SQLException e) {
			System.err.println("[DatabaseTest] Error deleting test database: " + e.getMessage());
		}
	}

	/**
	 * Checks proper connection to test database using the Database class functionality 
	 * @throws SQLException
	 */
	@Test
	void testConnectToDatabase() throws SQLException {
		System.out.println("\n ------- Testing Connection -------\n");
		System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
		assertNotNull(database.getConnection(), "Connection should not be null after connecting to the database");
		testPass+= 1;
	}

	/**
	 * Checks if test database is empty from the database class
	 * @throws SQLException
	 */
	@Test
	void testIsDatabaseEmptyInitially() throws SQLException {
		System.out.println("\n ------- Testing IsEmpty() -------\n");
		System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
		assertTrue(database.isDatabaseEmpty(), "Database should be empty ");
		testPass+= 1;
	}

	/**
	 * Checks setup admin functionality
	 * @throws SQLException
	 */
	@Test
	void testSetupAdministrator() throws SQLException {
		System.out.println("\n ------- Testing SetupAdministrator() -------\n");
		System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
		String username = "admin";
		char[] password = "Password1!".toCharArray();
		char [] nullPass = "".toCharArray();
		database.setupAdministrator("", password);
		database.setupAdministrator(username, nullPass);
		database.setupAdministrator(username, password);

		assertFalse(database.isDatabaseEmpty(), "Database should not be empty after adding an administrator");
		assertTrue(database.isUserAdmin(username), "User should be an admin after setup");
		testPass+= 2;
	}

	/**
	 * Checks update user functionality
	 * @throws SQLException
	 */
	@Test
	void testUpdateUSer() throws SQLException {
		System.out.println("\n ------- Testing UpdateUser() -------\n");
		System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
		String username = "admin";
		char[] password = "Password1!".toCharArray();
		database.setupAdministrator(username, password);
		database.updateUser("abc", "xyz", "abc", "", "nouser@email.com");
		database.updateUser(username, "", "", "", "");
		database.updateUser(username, "admin", "lastName", "", "admin@eamail.com");
		String email = database.getEmail("admin@eamail.com");
		assertNotNull(database.getEmail("admin@eamail.com"), "admin@eamail.com");
		assertNotNull(database.doesUserExist("admin@eamail.com"), "admin@eamail.com");
		testPass+= 2;
	}

	/**
	 * Checks invite user functionality
	 * @throws SQLException
	 */
	@Test
	void testInviteUser() throws SQLException {
		System.out.println("\n ------- Testing InviteUser() -------\n");
		System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
		String inviteCode = "INVITE123";
		String email = "invitee@example.com";
		database.inviteUser(inviteCode, email, true, false, false);

		assertFalse(database.isDatabaseEmpty(), "Database should not be empty after inviting a user");
		assertTrue(database.doesUserExist(email), "User should exist after inviting");
		testPass+= 2;
	}

	/**
	 * Checks complete invite functionality
	 * @throws SQLException
	 */
	@Test
	void testCompleteInvite() throws SQLException {
		System.out.println("\n ------- Testing CompleteInvite() -------\n");
		System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
		String inviteCode = "INVITE123";
		String email = "invitee@example.com";
		database.inviteUser(inviteCode, email, true, false, false);

		boolean result = database.completeInvite(inviteCode, "newUser", "newPass".toCharArray());
		assertTrue(result, "Invite should be completed successfully");
		assertTrue(database.isUserAdmin("newUser"), "New user should be an admin after completing invite");
		testPass+= 2;
	}

	/**
	 * Checks the role check functionality
	 * @throws SQLException
	 */
	@Test
	void testIsUserAdmin() throws SQLException {
		System.out.println("\n ------- Testing Admin Permission Check -------\n");
		System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
		String username = "admin";
		char[] password = "Password1!".toCharArray();
		database.setupAdministrator(username, password);

		assertTrue(database.isUserAdmin(username), "User should have admin privileges");
		assertFalse(database.isUserAdmin("nonExistentUser"), "Non-existent user should not have admin privileges");
		testPass+= 2;
	}
	/**
	 * Checks the role check functionality
	 * @throws SQLException
	 */
	@Test
	void testIsUserStudent() throws SQLException {
		System.out.println("\n ------- Testing User Database Related Functions -------\n");
		System.out.println("\nTESTS IN THIS TEST GROUP: 11\n");
		String username = "student";
		char[] password = "Password1!".toCharArray();
		database.setupAdministrator(username, password);
		database.updateUser(username, "Alyssa", "Last", "perf" , "email@email.com");
		database.updateUserPermissions("email@email.com", true, true, true);
		database.getUserId(username);
		database.getOTPFlag(username);
		String retrievedFirstName = database.getFirstName(username);
	    
		System.out.println("Testing first name matches in database");
		// Verify the first name matches
	    assertEquals("Alyssa", retrievedFirstName, "First name should match the one stored in the database");

	    boolean isValid = database.validateCredentials(username, password);
	    System.out.println("Testing credential validation");
	    assertTrue(isValid, "Valid credentials should return true");

	    System.out.println("Testing invalid username for user");
	    // Test invalid username
	    isValid = database.validateCredentials("invalidUser", password);
	    assertFalse(isValid, "Invalid username should return false");

	    System.out.println("Testing invalid password for user");
	    // Test invalid password
	    isValid = database.validateCredentials(username, "WrongPassword".toCharArray());
	    assertFalse(isValid, "Invalid password should return false");
	    
	    System.out.println("Testing getting non existent user");
	    // Test with a non-existent user
	    retrievedFirstName = database.getFirstName("nonExistentUser");
	    assertEquals("", retrievedFirstName, "Non-existent user should return an empty string as first name");
		assertTrue(database.updatePassword(username, password), "User exists password should udpate");
		assertTrue(database.isUserStudent(username), "User should have student privileges");
		assertTrue(database.isUserAdmin(username), "User should have admin privileges");
		assertTrue(database.isUserInstructor(username), "User should have instructor privileges");
		
		System.out.println("Testing delete user");
	    // Delete the user

	    // Verify the user and related data are deleted
	    assertTrue(database.doesUserExist("email@email.com"), "User should exist in the database");
	    assertFalse(database.isDatabaseEmpty(), "Database should not be empty");
	    testPass+= 11;
	}

	@Test
	void testGetAllUsers() throws SQLException {
	    System.out.println("\n ------- Testing getAllUsers() -------\n");
	    System.out.println("\nTESTS IN THIS TEST GROUP: 24\n");

	    // Set up test data
	    String insertUser1 = "INSERT INTO cse360users (firstName, lastName, preferredName, email, username, isAdmin, isStudent, isInstructor, inviteToken, otp, otpExpiration, otpFlag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    String insertUser2 = "INSERT INTO cse360users (firstName, lastName, preferredName, email, username, isAdmin, isStudent, isInstructor, inviteToken, otp, otpExpiration, otpFlag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try (PreparedStatement pstmt1 = connection.prepareStatement(insertUser1);
	         PreparedStatement pstmt2 = connection.prepareStatement(insertUser2)) {

	        pstmt1.setString(1, "Alyssa");
	        pstmt1.setString(2, "Doe");
	        pstmt1.setString(3, "Johnny");
	        pstmt1.setString(4, "alyssa.doe@example.com");
	        pstmt1.setString(5, "lyss");
	        pstmt1.setBoolean(6, true);
	        pstmt1.setBoolean(7, false);
	        pstmt1.setBoolean(8, false);
	        pstmt1.setString(9, "TOKEN123");
	        pstmt1.setString(10, "OTP1");
	        pstmt1.setTimestamp(11, new Timestamp(System.currentTimeMillis() + 3600000)); // +1 hour
	        pstmt1.setBoolean(12, true);
	        pstmt1.executeUpdate();

	        pstmt2.setString(1, "Jane");
	        pstmt2.setString(2, "Smith");
	        pstmt2.setString(3, "Janie");
	        pstmt2.setString(4, "jane.smith@example.com");
	        pstmt2.setString(5, "janesmith");
	        pstmt2.setBoolean(6, false);
	        pstmt2.setBoolean(7, true);
	        pstmt2.setBoolean(8, false);
	        pstmt2.setString(9, "TOKEN456");
	        pstmt2.setString(10, "OTP2");
	        pstmt2.setTimestamp(11, new Timestamp(System.currentTimeMillis() + 7200000)); // +2 hours
	        pstmt2.setBoolean(12, true);
	        pstmt2.executeUpdate();
	    }

	    // Call the method
	    List<Map<String, Object>> users = database.getAllUsers();

	    // Verify the results
	    assertNotNull(users, "The returned users list should not be null");
	    assertEquals(2, users.size(), "The users list should contain exactly 2 users");

	    // Verify the first user
	    Map<String, Object> user1 = users.get(0);
	    assertEquals("Alyssa", user1.get("firstName"));
	    assertEquals("Doe", user1.get("lastName"));
	    assertEquals("Johnny", user1.get("preferredName"));
	    assertEquals("alyssa.doe@example.com", user1.get("email"));
	    assertEquals("lyss", user1.get("username"));
	    assertTrue((Boolean) user1.get("isAdmin"));
	    assertFalse((Boolean) user1.get("isStudent"));
	    assertFalse((Boolean) user1.get("isInstructor"));
	    assertEquals("TOKEN123", user1.get("inviteToken"));
	    assertEquals("OTP1", user1.get("otp"));
	    assertTrue((Boolean) user1.get("otpFlag"));

	    // Verify the second user
	    Map<String, Object> user2 = users.get(1);
	    assertEquals("Jane", user2.get("firstName"));
	    assertEquals("Smith", user2.get("lastName"));
	    assertEquals("Janie", user2.get("preferredName"));
	    assertEquals("jane.smith@example.com", user2.get("email"));
	    assertEquals("janesmith", user2.get("username"));
	    assertFalse((Boolean) user2.get("isAdmin"));
	    assertTrue((Boolean) user2.get("isStudent"));
	    assertFalse((Boolean) user2.get("isInstructor"));
	    assertEquals("TOKEN456", user2.get("inviteToken"));
	    assertEquals("OTP2", user2.get("otp"));
	    assertTrue((Boolean) user2.get("otpFlag"));
	    
	    testPass+= 24;
	}


}



