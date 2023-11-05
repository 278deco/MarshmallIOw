package marshmalliow.core.helpers;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import marshmalliow.core.security.AESKeySize;
import marshmalliow.core.security.SaltSize;

public class SecurityHelper {

	public static final int ITERATION_COUNT_DEFAULT = 65536;
	
	public static byte[] generateSalt(SaltSize size) {
		final byte[] vector = new byte[size.getSize()];
		new SecureRandom().nextBytes(vector);
		
		return vector;
	}
	
	public static byte[] generateSalt(byte[] seed, SaltSize size) {
		final byte[] vector = new byte[size.getSize()];
		new SecureRandom(seed).nextBytes(vector);
		
		return vector;
	}
	
	public static SecretKey getAESKey(AESKeySize size) throws NoSuchAlgorithmException {
		final KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(size.getSize(), SecureRandom.getInstanceStrong());
		
		return keyGen.generateKey();
	}
	
	public static SecretKey getAESKeyFromPassword(AESKeySize size, char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		final KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT_DEFAULT, size.getSize());
		
		return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
	}
}
