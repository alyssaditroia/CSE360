/**
 * 
 */
package models;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import database.Database;

import java.util.Random;


/**
 * OneTimePassword Class
 */
public class OTP {
    private String OTP;
    private LocalDateTime expiration; 
    private boolean flag;
    private Database db;
    private Random random = new Random();

    /**
     * Constructor to initialize the database
     * @param db
     */
    public OTP(Database db) {
        this.db = db;
    }

    /**
     * Generates a random OTP, sets the expiration time, and saves it to the database for the specified user
     * @param username The username to associate the OTP with
     * @throws SQLException
     */
    public void generateAndSaveOTP(String username) throws SQLException {
        // Generate a random 6-digit OTP
        this.OTP = String.valueOf(100000 + random.nextInt(900000));
        this.expiration = LocalDateTime.now().plusMinutes(10);  // Set expiration time to 10 minutes from now
        this.flag = true;  // Set the OTP flag to true
        
        // SQL query to update OTP, expiration, and flag for the specified user
        String query = "UPDATE cse360users SET otp = ?, otpExpiration = ?, otpFlag = ? WHERE username = ?";
        
        try (PreparedStatement pstmt = db.getConnection().prepareStatement(query)) {
            pstmt.setString(1, OTP);
            pstmt.setString(2, expiration.format(DateTimeFormatter.ISO_DATE_TIME));
            pstmt.setBoolean(3, flag);
            pstmt.setString(4, username);
            pstmt.executeUpdate();
        }
    }

    /**
     * Method to send OTP to the user's email (placeholder for actual email sending logic)
     * @param email The user's email address
     * @param otp The generated OTP to send
     */
    public void sendOTP(String email, String otp) {
        // Implement email sending logic here
        System.out.println("Sending OTP " + otp + " to " + email);
    }

    /**
     * Validates if the OTP is correct, not expired, and the flag is still active
     * @param username The username to validate the OTP for
     * @param inputOTP The OTP provided by the user
     * @return True if the OTP is valid, false otherwise
     * @throws SQLException
     */
    public boolean validateOTP(String username, String inputOTP) throws SQLException {
        String query = "SELECT otp, otpExpiration, otpFlag FROM cse360users WHERE username = ?";
        try (PreparedStatement pstmt = db.getConnection().prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedOTP = rs.getString("otp");
                LocalDateTime storedExpiration = LocalDateTime.parse(rs.getString("otpExpiration"));
                boolean storedFlag = rs.getBoolean("otpFlag");

                // Check if OTP matches, has not expired, and flag is still true
                if (storedFlag && storedOTP.equals(inputOTP) && LocalDateTime.now().isBefore(storedExpiration)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets the OTP flag to false after successful OTP validation or expiration
     * @param username The username to invalidate the OTP for
     * @throws SQLException
     */
    public void invalidateOTP(String username) throws SQLException {
        String query = "UPDATE cse360users SET otpFlag = ? WHERE username = ?";
        try (PreparedStatement pstmt = db.getConnection().prepareStatement(query)) {
            pstmt.setBoolean(1, false);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        }
    }
}

