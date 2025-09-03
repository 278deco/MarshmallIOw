package marshmalliow.core.objects;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import marshmalliow.core.security.FileCredentials;
import reactor.util.annotation.Nullable;

public abstract class IOClass {
	
	protected final UUID id;
	
	protected Directory directory;
	protected String fileName;
	protected FileCredentials credentials;
	
	public IOClass(Directory dir, String name, @Nullable FileCredentials credentials) {
		this.id = UUID.randomUUID();
		this.directory = dir;
		this.fileName = name;
		
		if(credentials == null) {
			this.credentials = FileCredentials.EMPTY;
		} else {
			this.credentials = credentials;
		}		
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
	
	/**
	 * Get the file name with its extension like "file.json" or "file.txt"
	 * @return the file name with its extension
	 */
	public abstract String getFileWithExtension();
	
	/**
	 * Get the full path of the file including the directory and the file name with its extension<br/>
	 * 
	 * This method resolves the file name with its extension against the directory path.
	 * @return the full path of the file
	 * @see Path
	 */
	public Path getFullPath() {
		return this.directory.getPath().resolve(getFileWithExtension());
	}
	
	@Override
	public int hashCode() {
		int result = this.fileName.hashCode();
		result = 31 * result + this.getFileType().ordinal();
		result = 31 * result + this.getDirectory().hashCode();
		
		return result;
	}
}
