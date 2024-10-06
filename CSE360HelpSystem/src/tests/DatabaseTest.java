package tests;
import database.Database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;

import org.junit.jupiter.api.*;

public class DatabaseTest {
    private Database database;
    private static int passedTestCases = 0;  // Static variable to track passed tests


    @BeforeEach
    public void setUp() throws SQLException {
    	database = Database.getInstance();
        database.connectToDatabase(); // Initialize test database
    }

    @AfterEach
    public void tearDown() {
        database.closeConnection(); // Close the connection after each test
        System.out.println("Test passed. Current number of passed cases: " + passedTestCases);

    }
    @Test
    public void testConnectToDatabase() throws Exception {
        Database db = Database.getInstance();
        db.connectToDatabase();
        assertNotNull(db.getConnection());  // Assuming a valid connection should be non-null

        // If the assertion passes, count it as a passed test case
        passedTestCases++;
    }

    @Test
    public void testIsDatabaseEmpty() throws SQLException {
        Database db = Database.getInstance();
        db.connectToDatabase();
        boolean isEmpty = db.isDatabaseEmpty();
        assertTrue(isEmpty);  // Assuming database is empty

        // Increment pass count if assertion passes
        passedTestCases++;
    }
    @Test
    public void testUpdateUser() throws SQLException {
    	 Database db = Database.getInstance();
         db.connectToDatabase();
         char [] testPassword = "password123!".toCharArray();
        db.setupAdministrator("adminUser", testPassword);
        db.updateUser("adminUser", "John", "Doe", "JD", "john123@example.com");

        assertEquals("John", db.getFirstName("adminUser"), "First name should be updated to John");
    }

    @Test
    public void testValidateCredentials() throws SQLException {
    	 Database db = Database.getInstance();
         db.connectToDatabase();
         char [] testPassword = "password123!".toCharArray();
        db.setupAdministrator("adminUser", testPassword);

        assertTrue(db.validateCredentials("adminUser", testPassword),
            "Credentials should match for valid user");
        char [] wrongTestPassword = "wrongPassword".toCharArray();
        assertFalse(db.validateCredentials("adminUser", wrongTestPassword),
            "Credentials should not match for wrong password");
    }

    @Test
    public void testInviteUser() throws SQLException {
    	 Database db = Database.getInstance();
         db.connectToDatabase();
        String inviteCode = db.inviteUser("inviteCode123", "invite1@example.com", true, false, false);

        assertTrue(db.validateInvite(inviteCode), "Invite should be valid");
    }

    @Test
    public void testCompleteInvite() throws SQLException {
    	 Database db = Database.getInstance();
         db.connectToDatabase();
         char [] testPassword = "password123!".toCharArray();
        String inviteCode = db.inviteUser("inviteCode123", "invite@example.com", true, false, false);
        boolean inviteCompleted = db.completeInvite(inviteCode, "invitedUser", testPassword);

        assertTrue(inviteCompleted, "Invite should be completed successfully");
        assertTrue(db.validateCredentials("invitedUser", testPassword),
            "Credentials should be valid after invite completion");
    }

    @Test
    public void testUpdatePassword() throws SQLException {
    	 Database db = Database.getInstance();
         db.connectToDatabase();
         char [] newTestPassword = "newPassword2!".toCharArray();
        db.setupAdministrator("adminUser", newTestPassword);
        assertTrue(db.updatePassword("adminUser", newTestPassword), "Password should be updated");

        assertTrue(db.validateCredentials("adminUser", newTestPassword), 
            "Credentials should be valid after password update");
    }

    @Test
    public void testIsUserAdmin() throws SQLException {
    	 Database db = Database.getInstance();
         db.connectToDatabase();
         char [] testPassword = "password123!".toCharArray();

        db.setupAdministrator("adminUser", testPassword);

        assertTrue(db.isUserAdmin("adminUser"), "adminUser should have admin privileges");
    }

    // Add more tests as needed
}
