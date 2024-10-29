package Encryption;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
/**
 * The {@code EncryptionHelper} handles the encrypt and decrypt functions
 */
public class EncryptionHelper {

	private static String BOUNCY_CASTLE_PROVIDER_IDENTIFIER = "BC";	
	private Cipher cipher;
	
	byte[] keyBytes = new byte[] {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
            0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17 };
	private SecretKey key = new SecretKeySpec(keyBytes, "AES");

	/**
	 * Constructor for EncryptionHelper that initializes the Bouncy Castle security provider
	 * and sets up the AES cipher in CBC mode with PKCS5 padding.
	 *
	 * @throws Exception if there's an error initializing the cipher or security provider
	 */
	public EncryptionHelper() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", BOUNCY_CASTLE_PROVIDER_IDENTIFIER);		
	}
	
	/**
	 * Encrypts the given plaintext using AES encryption with the specified initialization vector.
	 *
	 * @param plainText the data to be encrypted
	 * @param initializationVector the initialization vector to use for encryption
	 * @return the encrypted data as a byte array
	 * @throws Exception if encryption fails
	 */
	public byte[] encrypt(byte[] plainText, byte[] initializationVector) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(initializationVector));
		return cipher.doFinal(plainText);
	}
	
	/**
	 * Decrypts the given ciphertext using AES decryption with the specified initialization vector.
	 *
	 * @param cipherText the data to be decrypted
	 * @param initializationVector the initialization vector used for encryption
	 * @return the decrypted data as a byte array
	 * @throws Exception if decryption fails
	 */
	public byte[] decrypt(byte[] cipherText, byte[] initializationVector) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(initializationVector));
		return cipher.doFinal(cipherText);
	}
	
}
