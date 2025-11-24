package marshmalliow.core.json;

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
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import marshmalliow.core.file.AbstractFile;
import marshmalliow.core.file.FileType;
import marshmalliow.core.file.ReadMode;
import marshmalliow.core.file.ReadResult;
import marshmalliow.core.file.SaveMode;
import marshmalliow.core.file.SaveResult;
import marshmalliow.core.helpers.SecurityHelper;
import marshmalliow.core.io.JSONLexer;
import marshmalliow.core.io.JSONParser;
import marshmalliow.core.io.JSONWriter;
import marshmalliow.core.json.objects.JSONArray;
import marshmalliow.core.json.objects.JSONContainer;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.security.EncryptionType;

public abstract class AbstractJSONFile extends AbstractFile {
	
	private JSONContainer content;
	private final Cipher cipher; // Only use when the file is encrypted
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean hasBeenRead = false; // Indicates if the file has been read at least once from disk. Does not reset after a save.
	
	protected AbstractJSONFile(AbstractJSONFileBuilder<?> builder) {
		super(builder.directory, builder.name, builder.credentials);
		
		this.content = builder.base;
		
		if(builder.credentials != null) {
			this.cipher = initCipher();
		} else {
			this.cipher = null;
		}
	}
	
	private final Cipher initCipher() {
		try {
			return this.credentials.getType() != EncryptionType.NONE ? Cipher.getInstance(this.credentials.getType().getEncryption()) : null;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			return null;
		}
	}

	@Override
	public final ReadResult readFile(ReadMode mode) throws IOException {
		try {
			lock.writeLock().lock(); // Acquire write lock to prevent reads/writes during this operation
			// Check for the file existence on the disk
			final boolean exists = directory.exists(this.fileName) && directory.size(this.fileName) > 0;
			final boolean canRead = directory.isReadable(this.fileName);
						
			// If the content has been modified and we are not forcing a read, we do not read the file again because it would overwrite the modifications.
			if(this.content.isModified() && !mode.equals(ReadMode.FORCE)) {
				throw new IOException("The content has been modified and cannot be read again without forcing a read.");
			}
			
			// If the file exists and is readable, we read it
			if(exists && canRead) {
				try(final BufferedReader reader = determineInputEncryption(directory.openInputStream(this.fileName))) {
					final JSONLexer lexer = new JSONLexer(reader);
					final JSONParser parser = new JSONParser(lexer);
					this.content = parser.parse();
				} catch (Exception e) {
					throw new IOException("Failed to read the JSON file: " + e.getMessage(), e);
				}
				this.hasBeenRead = true; // Mark as read
				this.content.resetModified(); // Reset the modified state after reading only if no exception occurred
				return ReadResult.READ;
			} else if (!exists) {
				this.hasBeenRead = false; // If the file does not exist, mark as not read
				return ReadResult.FILE_NOT_FOUND;
			} else {
				throw new IOException("The file is not readable or does not exist.");
			}
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	private final BufferedReader determineInputEncryption(InputStream fis) throws IOException {
		switch(this.credentials.getType()) {
			case AES_GCM_TAG_96 -> {
				try {
					return SecurityHelper.decryptWithAESGCM(cipher, fis, credentials, 96);
				}catch(InvalidKeyException | InvalidAlgorithmParameterException e) {
					throw new SecurityException("Couldn't determine input encryption for file "+this.uri().toString(), e);
				}
			}
			case AES_GCM_TAG_112 -> {
				try {
					return SecurityHelper.decryptWithAESGCM(cipher, fis, credentials, 112);
				}catch(InvalidKeyException | InvalidAlgorithmParameterException e) {
					throw new SecurityException("Couldn't determine input encryption for file "+this.uri().toString(), e);
				}
			}
			case AES_GCM_TAG_128 -> {
				try {
					return SecurityHelper.decryptWithAESGCM(cipher, fis, credentials, 128);
				}catch(InvalidKeyException | InvalidAlgorithmParameterException e) {
					throw new SecurityException("Couldn't determine input encryption for file "+this.uri().toString(), e);
				}
			}
			default -> {
				return new BufferedReader(new InputStreamReader(fis));
			}
		}
	}

	@Override
	public final SaveResult saveFile(SaveMode mode) throws IOException {
		try {
			lock.readLock().lock(); // Acquire read lock to prevent writes during this operation
			
			final boolean exists = directory.exists(this.fileName) && directory.size(this.fileName) > 0;
			
			if(!mode.equals(SaveMode.OVERWRITE) && exists && !this.hasBeenRead) {
				return SaveResult.SKIPPED_NOT_LOADED;
			}
			
			// If the content has not been modified and we are not forcing a save, we do not save the file again.
			if(!this.content.isModified() && !mode.equals(SaveMode.FORCE)) {
				return SaveResult.SKIPPED_NO_CHANGES;
			}
			
			try(final BufferedWriter writer = determineOutputEncryption(directory.openOutputStream(this.fileName))) {
				final JSONWriter jsonWriter = new JSONWriter(this.content);
				jsonWriter.write(writer);
			} catch (Exception e) {
				throw new IOException("Failed to save the JSON file: " + e.getMessage(), e);
			}
			this.content.resetModified(); // Reset the modified state after saving only if no exception occurred
			return SaveResult.SAVED;
		} finally {
			lock.readLock().unlock();
		}		
	}

	private final BufferedWriter determineOutputEncryption(OutputStream fos) throws IOException {
		switch(this.credentials.getType()) {
			case AES_GCM_TAG_96 -> {
				try {
					return SecurityHelper.encryptWithAESGCM(cipher, fos, credentials, 96);
				}catch(InvalidKeyException | InvalidAlgorithmParameterException e) {
					throw new SecurityException("Couldn't determine input encryption for file "+this.uri().toString(), e);
				}
			}
			case AES_GCM_TAG_112 -> {
				try {
					return SecurityHelper.encryptWithAESGCM(cipher, fos, credentials, 112);
				}catch(InvalidKeyException | InvalidAlgorithmParameterException e) {
					throw new SecurityException("Couldn't determine input encryption for file "+this.uri().toString(), e);
				}
			}
			case AES_GCM_TAG_128 -> {
				try {
					return SecurityHelper.encryptWithAESGCM(cipher, fos, credentials, 128);
				}catch(InvalidKeyException | InvalidAlgorithmParameterException e) {
					throw new SecurityException("Couldn't determine input encryption for file "+this.uri().toString(), e);
				}
			}
			default -> {
				return new BufferedWriter(new OutputStreamWriter(fos));
			}
		}
	}

	@Override
	public final FileType getFileType() {
		return FileType.JSON;
	}

	protected JSONContainer getContent() {
		try {
			lock.readLock().lock();
			return this.content;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	protected JSONObject getContentAsObject() {
		try {
			lock.readLock().lock();

			if(this.content instanceof JSONObject) return (JSONObject) this.content;
			else return null;
		} finally {
			lock.readLock().unlock();
		}
		
	}
	
	protected JSONArray getContentAsArray() {
		try {
			lock.readLock().lock();

			if(this.content instanceof JSONArray) return (JSONArray) this.content;
			else return null;
		} finally {
			lock.readLock().unlock();
		}
	}
}
