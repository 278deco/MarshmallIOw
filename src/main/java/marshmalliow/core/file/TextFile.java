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
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

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
	
	private final AtomicBoolean contentModified = new AtomicBoolean(false);
	private List<String> content;
	
	private final Cipher cipher; //Only use when the file is encrypted
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean hasBeenRead = false; // Indicates if the file has been read at least once from disk. Does not reset after a save.
	
	
	protected TextFile(TextFileBuilder<?> builder) {
		super(builder.directory, builder.name, builder.credentials);
		
		if(builder.credentials != null) {
			this.cipher = initCipher();
		} else {
			this.cipher = null;
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
	public void readFile(boolean forceRead) throws IOException {
		try {
			lock.writeLock().lock(); // We lock using write lock so nobody can write or read from memory while we are reading from disk
			
			// Check for the file existence on the disk
			final boolean exists = Files.exists(getFullPath()) && Files.size(getFullPath()) > 0;
			final boolean canRead = Files.isReadable(getFullPath());
			
			// If the content has been modified and we are not forcing a read, we do not read the file again because it would overwrite the modifications.
			if(this.contentModified.get() && !forceRead) {
				throw new IOException("The content has been modified and cannot be read again without forcing a read.");
			}
			
			// If the file exists and is readable, we read it
			if(exists && canRead) {
				try (final BufferedReader reader = determineInputEncryption(Files.newInputStream(getFullPath()))) {
		            String line;
		            while ((line = reader.readLine()) != null) {
		                this.content.add(line);
		            }
		        } catch (IOException e) {
		        	throw new IOException("Failed to read the text file: " + e.getMessage(), e);
		        }
			}else if (!exists) {
				this.hasBeenRead = false; // If the file does not exist, mark as not read
			} else {
				throw new IOException("The file is not readable or does not exist.");
			}
			
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public void readFile() throws IOException {
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
	
	@Override
	public void saveFile(boolean forceSave) throws IOException {
		try {
			lock.writeLock().lock(); // We lock using write lock so nobody can write or read from memory while we are writing to disk
			
			final boolean exists = Files.exists(getFullPath()) && Files.size(getFullPath()) > 0;
			
			if(!forceSave && exists && !this.hasBeenRead) {
				throw new IOException("The file has not been read before saving. Please read the file first or force the save.");
			}
			
			if(!this.contentModified.get() && !forceSave) {
				throw new IOException("The content has not been modified and cannot be saved again without forcing a save.");
			}
			
			try(final BufferedWriter writer = determineOutputEncryption(Files.newOutputStream(getFullPath()))) {

				for(int i = 0; i < content.size(); i++) {
					writer.write(content.get(i));
					if(i != content.size()-1) writer.newLine();
				}
				
			} catch (IOException e) {
				throw new IOException("Failed to write the text file: " + e.getMessage(), e);
			}
		} finally {
			lock.writeLock().unlock();
		}	
	}
	
	public void saveFile() throws IOException {
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
	 * Add a new line(s) to content of the file<br>
	 * If the file is never saved, the line while only be added to this instance of the TextFile
	 * 
	 * @param lines One or more lines which needs to added
	 * @see #saveFile()
	 */
	public void addNewLine(String... lines) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
		
			for(String line : lines) {
				if(line != null && line != "" && !line.isBlank() && !line.isEmpty()) this.content.add(line);
			}
		}	finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * Add a new line(s) to content of the file<br>
	 * If the file is never saved, the line while only be added to this instance of the TextFile
	 * 
	 * @param lines One or more lines which needs to added
	 * @see #saveFile()
	 */
	public void addNewLine(List<String> lines) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			
			for(String line : lines) {
				if(line != null && line != "" && !line.isBlank() && !line.isEmpty()) this.content.add(line);
			}
		}	finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * Add a single new line to content of the file<br>
	 * If the file is never saved, the line while only be added to this instance of the TextFile
	 * 
	 * @param line The line which needs to be added
	 * @see #saveFile()
	 */
	public void addNewLine(String line) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);

			if(line != null) this.content.add(line);
		}	finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * Clear all file's content
	 */
	public void clearContent() {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);

			this.content.clear();
		}	finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * Get the content of the file as an unmodifiable list<br/>
	 * Modifications to the returned list will throw {@link UnsupportedOperationException}
	 * 
	 * @return the content of the file
	 * @see #addNewLine(String)
	 * @see #addNewLine(String...)
	 * @see #addNewLine(List)
	 */
	public List<String> getContent() {
		try {
			lock.readLock().lock();
			
			return Collections.unmodifiableList(this.content);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Get a specific line of file's content by its index
	 * 
	 * @param i The index of the line
	 * @return the line at the specified index or <code>null</code> if the index is out of bounds
	 */
	public String getLine(int i) {
		try {
			lock.readLock().lock();

			return this.content.size() <= i ? null : this.content.get(i);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Get a random line of file's content using a {@link Random} instance<br/>
	 * If the file is empty, it will return an empty string
	 * 
	 * @param randomGenerator An instance of Random generator to use
	 * @return a random line of the file or null if the file is empty
	 */
	public String getRandomLine(Random randomGenerator) {
		try {
			lock.readLock().lock();

			return getContentSize() > 0 ? getLine(randomGenerator.nextInt(getContentSize())) : null;		
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Get file's content size
	 * @return the size of the content
	 */
	public int getContentSize() {
		try {
			lock.readLock().lock();
			return this.content == null ? 0 : this.content.size();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public FileType getFileType() {
		return FileType.PLAIN_TEXT;
	}

	@Override
	public String getFileWithExtension() {
		return this.fileName+".txt";
	}
	
	public static <T extends TextFile> TextFileBuilder<T> builder(Class<T> clazz) {
		return new TextFileBuilder<>(clazz);
	}
	
	public static TextFileBuilder<TextFile> builder() {
		return new TextFileBuilder<>(TextFile.class);
	}
	
	public static class TextFileBuilder<T extends TextFile> {
		private final Class<T> clazz;
		
		private Directory directory;
		private String name;
		private FileCredentials credentials;
		
		public TextFileBuilder(Class<T> clazz) {
			this.clazz = clazz;
		}
		
		public TextFileBuilder<T> directory(Directory directory) {
			this.directory = directory;
			return this;
		}
		
		public TextFileBuilder<T> name(String name) {
			this.name = name;
			return this;
		}
		
		public TextFileBuilder<T> credentials(FileCredentials credentials) {
			this.credentials = credentials;
			return this;
		}
		
		public T build() {
			if (this.directory == null || this.name == null) {
				throw new IllegalArgumentException("The directory or name cannot be null");
			}
			
			try {
				return clazz.getConstructor(TextFileBuilder.class).newInstance(this);
			} catch (Exception e) {
				throw new RuntimeException("Couldn't create a new instance of "+clazz.getName(), e);
			}
		}
		
	}
	
}