package tests;

import static org.junit.jupiter.api.Assertions.*;

import database.Database;
import models.OTP;

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
    private static int testcount = 0;

    @BeforeEach
    void setUp() throws SQLException {
		System.out.println("\n =========== OTP TEST ===========\n");
		testcount = testcount + 1;
		System.out.printf("Test # %d%n", testcount);
        db = Database.getInstance();
        db.connectToDatabase();

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

    @Test
    void testGenerateAndSaveOTP() throws SQLException {
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
        }
    }

    @Test
    void testValidateOTP_Success() throws SQLException {
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
    }

    @Test
    void testValidateOTP_InvalidOTP() throws SQLException {
        otp.generateAndSaveOTP(testEmail);

        // Validate an incorrect OTP
        String validationResult = otp.validateOTP("testUser", "wrongOTP".toCharArray());
        assertEquals("Invalid One Time Password.", validationResult, "Validation should fail for an incorrect OTP.");
    }

    @Test
    void testValidateOTP_ExpiredOTP() throws SQLException {
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
    }

    @Test
    void testValidateOTP_NullUsername() throws SQLException {
        String validationResult = otp.validateOTP(null, "123456".toCharArray());
        assertEquals("Fields cannot be empty.", validationResult, "Validation should fail for null username.");
    }

    @Test
    void testValidateOTP_NullPassword() throws SQLException {
        String validationResult;
		validationResult = otp.validateOTP("testUser", null);
		
        assertEquals("Fields cannot be empty.", validationResult, "Validation should fail for null password.");
    }

    @Test
    void testInvalidateOTP() throws SQLException {
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
        }
    }
}


