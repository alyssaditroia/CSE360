package Encryption;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
/**
 * The {@code EncryptionUtils} handles the IV generation, byte array conversions, and char array conversions. 
 * 
 * @author Lynn Robert carter
 * 
 */
public class EncryptionUtils {
	private static int IV_SIZE = 16;
	
	/**
	 * Converts a byte array to a character array using the default charset.
	 *
	 * @param bytes the byte array to convert
	 * @return the converted character array
	 */
	public static char[] toCharArray(byte[] bytes) {		
        CharBuffer charBuffer = Charset.defaultCharset().decode(ByteBuffer.wrap(bytes));
        return Arrays.copyOf(charBuffer.array(), charBuffer.limit());
	}
	
	/**
	 * Converts a character array to a byte array using the default charset.
	 *
	 * @param chars the character array to convert
	 * @return the converted byte array
	 */
	public static byte[] toByteArray(char[] chars) {		
        ByteBuffer byteBuffer = Charset.defaultCharset().encode(CharBuffer.wrap(chars));
        return Arrays.copyOf(byteBuffer.array(), byteBuffer.limit());
	}
		
	/**
	 * Generates an initialization vector from the input text by cycling through its characters.
	 * The resulting IV has a fixed size of 16 bytes.
	 *
	 * @param text the input text to generate the IV from
	 * @return a byte array containing the initialization vector
	 */
	public static byte[] getInitializationVector(char[] text) {
		char iv[] = new char[IV_SIZE];
		
		int textPointer = 0;
		int ivPointer = 0;
		while(ivPointer < IV_SIZE) {
			iv[ivPointer++] = text[textPointer++ % text.length];
		}
		
		return toByteArray(iv);
	}
	
	/**
	 * Prints the contents of a character array to standard output.
	 *
	 * @param chars the character array to print
	 */
	public static void printCharArray(char[] chars) {
		for(char c : chars) {
			System.out.print(c);
		}
	}
}
