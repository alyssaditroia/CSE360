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
    private static void displayInputState(String inputLine, int currentCharNdx, char currentChar) {
        System.out.println(inputLine);
        System.out.println(inputLine.substring(0, currentCharNdx) + "?");
        System.out.println("Password length: " + inputLine.length() + " | Current index: " +
                currentCharNdx + " | Current character: \"" + currentChar + "\"");
    }

    // Evaluates the password based on set criteria
    public static String evaluatePassword(String input) {
        passwordErrorMessage = "";
        passwordIndexofError = 0;

        if (input == null || input.isEmpty()) {
            return "*** Error *** The password is empty!";
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        boolean isLongEnough = input.length() >= 8;

        String specialChars = "~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/";

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            displayInputState(input, i, currentChar);

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
            passwordIndexofError = input.length();
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
    public boolean validateOTP(String otp) {
        return otp != null && otp.length() == 6; // OTP must be 6 digits long
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
    public static String validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return "*** Error *** Password cannot be empty!";
        }
        return evaluatePassword(password); // Evaluate password strength
    }

    // Validates username, password, and confirm password during setup
    public static String validateSetupFields(String username, String password, String confirmPassword) {
        String usernameError = validateUsername(username);
        if (!usernameError.isEmpty()) {
            return usernameError; // Username validation failed
        }

        String passwordError = validatePassword(password);
        if (!passwordError.isEmpty()) {
            return passwordError; // Password validation failed
        }

        if (!password.equals(confirmPassword)) {
            return "*** Error *** Passwords do not match!";
        }

        return ""; // All validations passed
    }

    // Validates password change
    public static String validateChangePassword(String password, String confirmPassword) {
        String passwordError = validatePassword(password);
        if (!passwordError.isEmpty()) {
            return passwordError; // Password validation failed
        }

        if (!password.equals(confirmPassword)) {
            return "*** Error *** Passwords do not match!";
        }

        return ""; // All validations passed
    }

    // Checks if a given field is empty or invalid
    public boolean isFieldEmpty(String field) {
        return field == null || field.trim().length() < 2;
    }

    // Validates both username and password
    public static String textValidation(String username, char[] password) {
        String usernameError = validateUsername(username);
        if (!usernameError.isEmpty()) {
            return usernameError; // Username is invalid
        }

        String passwordError = validatePassword(new String(password));
        if (!passwordError.isEmpty()) {
            return passwordError; // Password is invalid
        }

        return ""; // Both username and password are valid
    }

    // Placeholder for future error message display implementation
    public void displayErrorMessages() {
        // Implement UI logic for displaying error messages, if needed
    }
}


