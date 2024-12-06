package tests;

import static org.junit.jupiter.api.Assertions.*;

import database.Database;
import models.OTP;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
/**
 * <p> Title: OTPTest </p>
 * Tests the OTP Class
 * 
 *  @author Cooper Anderson
 */
class OTPTest {
    private OTP otp;
    private Database db;
    private String testEmail = "test@example.com";
    private static int testcount = 1;
    private static int testPass = 0;

    @BeforeEach
    void setUp() throws SQLException {
    	// Console output for JUnit test
		System.out.println("\n =========== OTP TEST ===========\n");
		System.out.printf("Test Group # %d%n", testcount++);
		
		// Database connection for test database
        db = Database.getInstance();
        db.connectToDatabase();

        // Creating an OTP object
        otp = new OTP();

        // Insert a test user into the database
        String insertUserSQL = "INSERT INTO cse360users (firstName, lastName, preferredName, email, username, password) "
                + "VALUES ('Test', 'User', 'Tester', '" + testEmail + "', 'testUser', 'password')";
        db.getConnection().createStatement().execute(insertUserSQL);
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Clean up the test database after each test
        String deleteUserSQL = "DELETE FROM cse360users WHERE email = '" + testEmail + "'";
        db.getConnection().createStatement().execute(deleteUserSQL);
        db.closeConnection();
    }
	@AfterAll
	static void afterAll() {
		System.out.println("\n =========== OTP TESTS COMPLETE ===========\n");
    	System.out.println("\nTOTAL TESTS: " + testPass + "\nTESTS PASSED: " + testPass + "\n");
	}

    @Test
    void testGenerateAndSaveOTP() throws SQLException {
    	System.out.println("\nTesting Generate and Save OTP");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 3\n");
        otp.generateAndSaveOTP(testEmail);

        // Verify that the OTP was saved correctly
        String query = "SELECT otp FROM cse360users WHERE email = ?";
        try (var pstmt = db.getConnection().prepareStatement(query)) {
            pstmt.setString(1, testEmail);
            var rs = pstmt.executeQuery();
            assertTrue(rs.next(), "User should exist in the database.");
            String storedOTP = rs.getString("otp");
            assertNotNull(storedOTP, "OTP should not be null.");
            assertEquals(6, storedOTP.length(), "OTP should be 6 digits long.");
            testPass+= 3; 
        }
    }

    @Test
    void testValidateOTP_Success() throws SQLException {
    	System.out.println("\nTesting Validate Valid OTP");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        otp.generateAndSaveOTP(testEmail);

        // Fetch the generated OTP from the database
        String query = "SELECT otp FROM cse360users WHERE email = ?";
        String storedOTP = null;
        try (var pstmt = db.getConnection().prepareStatement(query)) {
            pstmt.setString(1, testEmail);
            var rs = pstmt.executeQuery();
            assertTrue(rs.next(), "User should exist in the database.");
            storedOTP = rs.getString("otp");
        }

        // Validate the OTP
        String validationResult = otp.validateOTP("testUser", storedOTP.toCharArray());
        assertEquals("", validationResult, "Validation should succeed for the correct OTP.");
        testPass+= 2;
    }

    @Test
    void testValidateOTP_InvalidOTP() throws SQLException {
    	System.out.println("\nTesting Validate Invalid OTP");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
        otp.generateAndSaveOTP(testEmail);

        // Validate an incorrect OTP
        String validationResult = otp.validateOTP("testUser", "wrongOTP".toCharArray());
        assertEquals("Invalid One Time Password.", validationResult, "Validation should fail for an incorrect OTP.");
        testPass+= 1; 
    }

    @Test
    void testValidateOTP_ExpiredOTP() throws SQLException {
    	System.out.println("\nTesting Validation of Expired OTP");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
        otp.generateAndSaveOTP(testEmail);

        // Simulate expiration by setting the expiration time to the past
        String updateSQL = "UPDATE cse360users SET otpExpiration = ? WHERE email = ?";
        try (var pstmt = db.getConnection().prepareStatement(updateSQL)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().minusDays(1))); // Set to 1 day ago
            pstmt.setString(2, testEmail);
            pstmt.executeUpdate();
        }

        // Validate the OTP
        String validationResult = otp.validateOTP("testUser", "123456".toCharArray()); // Use the OTP generated
        assertEquals("Invalid One Time Password.", validationResult, "Validation should fail for expired OTP.");
        testPass+= 1; 
    }

    
    @Test
    void testValidateOTP_NullUsername() throws SQLException {
    	System.out.println("\nTesting Validation of OTP With Invalid Username");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
        String validationResult = otp.validateOTP(null, "123456".toCharArray());
        assertEquals("Fields cannot be empty.", validationResult, "Validation should fail for null username.");
        testPass++; 
    }

    @Test
    void testValidateOTP_NullPassword() throws SQLException {
    	System.out.println("\nTesting Validation of OTP With Invalid Password");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
        String validationResult;
		validationResult = otp.validateOTP("testUser", null);
		
        assertEquals("Fields cannot be empty.", validationResult, "Validation should fail for null password.");
        testPass++;
    }

    @Test
    void testInvalidateOTP() throws SQLException {
    	System.out.println("\nTesting Invalidation of OTP After Use");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 2\n");
        otp.generateAndSaveOTP(testEmail);

        // Invalidate the OTP
        otp.invalidateOTP("testUser");

        // Verify that the OTP is invalidated in the database
        String query = "SELECT otpFlag FROM cse360users WHERE email = ?";
        try (var pstmt = db.getConnection().prepareStatement(query)) {
            pstmt.setString(1, testEmail);
            var rs = pstmt.executeQuery();
            assertTrue(rs.next(), "User should exist in the database.");
            assertFalse(rs.getBoolean("otpFlag"), "OTP flag should be false after invalidation.");
            testPass+= 2; 
        }
    }
}


