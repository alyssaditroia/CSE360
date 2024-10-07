package tests;

import org.junit.jupiter.api.Test;

import models.TextValidation;

import static org.junit.jupiter.api.Assertions.*;

class TextValidationTest {

    @Test
    void testEvaluatePassword_EmptyPassword() {
    	char [] testPass = "".toCharArray();
        String result = TextValidation.evaluatePassword(testPass);
        assertEquals("*** Error *** The password is empty!", result);
    }

    @Test
    void testEvaluatePassword_NullPassword() {
        String result = TextValidation.evaluatePassword(null);
        assertEquals("*** Error *** The password is empty!", result);
    }

    @Test
    void testEvaluatePassword_ValidPassword() {
    	String test = "Valid1!0";
    	char [] testPass = test.toCharArray();
        String result = TextValidation.evaluatePassword(testPass);
        assertEquals("", result);
    }

    @Test
    void testEvaluatePassword_InvalidPassword_NoUppercase() {
    	char [] testPass = "invalid1!".toCharArray();
        String result = TextValidation.evaluatePassword(testPass);
        assertEquals("*** Error *** The following conditions were not met: Uppercase letter;", result);
    }

    @Test
    void testEvaluatePassword_InvalidPassword_NoLowercase() {
    	char [] testPass = "INVALID1!".toCharArray();
        String result = TextValidation.evaluatePassword(testPass);
        assertEquals("*** Error *** The following conditions were not met: Lowercase letter;", result);
    }

    @Test
    void testEvaluatePassword_InvalidPassword_NoDigit() {
    	char [] testPass = "InvalidPassword!".toCharArray();
        String result = TextValidation.evaluatePassword(testPass);
        assertEquals("*** Error *** The following conditions were not met: Numeric digit;", result);
    }

    @Test
    void testEvaluatePassword_InvalidPassword_NoSpecialChar() {
    	char [] testPass = "Invalid1".toCharArray();
        String result = TextValidation.evaluatePassword(testPass);
        assertEquals("*** Error *** The following conditions were not met: Special character;", result);
    }

    @Test
    void testValidateUsername_EmptyUsername() {
        String result = TextValidation.validateUsername("");
        assertEquals("*** Error *** Username cannot be empty!", result);
    }

    @Test
    void testValidateUsername_TooShort() {
        String result = TextValidation.validateUsername("ab");
        assertEquals("*** Error *** Username must be between 3 and 20 characters.", result);
    }

    @Test
    void testValidateUsername_TooLong() {
        String result = TextValidation.validateUsername("a_very_long_username!!!!!!!");
        assertEquals("*** Error *** Username must be between 3 and 20 characters.", result);
    }

    @Test
    void testValidateUsername_InvalidCharacters() {
        String result = TextValidation.validateUsername("Invalid@Username");
        assertEquals("*** Error *** Username can only contain letters, digits, and underscores.", result);
    }

    @Test
    void testValidateUsername_ValidUsername() {
        String result = TextValidation.validateUsername("Valid_Username123");
        assertEquals("", result);
    }

    @Test
    void testValidateEmail_EmptyEmail() {
        String result = TextValidation.validateEmail("");
        assertEquals("*** Error *** Email cannot be empty!", result);
    }

    @Test
    void testValidateEmail_InvalidEmailFormat() {
        String result = TextValidation.validateEmail("invalid-email");
        assertEquals("*** Error *** Invalid email format!", result);
    }

    @Test
    void testValidateEmail_ValidEmail() {
        String result = TextValidation.validateEmail("valid.email@example.com");
        assertEquals("", result);
    }

    @Test
    void testValidateSetupFields_ValidInput() {
    	char [] testPass = "Valid1!0".toCharArray();
        String result = TextValidation.validateSetupFields("Valid_User123", testPass, testPass);
        assertEquals("", result);
    }

    @Test
    void testValidateSetupFields_UsernameError() {
    	char [] testPass = "Valid1!0".toCharArray();
        String result = TextValidation.validateSetupFields("ab", testPass, testPass);
        assertEquals("*** Error *** Username must be between 3 and 20 characters.", result);
    }

    @Test
    void testValidateSetupFields_PasswordError() {
    	char [] testPass = "invalid".toCharArray();
        String result = TextValidation.validateSetupFields("Valid_User123", testPass, testPass);
        assertEquals("*** Error *** The following conditions were not met: Uppercase letter; Numeric digit; Special character; At least 8 characters long;", result);
    }

    @Test
    void testValidateChangePassword_MismatchedPasswords() {
    	char [] testPass = "Valid1!0".toCharArray();
    	char [] testPass2 = "Different1!".toCharArray();
        String result = TextValidation.validateChangePassword(testPass, testPass2);
        assertEquals("*** Error *** Passwords do not match!", result);
    }

    @Test
    void testIsFieldEmpty_NullField() {
        String result = TextValidation.isFieldEmpty("");
        assertEquals("Field can not be null", result);
    }

    @Test
    void testIsFieldEmpty_EmptyField() {
        String result = TextValidation.isFieldEmpty("  ");
        assertEquals("Field can not be null", result);
    }

    @Test
    void testIsFieldEmpty_ValidField() {
        String result = TextValidation.isFieldEmpty("Valid Field");
        assertEquals("", result);
    }
}
