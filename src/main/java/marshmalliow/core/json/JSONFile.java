package marshmalliow.core.json;

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

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import marshmalliow.core.helpers.SecurityHelper;
import marshmalliow.core.io.JSONLexer;
import marshmalliow.core.io.JSONParser;
import marshmalliow.core.io.JSONWriter;
import marshmalliow.core.json.objects.JSONContainer;
import marshmalliow.core.objects.Directory;
import marshmalliow.core.objects.FileType;
import marshmalliow.core.objects.IOClass;
import marshmalliow.core.security.EncryptionType;
import marshmalliow.core.security.FileCredentials;

public class JSONFile extends IOClass {
	
	private JSONContainer content;
	private final Cipher cipher; // Only use when the file is encrypted
	
	private final Object mutex = new Object();
	private boolean hasBeenRead = false; // Indicates if the file has been read at least once from disk. Does not reset after a save.
	
	protected JSONFile(JSONFileBuilder<?> builder) {
		super(builder.directory, builder.name, builder.credentials);
		
		this.content = builder.base;
		
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

	@Override
	public void readFile(boolean forceRead) throws IOException {
		synchronized (mutex) {
			// Check for the file existence on the disk
			final boolean exists = Files.exists(getFullPath()) && Files.size(getFullPath()) > 0;
			final boolean canRead = Files.isReadable(getFullPath());
			
			// If the content has been modified and we are not forcing a read, we do not read the file again because it would overwrite the modifications.
			if(this.content.isModified() && !forceRead) {
				throw new IOException("The content has been modified and cannot be read again without forcing a read.");
			}
			
			// If the file exists and is readable, we read it
			if(exists && canRead) {
				try(final BufferedReader reader = determineInputEncryption(Files.newInputStream(getFullPath()))) {
					final JSONLexer lexer = new JSONLexer(reader);
					final JSONParser parser = new JSONParser(lexer);
					this.content = parser.parse();
					this.hasBeenRead = true; // Mark as read
					this.content.resetModified(); // Reset the modified state after reading
				} catch (Exception e) {
					throw new IOException("Failed to read the JSON file: " + e.getMessage(), e);
				}
			} else if (!exists) {
				this.hasBeenRead = false; // If the file does not exist, mark as not read
			} else {
				throw new IOException("The file is not readable or does not exist.");
			}
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
				return new BufferedReader(new InputStreamReader(fis));
			}
		}
	}

	@Override
	public void saveFile(boolean forceSave) throws IOException {
		synchronized (mutex) {
			final boolean exists = Files.exists(getFullPath()) && Files.size(getFullPath()) > 0;
			
			if(!forceSave && exists && !this.hasBeenRead) {
				throw new IOException("The file has not been read before saving. Please read the file first or force the save.");
			}
			
			// If the content has not been modified and we are not forcing a save, we do not save the file again.
			if(!this.content.isModified() && !forceSave) {
				throw new IOException("The content has not been modified and cannot be saved again without forcing a save.");
			}
			
			try(final BufferedWriter writer = determineOutputEncryption(Files.newOutputStream(getFullPath()))) {
				final JSONWriter jsonWriter = new JSONWriter(this.content);
				jsonWriter.write(writer);
				this.content.resetModified(); // Reset the modified state after saving
			} catch (Exception e) {
				throw new IOException("Failed to save the JSON file: " + e.getMessage(), e);
			}
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
				return new BufferedWriter(new OutputStreamWriter(fos));
			}
		}
	}

	@Override
	public FileType getFileType() {
		return FileType.JSON;
	}

	@Override
	public String getFileWithExtension() {
		return this.fileName + ".json";
	}
	
	public JSONContainer getContent() {
		synchronized (mutex) {
			return this.content;
		}
	}
	
	public static <T extends JSONFile> JSONFileBuilder<T> builder(Class<T> clazz) {
		return new JSONFileBuilder<>(clazz);
	}
	
	public static JSONFileBuilder<JSONFile> builder() {
		return new JSONFileBuilder<>(JSONFile.class);
	}
	
	public static class JSONFileBuilder<T extends JSONFile> {
		private final Class<T> clazz;
		
		private Directory directory;
		private String name;
		private JSONContainer base;
		private FileCredentials credentials;
		
		
		public JSONFileBuilder(Class<T> clazz) {
			this.clazz = clazz;
		}
		public JSONFileBuilder<T> directory(Directory directory) {
			this.directory = directory;
			return this;
		}
		
		public JSONFileBuilder<T> name(String name) {
			this.name = name;
			return this;
		}
		
		public JSONFileBuilder<T> base(JSONContainer base) {
			this.base = base;
			return this;
		}
		
		public JSONFileBuilder<T> credentials(FileCredentials credentials) {
			this.credentials = credentials;
			return this;
		}
		
		public T build() {
			if (this.directory == null || this.name == null || this.base == null) {
				throw new IllegalArgumentException("Directory, name and JSONContainer base must be set");
			}
			
			try {
				return clazz.getConstructor(JSONFileBuilder.class).newInstance(this);
			}catch(ReflectiveOperationException e) {
				throw new RuntimeException("Failed to create JSONFile instance", e);
			}
		}
	}

}
