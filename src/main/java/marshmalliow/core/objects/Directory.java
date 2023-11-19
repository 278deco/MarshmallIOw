package marshmalliow.core.objects;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Directory {
	
	private String name;
	private Path directory;
	
	/**
	 * Create a new instance of {@link Directory}.<br/>
	 * The {@link String} representation of the path is used as the name of this instance.<br/>
	 * The name is only used inside the program and doesn't require to be equal to the path's name on the disk.
	 * @param pathDir a {@link String} representation of a path to a file on the disk
	 */
	public Directory(String pathDir) {
		final Path temp = Paths.get(pathDir);
		if(Files.exists(temp) && !Files.isDirectory(temp)) throw new InvalidPathException(pathDir, "The directory cannot be constructed with a file path");
			
		this.directory = temp;
		this.name = pathDir;
	}
	
	/**
	 * Create a new instance of {@link Directory}.<br/>
	 * The {@link String} representation of the path is used as the name of this instance.<br/>
	 * The name is only used inside the program and doesn't require to be equal to the path's name on the disk.
	 * @param path The {@link Path} of the directory
	 */
	public Directory(Path path) {
		if(Files.exists(path) && !Files.isDirectory(path)) throw new InvalidPathException(path.toString(), "The directory cannot be constructed with a file path");
		this.name = path.toString();
		this.directory = path;
	}
	
	/**
	 * Create a new instance of {@link Directory}.<br/>
	 * The name is only used inside the program and doesn't require to be equal to the path's name on the disk.
	 * @param name The given name of the directory
	 * @param pathDir a {@link String} representation of a path
	 */
	public Directory(String name, String pathDir) {
		final Path temp = Paths.get(pathDir);
		if(Files.exists(temp) && !Files.isDirectory(temp)) throw new InvalidPathException(pathDir, "The directory cannot be constructed with a file path");
		
		this.directory = temp;
		this.name = name;
	}
	
	/**
	 * Create a new instance of {@link Directory}.<br/>
	 * The name is only used inside the program and doesn't require to be equal to the path's name on the disk.
	 * @param name The given name of the directory
	 * @param path The {@link Path} of the directory
	 */
	public Directory(String name, Path path) {
		if(Files.exists(path) && !Files.isDirectory(path)) throw new InvalidPathException(path.toString(), "The directory cannot be constructed with a file path");
		
		this.directory = path;
		this.name = name;
	}
	
	/**
	 * Get the name identifier of this instance of {@link Directory}.
	 * @return The directory's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the path of the this instance of {@link Directory}
	 * @return The directory's path
	 * @see Path
	 */
	public Path getPath() {
		return directory;
	}
	
	/**
	 * Check if the given path is similar to this instance's path
	 * @param path A {@link Path} instance to be compared
	 * @return If the two paths are equals
	 */
	public boolean isPathSimilar(Path path) {
		return path.equals(this.getPath());
	}
	
	/**
	 * Check if the given name is similar to this instance's name
	 * @param nameID the name to be compared
	 * @return If the two names are equals
	 */
	public boolean isNameSimilar(String nameID) {
		return nameID.equals(this.getName());
	}
	
	@Override
	public int hashCode() {
		int result = this.name.hashCode();
		result = 31 * result + this.directory.hashCode();
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Directory && areEquals((Directory)obj);
	}

	private boolean areEquals(Directory obj) {
		return isPathSimilar(obj.getPath()) && obj.getName().equalsIgnoreCase(this.getName());
	}
}
