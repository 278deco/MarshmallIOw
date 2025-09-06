package marshmalliow.core.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import marshmalliow.core.security.AESKeySize;
import marshmalliow.core.security.FileCredentials;
import marshmalliow.core.security.SaltSize;

public class SecurityHelper {

	public static final int ITERATION_COUNT_DEFAULT = 65536;
	
	/**
	 * Generates a random salt of the specified {@link SaltSize}.
	 * @param size The size of the salt to generate.
	 * @return A byte array containing the generated salt.
	 */
	public static byte[] generateSalt(SaltSize size) {
		final byte[] vector = new byte[size.getSize()];
		new SecureRandom().nextBytes(vector);
		
		return vector;
	}
	
	/**
     * Generates a random salt of the specified {@link SaltSize} using the provided seed.
     * @param seed The seed to use for generating the salt.
     * @param size The size of the salt to generate.
     * @return A byte array containing the generated salt.
     */
	public static byte[] generateSalt(byte[] seed, SaltSize size) {
		final byte[] vector = new byte[size.getSize()];
		new SecureRandom(seed).nextBytes(vector);
		
		return vector;
	}
	
	/**
	 * Generates a random AES key of the specified {@link AESKeySize}.
	 * 
	 * @param size The size of the key to generate.
	 * @return A {@link SecretKey} containing the generated key.
	 * @throws NoSuchAlgorithmException If the algorithm used to generate the key is not found.
	 */
	public static SecretKey getAESKey(AESKeySize size) throws NoSuchAlgorithmException {
		final KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(size.getSize(), SecureRandom.getInstanceStrong());
		
		return keyGen.generateKey();
	}
	
	/**
	 * Generates an AES key of the specified {@link AESKeySize} using a password and
	 * salt.
	 * 
	 * @param size     The size of the key to generate.
	 * @param password The password to use for generating the key.
	 * @param salt     The salt to use for generating the key.
	 * @return A {@link SecretKey} containing the generated key.
	 * @throws NoSuchAlgorithmException If the algorithm used to generate the key is not found.
	 * @throws InvalidKeySpecException  If the key specification is invalid.
	 */
	public static SecretKey getAESKeyFromPassword(AESKeySize size, char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		final KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT_DEFAULT, size.getSize());
		
		return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
	}
	
	/**
	 * Encrypts the data from the specified input stream using the provided cipher
	 * and writes the encrypted data to the output stream.
	 * 
	 * @param cipher The cipher to use for encryption.
	 * @param out    The output stream to write the encrypted data to.
	 * @param credentials The credentials to use for encryption.
	 * @param tagSize The size of the authentication
	 * @return A {@link CipherOutputStream} containing the encrypted data.
	 * @throws IOException If an I/O error occurs.
	 */
	public static BufferedWriter encryptWithAESGCM(Cipher cipher, OutputStream out, FileCredentials credentials, int tagSize) throws InvalidKeyException, InvalidAlgorithmParameterException, IOException {
		byte[] iv = SecurityHelper.generateSalt(credentials.getInitVectorSize());
		out.write(iv);
		
		cipher.init(Cipher.ENCRYPT_MODE, credentials.getKey(), new GCMParameterSpec(tagSize, iv));
		final CipherOutputStream cipherOut = new CipherOutputStream(out, cipher);
			
		return new BufferedWriter(new OutputStreamWriter(cipherOut, "UTF-8"));
	}
	
	/**
	 * Decrypts the data from the specified input stream using the provided cipher
	 * and writes the decrypted data to the output stream.
	 * 
	 * @param cipher The cipher to use for decryption.
	 * @param in     The input stream to read the encrypted data from.
	 * @param credentials The credentials to use for decryption.
	 * @param tagSize The size of the authentication
	 * @return A {@link CipherInputStream} containing the decrypted data.
	 * @throws IOException If an I/O error occurs.
	 */
	public static BufferedReader decryptWithAESGCM(Cipher cipher, InputStream in, FileCredentials credentials, int tagSize) throws InvalidKeyException, InvalidAlgorithmParameterException, IOException {
		final byte[] fileIv = new byte[credentials.getInitVectorSize().getSize()];
		in.read(fileIv);
		
		cipher.init(Cipher.DECRYPT_MODE, credentials.getKey(), new GCMParameterSpec(tagSize, fileIv));
		final CipherInputStream cipherIn = new CipherInputStream(in, cipher);
		
		return new BufferedReader(new InputStreamReader(cipherIn, "UTF-8"));
	}
}
