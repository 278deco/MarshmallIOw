package marshmalliow.core.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import marshmalliow.core.helpers.SecurityHelper;
import marshmalliow.core.objects.Directory;
import marshmalliow.core.objects.FileType;
import marshmalliow.core.objects.IOClass;
import marshmalliow.core.security.EncryptionType;
import marshmalliow.core.security.FileCredentials;

/**
 * Manage text file easily (like .txt file) with reading, writing methods
 * @version 1.0.0
 * @author 278deco
 */
public class TextFile extends IOClass {

	private static final Logger LOGGER = LogManager.getLogger(TextFile.class);
	
	private List<String> content;
	private final Cipher cipher; //Only use when the file is encrypted
	
	/**
	 * Create new TextFile instance
	 * @param dir The directory of the TextFile
	 * @param name The name of the file <strong>without extension</strong>
	 */
	public TextFile(Directory dir, String name) {
		super(dir, name);
		this.cipher = null;
		
		initFile();
	}
	
	/**
	 * Create new TextFile instance
	 * @param dir The directory of the TextFile
	 * @param name The name of the file <strong>without extension</strong>
	 * @param credentials The {@link FileCredentials} associated with this file
	 */
	public TextFile(Directory dir, String name, FileCredentials credentials) {
		super(dir, name, credentials);
		this.cipher = initCipher();
		
		initFile();
	}
	
	private void initFile() {
		try {
			
			if(!Files.exists(getFullPath()))
				Files.createFile(getFullPath());
			this.content = new ArrayList<>();
			
		} catch (IOException e) {
			LOGGER.error("Unexpected error while loading text file [dir: {}, name: {}] with message {}", this.directory.getName(), this.fileName, e.getMessage());
		}
	}
	
	private Cipher initCipher() {
		try {
			return this.credentials.getType() != EncryptionType.NONE ? Cipher.getInstance(this.credentials.getType().getEncryption()) : null;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			return null;
		}
	}
	
	/**
	 * Read and add all file's content in a list
	 * @param forceRead Isn't used in this function
	 */
	@Override
	public void readFile(boolean forceRead) {
		InputStream input = null;
		BufferedReader buffer = null;
		
		try {
			input = Files.newInputStream(getFullPath());
			buffer = determineInputEncryption(input);
			
			if(!this.content.isEmpty()) this.content.clear();
			
			String line;
			while( (line = buffer.readLine()) != null) {
				this.content.add(line);
			}
			
		}catch(IOException e) {
			LOGGER.error("Unexpected error while loading text file [dir: {}, name: {}] with message {}", this.directory.getName(), this.fileName, e.getMessage());
		}finally {
			try { if(buffer != null) buffer.close(); }catch(IOException e) {}
			try { if(input != null) input.close(); }catch(IOException e) {}
		}
	}
	
	public void readFile() {
		this.readFile(false);
	}
	
	private BufferedReader determineInputEncryption(InputStream fis) throws IOException {
		switch(this.credentials.getType()) {
			case AES_GCM_TAG_96 -> {
				try {
					return SecurityHelper.decryptWithAESGCM(cipher, fis, credentials, 96);
				}catch(InvalidKeyException | InvalidAlgorithmParameterException e) {
					throw new SecurityException("Couldn't determine input encryption for file "+this.getFullPath(), e);
				}
			}
			case AES_GCM_TAG_112 -> {
				try {
					return SecurityHelper.decryptWithAESGCM(cipher, fis, credentials, 112);
				}catch(InvalidKeyException | InvalidAlgorithmParameterException e) {
					throw new SecurityException("Couldn't determine input encryption for file "+this.getFullPath(), e);
				}
			}
			case AES_GCM_TAG_128 -> {
				try {
					return SecurityHelper.decryptWithAESGCM(cipher, fis, credentials, 128);
				}catch(InvalidKeyException | InvalidAlgorithmParameterException e) {
					throw new SecurityException("Couldn't determine input encryption for file "+this.getFullPath(), e);
				}
			}
			default -> {
				return new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			}
		}
	}
	
	/**
	 * Write all lines contained in the list to the disk<br>
	 * This method will always return true as the file is saved in his own thread
	 * @param forceSave Isn't used in this function
	 */
	@Override
	public void saveFile(boolean forceSave) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				OutputStream output = null;
				BufferedWriter buffer = null;
				
				try {
					output = Files.newOutputStream(getFullPath());
					buffer = determineOutputEncryption(output);
					
					for(int i = 0; i < content.size(); i++) {
						buffer.write(content.get(i));
						if(i != content.size()-1) buffer.newLine();
					}
					
				}catch(IOException e) {
					LOGGER.error("Unexpected error while writing to text file [dir: {}, name: {}] with message {}", directory.getName(), fileName, e.getMessage());
				}finally {
					try { if(buffer != null) buffer.close(); }catch(IOException e) {}
					try { if(output != null) output.close(); }catch(IOException e) {}
				}
			}
		},"File-Save-Thread").start();
		
	}
	
	public void saveFile() {
		this.saveFile(false);
	}
	
	private BufferedWriter determineOutputEncryption(OutputStream fos) throws IOException {
		switch(this.credentials.getType()) {
			case AES_GCM_TAG_96 -> {
				try {
					return SecurityHelper.encryptWithAESGCM(cipher, fos, credentials, 96);
				}catch(InvalidKeyException | InvalidAlgorithmParameterException e) {
					throw new SecurityException("Couldn't determine input encryption for file "+this.getFullPath(), e);
				}
			}
			case AES_GCM_TAG_112 -> {
				try {
					return SecurityHelper.encryptWithAESGCM(cipher, fos, credentials, 112);
				}catch(InvalidKeyException | InvalidAlgorithmParameterException e) {
					throw new SecurityException("Couldn't determine input encryption for file "+this.getFullPath(), e);
				}
			}
			case AES_GCM_TAG_128 -> {
				try {
					return SecurityHelper.encryptWithAESGCM(cipher, fos, credentials, 128);
				}catch(InvalidKeyException | InvalidAlgorithmParameterException e) {
					throw new SecurityException("Couldn't determine input encryption for file "+this.getFullPath(), e);
				}
			}
			default -> {
				return new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			}
		}
	}

	/**
	 * Add a new line to content of the file<br>
	 * If the file is never saved, the line while only be added to this instance of the TextFile
	 * @param lines all the line which needs to added
	 * @see #saveFile()
	 */
	public void addNewLine(String... lines) {
		for(String line : lines) {
			if(line != null && line != "" && !line.isBlank() && !line.isEmpty()) this.content.add(line);
		}
	}
	
	/**
	 * Clear all file's content
	 */
	public void clearContent() {
		this.content.clear();
	}
	
	public List<String> getContent() {
		return Collections.unmodifiableList(this.content);
	}
	
	/**
	 * Return the content of the i line
	 * @param i The index of the line
	 * @return the line of the file
	 */
	public String getLine(int i) {
		return this.content.size() <= i ? null : this.content.get(i);
	}
	
	/**
	 * Get file's content size
	 * @return the size of the content
	 */
	public int getContentSize() {
		return this.content.size();
	}
	
	/**
	 * Get a random line of file's content
	 * @return a random line of the file
	 */
	public String getRandomLine(Random randomGenerator) {
		return getContentSize() > 0 ? getLine(randomGenerator.nextInt(getContentSize())) : "";
	}

	@Override
	public FileType getFileType() {
		return FileType.PLAIN_TEXT;
	}

	@Override
	public String getFullName() {
		return this.fileName+".txt";
	}
	
}