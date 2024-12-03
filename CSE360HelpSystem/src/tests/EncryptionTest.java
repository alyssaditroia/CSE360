package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Encryption.EncryptionHelper;
import Encryption.EncryptionUtils;
/**
 * The {@code EncryptionTest} class tests the Encryption Helper and Encryption Utils class
 */
class EncryptionTest {

    private EncryptionHelper encryptionHelper;
    private byte[] testIV;
    private byte[] testPlainText;
    private static int testcount = 1;
    private static int testPass = 0;

    @BeforeEach
    void setUp() throws Exception {
        encryptionHelper = new EncryptionHelper();
        System.out.println("\n =========== ENCRYPTION TEST ===========\n");
        System.out.printf("Test Group # %d%n", testcount++);
        testIV = new byte[16];
        Arrays.fill(testIV, (byte) 0x01); 
        testPlainText = "TestThisText".getBytes();
    }
	@AfterAll
	static void afterAll() {
		System.out.println("\n =========== ENCRYPTION TESTS COMPLETE ===========\n");
    	System.out.println("\nTOTAL TESTS: " + testPass + "\nTESTS PASSED: " + testPass + "\n");
	}

    // Tests for EncryptionHelper

    @Test
    void testEncryptDecrypt() throws Exception {
    	System.out.println("Testing Encryption and Decryption");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 4\n");
        // Encrypt the plaintext
        byte[] encrypted = encryptionHelper.encrypt(testPlainText, testIV);
        System.out.println("Encrypted Data: " + Arrays.toString(encrypted));
        assertNotNull(encrypted);
        assertNotEquals(0, encrypted.length);

        // Decrypt the ciphertext
        byte[] decrypted = encryptionHelper.decrypt(encrypted, testIV);
        System.out.println("Decrypted Data: " + new String(decrypted));
        assertNotNull(decrypted);

        // Verify the decrypted text matches the original plaintext
        assertArrayEquals(testPlainText, decrypted);
        testPass+= 4;
    }

    
    // Tests for EncryptionUtils
    @Test
    void testToCharArrayAndToByteArray() {
    	System.out.println("Testing toCharArray and toByteArray");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
    	System.out.println("Test String: TestString");
        String testString = "TestString";
        byte[] byteArray = testString.getBytes();

        // Convert to char array and back to byte array
        char[] charArray = EncryptionUtils.toCharArray(byteArray);
        byte[] resultByteArray = EncryptionUtils.toByteArray(charArray);

        // Verify the byte arrays are equal
        assertArrayEquals(byteArray, resultByteArray);
        testPass++;
    }

    @Test
    void testGetInitializationVector() {
    	System.out.println("Testing Initialization Vector Creation");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 4\n");
        char[] text = "TestIV".toCharArray();

        // Generate the IV
        byte[] iv = EncryptionUtils.getInitializationVector(text);

        // Verify the IV has the correct size
        assertNotNull(iv);
        assertEquals(16, iv.length);

        // Verify IV is a cyclic pattern of input characters
        assertEquals(text[0], EncryptionUtils.toCharArray(iv)[0]);
        assertEquals(text[1], EncryptionUtils.toCharArray(iv)[1]);
        testPass+= 4;
    }

    @Test
    void testPrintCharArray() {
    	System.out.println("Testing Printing Character Array");
    	System.out.println("\nTESTS IN THIS TEST GROUP: 1\n");
        char[] chars = "TestPrint".toCharArray();
        EncryptionUtils.printCharArray(chars);

        // Since this method only prints, ensure no exceptions occur
        assertDoesNotThrow(() -> EncryptionUtils.printCharArray(chars));
        testPass++;
    }
}

