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
import marshmalliow.core.json.io.JSONLexer;
import marshmalliow.core.json.io.JSONParser;
import marshmalliow.core.json.io.JSONWriter;
import marshmalliow.core.json.objects.JSONArray;
import marshmalliow.core.json.objects.JSONContainer;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.objects.Directory;
import marshmalliow.core.objects.FileType;
import marshmalliow.core.objects.IOClass;
import marshmalliow.core.security.EncryptionType;
import marshmalliow.core.security.FileCredentials;

public class JSONFile extends IOClass {
	
	private JSONContainer content;
	private final Cipher cipher; //Only use when the file is encrypted
	
	private final Object mutex = new Object();
	
	private boolean isOpen;
	
	public JSONFile(Directory dir, String name, JSONContainer content) {
		super(dir, name);
		this.content = content;
		this.isOpen = true;
		this.cipher = null;
	}
	
	public JSONFile(Directory dir, String name) {
		super(dir, name);
		this.content = null;
		this.isOpen = false;
		this.cipher = null;
	}
	
	public JSONFile(Directory dir, String name, JSONContainer content, FileCredentials credential) {
		super(dir, name, credential);
		this.content = content;
		this.isOpen = true;
		
		this.cipher = initCipher();
	}
	
	public JSONFile(Directory dir, String name, FileCredentials credential) {
		super(dir, name, credential);
		this.content = null;
		this.isOpen = false;
		
		this.cipher = initCipher();
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
			if(Files.size(getFullPath()) <= 0) {
				this.isOpen = true; //If the file content is empty but the user still wants to read it, then define the file as open
				this.content = new JSONObject();
			}else if((forceRead || !this.isOpen)) {
				InputStream stream = null;
				BufferedReader reader = null;
				try {
					stream = Files.newInputStream(getFullPath());
					reader = determineInputEncryption(stream);
									
					final JSONParser parser = new JSONParser(new JSONLexer(reader));
					this.content = parser.parse();
					this.isOpen = true;
				}finally {
					if(reader != null) reader.close();
					if(stream != null) stream.close();
				}
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
			if(this.isOpen && (forceSave || this.content.isModified())) {
				OutputStream stream = null;
				BufferedWriter writer = null;
				try {
					stream = Files.newOutputStream(getFullPath());
					writer = determineOutputEncryption(stream);
					
					final JSONWriter jsonWriter = new JSONWriter(this.content);
					jsonWriter.write(writer);
				}finally {
					if(writer != null) {
						writer.flush();
						writer.close();
					}
					if(stream != null) {
						stream.flush();
						stream.close();
					}
				}
				
			}
		}
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
	
	public void saveFile() throws IOException {
		this.saveFile(false);
	}
	
	public void reset() {
		synchronized (mutex) {
			this.content = null;			
		}
	}
	
	public void setPath(Directory dir) {
		synchronized (mutex) {
			this.directory = dir;
		}
	}
	
	public JSONContainer getContent() {
		return content;
	}
	
	public JSONObject getContentAsObject() {
		if(this.content instanceof JSONObject) return (JSONObject) this.content;
		else return null;
	}
	
	public JSONArray getContentAsArray() {
		if(this.content instanceof JSONArray) return (JSONArray) this.content;
		else return null;
	}
	
	@Override
	public String getFullName() {
		return this.fileName+".json";
	}

	@Override
	public FileType getFileType() {
		return FileType.JSON;
	}
}
