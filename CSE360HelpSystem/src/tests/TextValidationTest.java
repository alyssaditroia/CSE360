package tests;

import org.junit.jupiter.api.Test;

import models.TextValidation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
/**
 * Junit test cases for the Text Validation class
 */
class TextValidationTest {
	private static int testcount = 0;
	private static int testPass = 0;

    @BeforeEach
    void setUp() {
		System.out.println("\n =========== TEXT VALIDATION TEST ===========\n");
		testcount = testcount + 1;
		System.out.printf("Test # %d%n", testcount);
    }
	@AfterAll
	static void afterAll() {
		System.out.println("\n =========== TEXT VALIDATION TESTS COMPLETE ===========\n");
    	System.out.println("\nTOTAL TESTS: " + testPass + "\nTESTS PASSED: " + testPass + "\n");
	}
	/**
	 * Tests empty password check
	 */
    @Test
    void testEvaluatePassword_EmptyPassword() {
    	System.out.println("\nTesting Empty Password Evaluation");
    	char [] testPassw = "".toCharArray();
        String result = TextValidation.evaluatePassword(testPassw);
        assertEquals("*** Error *** The password is empty!", result);
        testPass++; 
    }

	/**
	 * Tests null password check
	 */
    @Test
    void testEvaluatePassword_NullPassword() {
    	System.out.println("\nTesting Null Password Evaluation");
        String result = TextValidation.evaluatePassword(null);
        assertEquals("*** Error *** The password is empty!", result);
        testPass++; 
    }

	/**
	 * Tests valid password check
	 */
    @Test
    void testEvaluatePassword_ValidPassword() {
    	System.out.println("\nTesting Valid Password Creation");
    	String test = "Valid1!0";
    	char [] testPassw = test.toCharArray();
        String result = TextValidation.evaluatePassword(testPassw);
        assertEquals("", result);
        testPass++; 
    }

	/**
	 * Tests invalid password check for no uppercase letters
	 */
    @Test
    void testEvaluatePassword_InvalidPassword_NoUppercase() {
    	System.out.println("\nTesting Invalid Password Format - No Uppercase");
    	char [] testPassw = "invalid1!".toCharArray();
        String result = TextValidation.evaluatePassword(testPassw);
        assertEquals("*** Error *** The following conditions were not met: Uppercase letter;", result);
        testPass++; 
    }

	/**
	 * Tests invalid password check for no lowercase letters 
	 */
    @Test
    void testEvaluatePassword_InvalidPassword_NoLowercase() {
    	System.out.println("\nTesting Invalid Password Format - No Lowercase");
    	char [] testPassw = "INVALID1!".toCharArray();
        String result = TextValidation.evaluatePassword(testPassw);
        assertEquals("*** Error *** The following conditions were not met: Lowercase letter;", result);
        testPass++; 
    }

	/**
	 * Tests invalid password check for no digits
	 */
    @Test
    void testEvaluatePassword_InvalidPassword_NoDigit() {
    	System.out.println("\nTesting Invalid Password Format - No Digit");
    	char [] testPassw = "InvalidPassword!".toCharArray();
        String result = TextValidation.evaluatePassword(testPassw);
        assertEquals("*** Error *** The following conditions were not met: Numeric digit;", result);
        testPass++; 
    }

	/**
	 * Tests invalid password check for no special characters
	 */
    @Test
    void testEvaluatePassword_InvalidPassword_NoSpecialChar() {
    	System.out.println("\nTesting Invalid Password Format - No Special Character");
    	char [] testPassw = "Invalid1".toCharArray();
        String result = TextValidation.evaluatePassword(testPassw);
        assertEquals("*** Error *** The following conditions were not met: Special character;", result);
        testPass++; 
    }

	/**
	 * Tests empty username check
	 */
    @Test
    void testValidateUsername_EmptyUsername() {
    	System.out.println("\nTesting Empty Username");
        String result = TextValidation.validateUsername("");
        assertEquals("*** Error *** Username cannot be empty!", result);
        testPass++; 
    }

	/**
	 * Tests if the username is too short function
	 */
    @Test
    void testValidateUsername_TooShort() {
    	System.out.println("\nTesting Invalid Username Format - Too Short");
        String result = TextValidation.validateUsername("ab");
        assertEquals("*** Error *** Username must be between 3 and 20 characters.", result);
        testPass++; 
    }

	/**
	 * Tests if the username is too long function
	 */
    @Test
    void testValidateUsername_TooLong() {
    	System.out.println("\nTesting Invalid Username Format - Too Long");
        String result = TextValidation.validateUsername("a_very_long_username!!!!!!!");
        assertEquals("*** Error *** Username must be between 3 and 20 characters.", result);
        testPass++; 
    }

