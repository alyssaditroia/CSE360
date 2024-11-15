package models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***********
 * The {@code TextValidation} model validates different text fields and formatting for text entities
 * 
 * @author Alyssa Ditroia
 * 
 **************/
public class TextValidation {

	/**
     * Stores the error message for password validation failures.
     */
	public static String passwordErrorMessage = "";
	
	/**
     * Stores the index where a password validation error occurred.
     */
	public static int passwordIndexofError = -1;

	 /**
     * Helper function to display the current state of password validation.
     * Used for debugging password evaluation process.
     *
     * @param password the password being evaluated
     * @param currentCharNdx the current character index being checked
     * @param currentChar the current character being evaluated
     */
	private static void displayInputState(char[] password, int currentCharNdx, char currentChar) {
		System.out.println(password);
		System.out.println("Password length: " + password.length + " | Current index: " + currentCharNdx
				+ " | Current character: \"" + currentChar + "\"");
	}

	 /**
     * Evaluates a password against security criteria including:
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - At least one special character
     * - Minimum length of 8 characters
     *
     * @param password the password to evaluate
     * @return empty string if password meets all criteria, error message otherwise
     */
	public static String evaluatePassword(char[] password) {
		passwordErrorMessage = "";
		passwordIndexofError = 0;

		if (password == null || password.length == 0) {
			return "*** Error *** The password is empty!";
		}

		boolean hasUpperCase = false;
		boolean hasLowerCase = false;
		boolean hasDigit = false;
		boolean hasSpecialChar = false;
		boolean isLongEnough = password.length >= 8;

		String specialChars = "~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/";

		for (int i = 0; i < password.length; i++) {
			char currentChar = password[i];
			displayInputState(password, i, currentChar);

			if (Character.isUpperCase(currentChar)) {
				hasUpperCase = true;
			} else if (Character.isLowerCase(currentChar)) {
				hasLowerCase = true;
			} else if (Character.isDigit(currentChar)) {
				hasDigit = true;
			} else if (specialChars.indexOf(currentChar) >= 0) {
				hasSpecialChar = true;
			} else {
				passwordIndexofError = i;
				return "*** Error *** Invalid character found!";
			}
		}

		StringBuilder errorMessage = new StringBuilder();
		if (!hasUpperCase)
			errorMessage.append("Uppercase letter; ");
		if (!hasLowerCase)
			errorMessage.append("Lowercase letter; ");
		if (!hasDigit)
			errorMessage.append("Numeric digit; ");
		if (!hasSpecialChar)
			errorMessage.append("Special character; ");
		if (!isLongEnough)
			errorMessage.append("At least 8 characters long; ");

		if (errorMessage.length() > 0) {
			passwordIndexofError = password.length;
			return "*** Error *** The following conditions were not met: " + errorMessage.toString().trim();
		}

		return ""; // All conditions satisfied
	}

	/**
     * Checks if the provided password is a one-time password (OTP).
     * OTP must be exactly 6 characters long.
     *
     * @param password the password to check
     * @return true if password is a valid OTP, false otherwise
     */
	public boolean isPasswordOTP(char[] password) {
		return password != null && password.length == 6; // OTP should be exactly 6 characters
	}

	/**
     * Validates an invite code.
     *
     * @param inviteCode the invite code to validate
     * @return true if invite code is not null or empty, false otherwise
     */
	public boolean validateInvite(String inviteCode) {
		return inviteCode != null && !inviteCode.trim().isEmpty();
	}

	/**
     * Validates a one-time password (OTP).
     *
     * @param otp the OTP to validate
     * @return true if OTP is exactly 6 characters long, false otherwise
     */
	public boolean validateOTP(char[] otp) {
		return otp != null && otp.length == 6; // OTP must be 6 digits long
	}

	/**
     * Validates email format using regex pattern.
     * Checks for proper email structure including @ symbol and domain.
     *
     * @param email the email address to validate
     * @return empty string if email is valid, error message otherwise
     */
	public static String validateEmail(String email) {
		if (email == null || email.isEmpty()) {
			return "*** Error *** Email cannot be empty!";
		}
		String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		Pattern pattern = Pattern.compile(emailRegex);
		Matcher matcher = pattern.matcher(email);
		if (!matcher.matches()) {
			return "*** Error *** Invalid email format!";
		}
		return ""; // Email is valid
	}

