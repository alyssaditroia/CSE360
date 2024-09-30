package models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*****
 * Focuses on application/business logic and data handling.
 * Doesn't directly deal with the UI.
 * It processes data and provides results that can be used by the controller to update the UI.
 */
public class TextValidation {
    // Method to validate email format
    public boolean validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Example TextValidation method for validating username and password
    public boolean textValidation(String username, String password) {
        try {
			return validateUsername(username) && validatePassword(password);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return false;
    }

    // Validate username (modify conditions as needed)
    private boolean validateUsername(String username) {
        // Example conditions: Username must be between 3 and 20 characters and can include letters, numbers, and underscores
        if (username.length() < 3 || username.length() > 20) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_]+$");
    }

    // Validate password
    public boolean validatePassword(String password) {
        // Example validation logic: Minimum length 8 characters, at least one uppercase letter, one lowercase letter, one digit, and one special character
        if (password.length() < 8) {
            return false;
        }
        if (!password.matches(".*[A-Z].*")) {
            return false; // At least one uppercase letter
        }
        if (!password.matches(".*[a-z].*")) {
            return false; // At least one lowercase letter
        }
        if (!password.matches(".*\\d.*")) {
            return false; // At least one digit
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false; // At least one special character
        }
        return true; // If all conditions are met
    }
    private boolean isPasswordValid(char[] password) {
        String passwordStr = new String(password); // Convert char[] to String

        // Password must be at least 8 characters long and include:
        // - At least one uppercase letter
        // - At least one lowercase letter
        // - At least one digit
        // - At least one special character
        if (passwordStr.length() < 8 ||
            !passwordStr.matches(".*[A-Z].*") ||
            !passwordStr.matches(".*[a-z].*") ||
            !passwordStr.matches(".*\\d.*") ||
            !passwordStr.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false;
        }

        return true; // Password meets all criteria
    }

    public void displayErrorMessages() {
        // Implement any UI logic for displaying error messages, if needed
    }

	public boolean textValidation(String username, char[] password) {
		// Validate username
	    if (!validateUsername(username)) {
	        return false; // Username is invalid
	    }

	    // Validate password
	    if (!isPasswordValid(password)) {
	        return false; // Password is invalid
	    }

	    return true; // Both username and password are valid
	}
}
