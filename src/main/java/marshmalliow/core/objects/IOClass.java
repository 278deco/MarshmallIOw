package marshmalliow.core.objects;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import marshmalliow.core.security.FileCredentials;

public abstract class IOClass {
	
	protected final UUID id;
	
	protected Directory directory;
	protected String fileName;
	protected FileCredentials credentials;
	
	public IOClass(Directory dir, String name, FileCredentials credentials) {
		this.id = UUID.randomUUID();
		this.directory = dir;
		this.fileName = name;
		this.credentials = credentials;
	}
	
	public IOClass(Directory dir, String name) {
		this.id = UUID.randomUUID();
		this.directory = dir;
		this.fileName = name;
		this.credentials = FileCredentials.EMPTY;
	}
	
	public abstract void readFile(boolean forceRead) throws IOException;
	public abstract void saveFile(boolean forceSave) throws IOException;
	
	public abstract FileType getFileType();
	
	/**
	 * Get the Universally unique identifier (UUID) of the file 
	 * @return an UUID
	 */
	public UUID getId() {
		return id;
	}
	
	/**
	 * Get the absolute file path 
	 * @return the file path
	 */
	public Directory getDirectory() {
		return this.directory;
	}
	
	/**
	 * Get the file name if present, else optional while be empty
	 * @return the file name
	 */
	public String getFileName() {
		return this.fileName;
	}
	
	public FileCredentials getCredentials() {
		return credentials;
	}
	
	public abstract String getFullName();
	
	public Path getFullPath() {
		return this.directory.getPath().resolve(getFullName());
	}
	
	@Override
	public int hashCode() {
		int result = this.fileName.hashCode();
		result = 31 * result + this.getFileType().ordinal();
		result = 31 * result + this.getDirectory().hashCode();
//		result = 31 * result + this.getCredentials().hashCode();
		
		return result;
	}
}
