package marshmalliow.core.file;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import marshmalliow.core.directory.Directory;
import marshmalliow.core.security.FileCredentials;
import reactor.util.annotation.Nullable;

public abstract class AbstractFile {
	
	protected final UUID id;
	
	protected Directory directory;
	protected String fileName;
	protected FileCredentials credentials;
	
	public AbstractFile(Directory dir, String name, @Nullable FileCredentials credentials) {
		this.id = UUID.randomUUID();
		this.directory = dir;
		this.fileName = name;
		
		if(credentials == null) {
			this.credentials = FileCredentials.EMPTY;
		} else {
			this.credentials = credentials;
		}		
	}

	public abstract ReadResult readFile(ReadMode mode) throws IOException;
	public abstract SaveResult saveFile(SaveMode mode) throws IOException;
	
	public abstract FileType getFileType();
	
	/**
	 * Get the Universally unique identifier (UUID) of the file 
	 * @return an UUID
	 */
	public final UUID getId() {
		return id;
	}
	
	public final URI uri() {
		return this.directory.resolve(fileName);
	}
	
	public final Optional<Path> asPath() {
		final URI uri = this.uri();
		if("file".equals(uri.getScheme())) {
			return Optional.of(Path.of(uri));
		}
		return Optional.empty();
	}
	
	/**
	 * Get the file name if present, else optional while be empty
	 * @return the file name
	 */
	public final String getFileName() {
		return this.fileName;
	}
	
	public final FileCredentials getCredentials() {
		return credentials;
	}

	@Override
	public final int hashCode() {
		int result = this.fileName.hashCode();
		result = 31 * result + this.getFileType().ordinal();
		result = 31 * result + this.directory.hashCode();
		
		return result;
	}
	
	@Override
	public String toString() {
		return "File{id=" + id + ", type=" + getFileType() + ", name=" + fileName + ", directory=" + directory.id() + "}";
	}
}
