package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import database.Database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

class DatabaseTest {
	private Database database;
	private Connection connection;

	@BeforeEach
	void setUp() throws SQLException {
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
				connection.close(); // Close the connection after each test
			}
		} catch (SQLException e) {
			System.err.println("[DatabaseTest] Error closing the connection: " + e.getMessage());
		}
	}

	@AfterAll
	static void afterAll() {
		deleteTestDatabase(); // Clean up the test database after all tests
	}

	private void clearTestDatabase() throws SQLException {
		// Clear the database (truncate or delete rows) before each test
		try (Statement stmt = connection.createStatement()) {
			stmt.execute("TRUNCATE TABLE cse360users"); // Example for a 'users' table
			System.out.println("[DatabaseTest] Test database cleared.");
		} catch (SQLException e) {
			System.err.println("[DatabaseTest] Error clearing test database: " + e.getMessage());
		}
	}

	private static void deleteTestDatabase() {
		String url = "jdbc:h2:~/firstsDatabase";
		String user = "user"; // Default user for H2
		String password = ""; // Default password for H2 (empty)

		try (Connection conn = DriverManager.getConnection(url, user, password);
				Statement stmt = conn.createStatement()) {
			stmt.execute("DROP ALL OBJECTS");
			System.out.println("[DatabaseTest] Test database deleted successfully.");
		} catch (SQLException e) {
			System.err.println("[DatabaseTest] Error deleting test database: " + e.getMessage());
		}
	}

	@Test
	void testConnectToDatabase() throws SQLException {
		assertNotNull(database.getConnection(), "Connection should not be null after connecting to the database");
	}

	@Test
	void testIsDatabaseEmptyInitially() throws SQLException {
		assertTrue(database.isDatabaseEmpty(), "Database should be empty ");
	}

	@Test
	void testSetupAdministrator() throws SQLException {
		String username = "admin";
		char[] password = "Password1!".toCharArray();
		database.setupAdministrator(username, password);

		assertFalse(database.isDatabaseEmpty(), "Database should not be empty after adding an administrator");
		assertTrue(database.isUserAdmin(username), "User should be an admin after setup");
	}

	@Test
	void testUpdateUSer() throws SQLException {
		String username = "admin";
		char[] password = "Password1!".toCharArray();
		database.setupAdministrator(username, password);
		database.updateUser(username, "admin", "admin", "", "admin@eamail.com");
		assertTrue(database.doesUserExist("admin@eamail.com"), "User should be an admin after setup");
	}

	@Test
	void testInviteUser() throws SQLException {
		String inviteCode = "INVITE123";
		String email = "invitee@example.com";
		database.inviteUser(inviteCode, email, true, false, false);

		assertFalse(database.isDatabaseEmpty(), "Database should not be empty after inviting a user");
		assertTrue(database.doesUserExist(email), "User should exist after inviting");
	}

	@Test
	void testCompleteInvite() throws SQLException {
		String inviteCode = "INVITE123";
		String email = "invitee@example.com";
		database.inviteUser(inviteCode, email, true, false, false);

		boolean result = database.completeInvite(inviteCode, "newUser", "newPass".toCharArray());
		assertTrue(result, "Invite should be completed successfully");
		assertTrue(database.isUserAdmin("newUser"), "New user should be an admin after completing invite");
	}

	@Test
	void testIsUserAdmin() throws SQLException {
		String username = "admin";
		char[] password = "Password1!".toCharArray();
		database.setupAdministrator(username, password);

		assertTrue(database.isUserAdmin(username), "User should have admin privileges");
		assertFalse(database.isUserAdmin("nonExistentUser"), "Non-existent user should not have admin privileges");
	}
}



