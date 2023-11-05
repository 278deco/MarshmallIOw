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
	
	public static BufferedWriter encryptWithAESGCM(Cipher cipher, OutputStream out, FileCredentials credentials, int tagSize) throws InvalidKeyException, InvalidAlgorithmParameterException, IOException {
		byte[] iv = SecurityHelper.generateSalt(credentials.getInitVectorSize());
		out.write(iv);
		
		cipher.init(Cipher.ENCRYPT_MODE, credentials.getKey(), new GCMParameterSpec(tagSize, iv));
		final CipherOutputStream cipherOut = new CipherOutputStream(out, cipher);
			
		return new BufferedWriter(new OutputStreamWriter(cipherOut, "UTF-8"));
	}
	
	public static BufferedReader decryptWithAESGCM(Cipher cipher, InputStream in, FileCredentials credentials, int tagSize) throws InvalidKeyException, InvalidAlgorithmParameterException, IOException {
		final byte[] fileIv = new byte[credentials.getInitVectorSize().getSize()];
		in.read(fileIv);
		
		cipher.init(Cipher.DECRYPT_MODE, credentials.getKey(), new GCMParameterSpec(tagSize, fileIv));
		final CipherInputStream cipherIn = new CipherInputStream(in, cipher);
		
		return new BufferedReader(new InputStreamReader(cipherIn, "UTF-8"));
	}
}
