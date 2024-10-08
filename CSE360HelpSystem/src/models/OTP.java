/**
 * 
 */
package models;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import database.Database;

import java.util.Random;

/**
 * OneTimePassword Class
 * 
 * @author Alyssa diTroia
 */
public class OTP {
	private char[] OTP;
	private LocalDateTime expiration;
	private boolean flag;
	private Database db;
	private Random random = new Random();

	/**
	 * Constructor to initialize the database
	 * 
	 * @param db
	 */
	public OTP() {
		db = Database.getInstance();
	}

	/**
	 * Generates a random OTP, sets the expiration time, and saves it to the
	 * database for the specified user
	 * 
	 * @param email The username to associate the OTP with
	 * @throws SQLException
	 */
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

