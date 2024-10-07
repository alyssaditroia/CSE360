package models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***********
 * 
 *  FINISHED PAGE DO NOT EDIT
 *  
 *  
 **************/
public class TextValidation {

    public static String passwordErrorMessage = "";
    public static int passwordIndexofError = -1;

    // Helper function to print the state of input during password evaluation
    private static void displayInputState(char[] password, int currentCharNdx, char currentChar) {
        System.out.println(password);
        System.out.println("Password length: " + password.length + " | Current index: " +
                currentCharNdx + " | Current character: \"" + currentChar + "\"");
    }

    // Evaluates the password based on set criteria
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
        if (!hasUpperCase) errorMessage.append("Uppercase letter; ");
        if (!hasLowerCase) errorMessage.append("Lowercase letter; ");
        if (!hasDigit) errorMessage.append("Numeric digit; ");
        if (!hasSpecialChar) errorMessage.append("Special character; ");
        if (!isLongEnough) errorMessage.append("At least 8 characters long; ");

        if (errorMessage.length() > 0) {
            passwordIndexofError = password.length;
            return "*** Error *** The following conditions were not met: " + errorMessage.toString().trim();
        }

        return ""; // All conditions satisfied
    }

    // Checks if the password is a one-time password (OTP)
    public boolean isPasswordOTP(char[] password) {
        return password != null && password.length == 6; // OTP should be exactly 6 characters
    }

    // Validates the invite code (non-empty check)
    public boolean validateInvite(String inviteCode) {
        return inviteCode != null && !inviteCode.trim().isEmpty();
    }

    // Validates OTP (length of 6 characters)
    public boolean validateOTP(char[] otp) {
        return otp != null && otp.length == 6; // OTP must be 6 digits long
    }

    // Validates email format using regex
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

    // Validates username for length and allowed characters
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

    // Validates password by checking conditions and running evaluatePassword
    public static String validatePassword(char[] password) {
        if (password == null || password.length == 0) {
            return "*** Error *** Password cannot be empty!";
        }
        return evaluatePassword(password); // Evaluate password strength
    }

    // Validates username, password, and confirm password during setup
    public static String validateSetupFields(String username, char [] password, char[] confirmPassword) {
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

    // Validates password change
    public static String validateChangePassword(char [] password, char [] confirmPassword) {
        String passwordError = validatePassword(password);
        if (!passwordError.isEmpty()) {
            return passwordError; // Password validation failed
        }

        if (!areCharArraysEqual(password, confirmPassword)) {
            return "*** Error *** Passwords do not match!";
        }

        return ""; // All validations passed
    }
    // Method to check if two char arrays are equal
    private static boolean areCharArraysEqual(char[] array1, char[] array2) {
        if (array1 == null || array2 == null) return false;
        if (array1.length != array2.length) return false;
        for (int i = 0; i < array1.length; i++) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    // Checks if a given field is empty or invalid
    public static String isFieldEmpty(String field) {
        if(field == null || field.trim().length() < 1) {
        	return "Field can not be null";
        }
        else {
        	return "";
        }
    }

    // Validates both username and password
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

    // Placeholder for future error message display implementation
    public void displayErrorMessages() {
        // Implement UI logic for displaying error messages, if needed
    }

    public static String isFieldEmpty(char[] password) {
        // Check if the password array is null or has no characters
        if (password == null || password.length == 0) {
            return "Password field cannot be empty.";
        }
        // Return an empty string if the password is not empty
        return "";
    }
}