	/**
     * Validates username according to the following rules:
     * - Length between 3 and 20 characters
     * - Only letters, numbers, and underscores allowed
     * - Cannot be empty
     *
     * @param username the username to validate
     * @return empty string if username is valid, error message otherwise
     */
	public static String validateUsername(String username) {
		if (username == null || username.isEmpty()) {
			return "*** Error *** Username cannot be empty!";
		}
		if (username.length() < 3 || username.length() > 20) {
			return "*** Error *** Username must be between 3 and 20 characters.";
		}
		if (!username.matches("^[a-zA-Z0-9_]+$")) {
			return "*** Error *** Username can only contain letters, digits, and underscores.";
		}
		return ""; // Username is valid
	}

	/**
     * Validates password by running it through the evaluation process.
     *
     * @param password the password to validate
     * @return empty string if password is valid, error message otherwise
     */
	public static String validatePassword(char[] password) {
		if (password == null || password.length == 0) {
			return "*** Error *** Password cannot be empty!";
		}
		return evaluatePassword(password); // Evaluate password strength
	}

	/**
     * Validates username and password fields during setup process.
     * Also ensures password and confirmation password match.
     *
     * @param username the username to validate
     * @param password the password to validate
     * @param confirmPassword the confirmation password to check against password
     * @return empty string if all validations pass, error message otherwise
     */
	public static String validateSetupFields(String username, char[] password, char[] confirmPassword) {
		String usernameError = validateUsername(username);
		if (!usernameError.isEmpty()) {
			return usernameError; // Username validation failed
		}

		String passwordError = validatePassword(password);
		if (!passwordError.isEmpty()) {
			return passwordError; // Password validation failed
		}

		if (!areCharArraysEqual(password, confirmPassword)) {
			return "*** Error *** Passwords do not match!";
		}

		return ""; // All validations passed
	}

	/**
     * Validates password change by checking new password validity and confirmation match.
     *
     * @param password the new password to validate
     * @param confirmPassword the confirmation password to check against new password
     * @return empty string if validation passes, error message otherwise
     */
	public static String validateChangePassword(char[] password, char[] confirmPassword) {
		String passwordError = validatePassword(password);
		if (!passwordError.isEmpty()) {
			return passwordError; // Password validation failed
		}

		if (!areCharArraysEqual(password, confirmPassword)) {
			return "*** Error *** Passwords do not match!";
		}

		return ""; // All validations passed
	}

	/**
     * Compares two char arrays for equality.
     * Used primarily for password confirmation matching.
     *
     * @param array1 first array to compare
     * @param array2 second array to compare
     * @return true if arrays are equal in length and content, false otherwise
     */
	private static boolean areCharArraysEqual(char[] array1, char[] array2) {
		if (array1 == null || array2 == null)
			return false;
		if (array1.length != array2.length)
			return false;
		for (int i = 0; i < array1.length; i++) {
			if (array1[i] != array2[i]) {
				return false;
			}
		}
		return true;
	}

	/**
     * Checks if a string field is empty or null.
     *
     * @param field the string to check
     * @return error message if field is empty or null, empty string otherwise
     */
	public static String isFieldEmpty(String field) {
		if (field == null || field.trim().length() < 1) {
			return "Field can not be null";
		} else {
			return "";
		}
	}

	/**
     * Validates both username and password at once.
     *
     * @param username the username to validate
     * @param password the password to validate
     * @return empty string if both are valid, error message otherwise
     */
	public static String textValidation(String username, char[] password) {
		String usernameError = validateUsername(username);
		if (!usernameError.isEmpty()) {
			return usernameError; // Username is invalid
		}

		String passwordError = validatePassword(password);
		if (!passwordError.isEmpty()) {
			return passwordError; // Password is invalid
		}

		return ""; // Both username and password are valid
	}

	/**
     * Placeholder method for future error message display implementation.
     */
	public void displayErrorMessages() {

	}

	/**
     * Checks if a password char array is empty or null.
     *
     * @param password the password array to check
     * @return error message if password is empty or null, empty string otherwise
     */
	public static String isFieldEmpty(char[] password) {
		// Check if the password array is null or has no characters
		if (password == null || password.length == 0) {
			return "Password field cannot be empty.";
		}
		// Return an empty string if the password is not empty
		return "";
	}
}
