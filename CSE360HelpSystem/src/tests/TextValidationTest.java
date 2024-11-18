package tests;

import org.junit.jupiter.api.Test;

import models.TextValidation;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Junit test cases for the Text Validation class
 */
class TextValidationTest {

	/**
	 * Tests empty password check
	 */
    @Test
    void testEvaluatePassword_EmptyPassword() {
    	char [] testPass = "".toCharArray();
        String result = TextValidation.evaluatePassword(testPass);
        assertEquals("*** Error *** The password is empty!", result);
    }

	/**
	 * Tests null password check
	 */
    @Test
    void testEvaluatePassword_NullPassword() {
        String result = TextValidation.evaluatePassword(null);
        assertEquals("*** Error *** The password is empty!", result);
    }

	/**
	 * Tests valid password check
	 */
    @Test
    void testEvaluatePassword_ValidPassword() {
    	String test = "Valid1!0";
    	char [] testPass = test.toCharArray();
        String result = TextValidation.evaluatePassword(testPass);
        assertEquals("", result);
    }

	/**
	 * Tests invalid password check for no uppercase letters
	 */
    @Test
    void testEvaluatePassword_InvalidPassword_NoUppercase() {
    	char [] testPass = "invalid1!".toCharArray();
        String result = TextValidation.evaluatePassword(testPass);
        assertEquals("*** Error *** The following conditions were not met: Uppercase letter;", result);
    }

	/**
	 * Tests invalid password check for no lowercase letters 
	 */
    @Test
    void testEvaluatePassword_InvalidPassword_NoLowercase() {
    	char [] testPass = "INVALID1!".toCharArray();
        String result = TextValidation.evaluatePassword(testPass);
        assertEquals("*** Error *** The following conditions were not met: Lowercase letter;", result);
    }

	/**
	 * Tests invalid password check for no digits
	 */
    @Test
    void testEvaluatePassword_InvalidPassword_NoDigit() {
    	char [] testPass = "InvalidPassword!".toCharArray();
        String result = TextValidation.evaluatePassword(testPass);
        assertEquals("*** Error *** The following conditions were not met: Numeric digit;", result);
    }

	/**
	 * Tests invalid password check for no special characters
	 */
    @Test
    void testEvaluatePassword_InvalidPassword_NoSpecialChar() {
    	char [] testPass = "Invalid1".toCharArray();
        String result = TextValidation.evaluatePassword(testPass);
        assertEquals("*** Error *** The following conditions were not met: Special character;", result);
    }

	/**
	 * Tests empty username check
	 */
    @Test
    void testValidateUsername_EmptyUsername() {
        String result = TextValidation.validateUsername("");
        assertEquals("*** Error *** Username cannot be empty!", result);
    }

	/**
	 * Tests if the username is too short function
	 */
    @Test
    void testValidateUsername_TooShort() {
        String result = TextValidation.validateUsername("ab");
        assertEquals("*** Error *** Username must be between 3 and 20 characters.", result);
    }

	/**
	 * Tests if the username is too long function
	 */
    @Test
    void testValidateUsername_TooLong() {
        String result = TextValidation.validateUsername("a_very_long_username!!!!!!!");
        assertEquals("*** Error *** Username must be between 3 and 20 characters.", result);
    }

	/**
	 * Tests if the username contains invalid characters
	 */
    @Test
    void testValidateUsername_InvalidCharacters() {
        String result = TextValidation.validateUsername("Invalid@Username");
        assertEquals("*** Error *** Username can only contain letters, digits, and underscores.", result);
    }

	/**
	 * Tests if the username vaildation works properly
	 */
    @Test
    void testValidateUsername_ValidUsername() {
        String result = TextValidation.validateUsername("Valid_Username123");
        assertEquals("", result);
    }

    /**
     * Tests the email validation checker by testing an empty email
     */
    @Test
    void testValidateEmail_EmptyEmail() {
        String result = TextValidation.validateEmail("");
        assertEquals("*** Error *** Email cannot be empty!", result);
    }

    /**
     * Tests the email validation checker by testing an invaild email
     */
    @Test
    void testValidateEmail_InvalidEmailFormat() {
        String result = TextValidation.validateEmail("invalid-email");
        assertEquals("*** Error *** Invalid email format!", result);
    }

    /**
     * Tests the email validation checker by testing a valid email
     */
    @Test
    void testValidateEmail_ValidEmail() {
        String result = TextValidation.validateEmail("valid.email@example.com");
        assertEquals("", result);
    }

    /**
     * Tests the setup fields check by testing valid input 
     */
    @Test
    void testValidateSetupFields_ValidInput() {
    	char [] testPass = "Valid1!0".toCharArray();
        String result = TextValidation.validateSetupFields("Valid_User123", testPass, testPass);
        assertEquals("", result);
    }

    /**
     * Tests the setup fields check by testing invalid username
     */
    @Test
    void testValidateSetupFields_UsernameError() {
    	char [] testPass = "Valid1!0".toCharArray();
        String result = TextValidation.validateSetupFields("ab", testPass, testPass);
        assertEquals("*** Error *** Username must be between 3 and 20 characters.", result);
    }

    /**
     * Tests the setup fields check by testing invalid password
     */
    @Test
    void testValidateSetupFields_PasswordError() {
    	char [] testPass = "invalid".toCharArray();
        String result = TextValidation.validateSetupFields("Valid_User123", testPass, testPass);
        assertEquals("*** Error *** The following conditions were not met: Uppercase letter; Numeric digit; Special character; At least 8 characters long;", result);
    }

    /**
     * Tests the setup fields check by testing invalid mismatched passwords
     */
    @Test
    void testValidateChangePassword_MismatchedPasswords() {
    	char [] testPass = "Valid1!0".toCharArray();
    	char [] testPass2 = "Different1!".toCharArray();
        String result = TextValidation.validateChangePassword(testPass, testPass2);
        assertEquals("*** Error *** Passwords do not match!", result);
    }

    /**
     * Tests the isFieldEmpty function with a null field
     */
    @Test
    void testIsFieldEmpty_NullField() {
        String result = TextValidation.isFieldEmpty("");
        assertEquals("Field can not be null", result);
    }

    /**
     * Tests the isFieldEmpty function with an empty field
     */
    @Test
    void testIsFieldEmpty_EmptyField() {
        String result = TextValidation.isFieldEmpty("  ");
        assertEquals("Field can not be null", result);
    }

    /**
     * Tests the isFieldEmpty function with a valid input
     */
    @Test
    void testIsFieldEmpty_ValidField() {
        String result = TextValidation.isFieldEmpty("Valid Field");
        assertEquals("", result);
    }
}