	/**
	 * Tests if the username contains invalid characters
	 */
    @Test
    void testValidateUsername_InvalidCharacters() {
    	System.out.println("\nTesting Invalid Username Format - Invalid Characters");
        String result = TextValidation.validateUsername("Invalid@Username");
        assertEquals("*** Error *** Username can only contain letters, digits, and underscores.", result);
        testPass++; 
    }

	/**
	 * Tests if the username vaildation works properly
	 */
    @Test
    void testValidateUsername_ValidUsername() {
    	System.out.println("\nTesting Valid Username Format");
        String result = TextValidation.validateUsername("Valid_Username123");
        assertEquals("", result);
        testPass++; 
    }

    /**
     * Tests the email validation checker by testing an empty email
     */
    @Test
    void testValidateEmail_EmptyEmail() {
    	System.out.println("\nTesting Empty Email");
        String result = TextValidation.validateEmail("");
        assertEquals("*** Error *** Email cannot be empty!", result);
        testPass++; 
    }

    /**
     * Tests the email validation checker by testing an invaild email
     */
    @Test
    void testValidateEmail_InvalidEmailFormat() {
    	System.out.println("\nTesting Invalid Email Format");
        String result = TextValidation.validateEmail("invalid-email");
        assertEquals("*** Error *** Invalid email format!", result);
        testPass++; 
    }

    /**
     * Tests the email validation checker by testing a valid email
     */
    @Test
    void testValidateEmail_ValidEmail() {
    	System.out.println("\nTesting Valid Email Format");
        String result = TextValidation.validateEmail("valid.email@example.com");
        assertEquals("", result);
        testPass++; 
    }

    /**
     * Tests the setup fields check by testing valid input 
     */
    @Test
    void testValidateSetupFields_ValidInput() {
    	System.out.println("\nTesting Valid Account Setup Fields");
    	char [] testPassw = "Valid1!0".toCharArray();
        String result = TextValidation.validateSetupFields("Valid_User123", testPassw, testPassw);
        assertEquals("", result);
        testPass++; 
    }

    /**
     * Tests the setup fields check by testing invalid username
     */
    @Test
    void testValidateSetupFields_UsernameError() {
    	System.out.println("\nTesting Invalid Account Setup Fields - Invalid Username");
    	char [] testPassw = "Valid1!0".toCharArray();
        String result = TextValidation.validateSetupFields("ab", testPassw, testPassw);
        assertEquals("*** Error *** Username must be between 3 and 20 characters.", result);
        testPass++; 
    }

    /**
     * Tests the setup fields check by testing invalid password
     */
    @Test
    void testValidateSetupFields_PasswordError() {
    	System.out.println("\nTesting Invalid Account Setup Fields - Invalid Password");
    	char [] testPassw = "invalid".toCharArray();
        String result = TextValidation.validateSetupFields("Valid_User123", testPassw, testPassw);
        assertEquals("*** Error *** The following conditions were not met: Uppercase letter; Numeric digit; Special character; At least 8 characters long;", result);
        testPass++; 
    }

    /**
     * Tests the setup fields check by testing invalid mismatched passwords
     */
    @Test
    void testValidateChangePassword_MismatchedPasswords() {
    	System.out.println("\nTesting Invalid Account Setup Fields - Password Missmatch");
    	char [] testPassw = "Valid1!0".toCharArray();
    	char [] testPass2 = "Different1!".toCharArray();
        String result = TextValidation.validateChangePassword(testPassw, testPass2);
        assertEquals("*** Error *** Passwords do not match!", result);
        testPass++; 
    }

    /**
     * Tests the isFieldEmpty function with a null field
     */
    @Test
    void testIsFieldEmpty_NullField() {
    	System.out.println("\nTesting Null Field Check");
        String result = TextValidation.isFieldEmpty("");
        assertEquals("Field can not be null", result);
        testPass++; 
    }

    /**
     * Tests the isFieldEmpty function with an empty field
     */
    @Test
    void testIsFieldEmpty_EmptyField() {
    	System.out.println("\nTesting Empty Field Check");
        String result = TextValidation.isFieldEmpty("  ");
        assertEquals("Field can not be null", result);
        testPass++; 
    }

    /**
     * Tests the isFieldEmpty function with a valid input
     */
    @Test
    void testIsFieldEmpty_ValidField() {
    	System.out.println("\nTesting Valid Field Check");
        String result = TextValidation.isFieldEmpty("Valid Field");
        assertEquals("", result);
        testPass++; 
    }
}
