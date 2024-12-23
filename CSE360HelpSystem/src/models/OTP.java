package models;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import database.Database;

import java.util.Random;

import Encryption.EncryptionHelper;

/**
 * The {@code OTP} class represents the one time password and handles OTP operations
 * The OTP class also interacts with the database for updating and handling OTPs
 * 
 * @author Alyssa DiTroia
 * @author Justin Faris
 */
public class OTP {
	/**
     * The one-time password stored as a character array for security.
     */
	private char[] OTP;
	
	/**
     * The expiration timestamp for the current OTP.
     * After this time, the OTP becomes invalid.
     */
	private LocalDateTime expiration;
	
	/**
     * Flag indicating whether the OTP is currently valid.
     * True indicates the OTP is active and can be used, false indicates it has been used or invalidated.
     */
	private boolean flag;
	
	/**
     * Database instance for managing OTP data persistence.
     */
	private Database db;
	
	/**
     * Helper instance for encrypting and decrypting OTP data.
     */
	private EncryptionHelper encryptionHelper;
	
	/**
     * Random number generator for OTP creation.
     * Used to generate secure random values for one-time passwords.
     */
	private Random random = new Random();

	/**
	 * Constructor to initialize the database
	 * 
	 */
	public OTP() {
		db = Database.getInstance();
		try {
			encryptionHelper = new EncryptionHelper();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates a random OTP, sets the expiration time, and saves it to the
	 * database for the specified user
	 * 
	 * @param email The username to associate the OTP with
	 * @throws SQLException
	 */
	// TODO Encrypt OTP
	public void generateAndSaveOTP(String email) throws SQLException {
		// Generate a random 6-digit OTP
		this.OTP = String.valueOf(100000 + random.nextInt(900000)).toCharArray();
		this.expiration = LocalDateTime.now().plusDays(10); // Set expiration time to 10 days from now
		this.flag = true; // Set the OTP flag to true
		System.out.println("OTP Generated for User: " + email + " OTP: " + String.valueOf(this.OTP));

		// SQL query to update OTP, expiration, and flag for the specified user
		String query = "UPDATE cse360users SET otp = ?, otpExpiration = ?, otpFlag = ?, password = ? WHERE email = ?";

		try (PreparedStatement pstmt = db.getConnection().prepareStatement(query)) {
			pstmt.setString(1, String.valueOf(OTP));
			pstmt.setString(2, expiration.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))); // Formatting
																										// expiration
			pstmt.setBoolean(3, flag);
			pstmt.setString(4, String.valueOf(OTP)); // Assuming you're saving the OTP as the password, which is not
														// recommended for security reasons
			pstmt.setString(5, email);
			pstmt.executeUpdate();
		}
	}

	/**
	 * Validates if the OTP is correct, not expired, and the flag is still active
	 * 
	 * @param username The username to validate the OTP for
	 * @param password The OTP provided by the user
	 * @return Error message if validation fails, or an empty string if valid
	 * @throws SQLException
	 */
	// TODO Decrypt OTP 
	public String validateOTP(String username, char[] password) throws SQLException {
		// Check if username or inputOTP is null
		if (username == null || password == null) {
			return "Fields cannot be empty.";
		}

		String query = "SELECT otp, otpExpiration, otpFlag FROM cse360users WHERE username = ?";
		try (PreparedStatement pstmt = db.getConnection().prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String storedOTP = rs.getString("otp");
				String expirationString = rs.getString("otpExpiration");

				// Check if the input OTP matches the stored OTP
				if (storedOTP != null && storedOTP.equals(String.valueOf(password))) {
					// Check if the expiration date is not expired
					if (expirationString != null) {
						// Get the current time in the same format
						LocalDateTime now = LocalDateTime.now();
						LocalDateTime expirationTime = LocalDateTime.parse(expirationString,
								DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

						// Compare the current time with the expiration time
						if (now.isBefore(expirationTime)) {
							return ""; // OTP is valid
						} else {
							return "One Time Password has expired.";
						}
					}
				} else {
					return "Invalid One Time Password.";
				}
			} else {
				return "User not found.";
			}
		}
		return "ERROR??";
	}

	/**
	 * Sets the OTP flag to false after successful OTP validation or expiration
	 * 
	 * @param username The username to invalidate the OTP for
	 * @throws SQLException
	 */
	public void invalidateOTP(String username) throws SQLException {
		String query = "UPDATE cse360users SET otpFlag = ?, otpExpiration = ?, otp = ? WHERE username = ?";
		try (PreparedStatement pstmt = db.getConnection().prepareStatement(query)) {
			pstmt.setBoolean(1, false);
			pstmt.setString(2, null);
			pstmt.setString(3, null);
			pstmt.setString(4, username);
			pstmt.executeUpdate();
		}
	}
}

