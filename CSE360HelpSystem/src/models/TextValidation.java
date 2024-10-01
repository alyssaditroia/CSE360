package models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextValidation {
    public static String passwordErrorMessage = "";
    public static String passwordInput = "";
    public static int passwordIndexofError = -1;

    private static void displayInputState(String inputLine, int currentCharNdx, char currentChar) {
        System.out.println(inputLine);
        System.out.println(inputLine.substring(0, currentCharNdx) + "?");
        System.out.println("The password size: " + inputLine.length() + "  |  The currentCharNdx: " +
                currentCharNdx + "  |  The currentChar: \"" + currentChar + "\"");
    }

    public static String evaluatePassword(String input) {
        passwordErrorMessage = "";
        passwordIndexofError = 0;

        if (input.length() <= 0) return "*** Error *** The password is empty!";

        String inputLine = input;
        int currentCharNdx = 0;
        char currentChar;

        boolean foundUpperCase = false;
        boolean foundLowerCase = false;
        boolean foundNumericDigit = false;
        boolean foundSpecialChar = false;
        boolean foundLongEnough = false;

        while (currentCharNdx < inputLine.length()) {
            currentChar = inputLine.charAt(currentCharNdx);
            displayInputState(inputLine, currentCharNdx, currentChar);

            if (Character.isUpperCase(currentChar)) {
                foundUpperCase = true;
            } else if (Character.isLowerCase(currentChar)) {
                foundLowerCase = true;
            } else if (Character.isDigit(currentChar)) {
                foundNumericDigit = true;
            } else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currentChar) >= 0) {
                foundSpecialChar = true;
            } else {
                passwordIndexofError = currentCharNdx;
                return "*** Error *** An invalid character has been found!";
            }

            if (currentCharNdx >= 7) {
                foundLongEnough = true;
            }

            currentCharNdx++;
        }

        StringBuilder errMessage = new StringBuilder();
        if (!foundUpperCase) errMessage.append("Upper case; ");
        if (!foundLowerCase) errMessage.append("Lower case; ");
        if (!foundNumericDigit) errMessage.append("Numeric digits; ");
        if (!foundSpecialChar) errMessage.append("Special character; ");
        if (!foundLongEnough) errMessage.append("Long Enough; ");

        if (errMessage.length() > 0) {
            passwordIndexofError = currentCharNdx;
            return errMessage + " conditions were not satisfied.";
        }

        return ""; // All conditions satisfied
    }

    public static String validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            return "*** Error *** Invalid email format!";
        }
        return ""; // Email is valid
    }

    public static String validateUsername(String username) {
        if (username.length() < 3 || username.length() > 20) {
            return "*** Error *** Username must be between 3 and 20 characters.";
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            return "*** Error *** Username can only contain letters, digits, and underscores.";
        }
        return ""; // Username is valid
    }

    public static String validatePassword(String password) {
        String validationResult = evaluatePassword(password);
        if (!validationResult.isEmpty()) {
            return validationResult; // Return the validation error message
        }
        return ""; // Password is valid
    }

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

    public void displayErrorMessages() {
        // Implement any UI logic for displaying error messages, if needed
    }
}


