package marshmalliow.core.json;

import java.nio.file.Files;
import java.nio.file.Path;

import marshmalliow.core.json.objects.JSONContainer;

public class JSONFile {
	
	private String name;
	private Path path;
	private JSONContainer content;
	
	public JSONFile(Path path, String name, JSONContainer content) {
		if(!Files.isDirectory(path)) throw new IllegalArgumentException("Cannot define a non directory path for a json file");
		
		this.path = path;
		this.name = name;
		this.content = content;
	}
	
	public JSONFile(Path path, String name) {
		this(path, name, null);
	}
	
	public JSONFile(Path path, JSONContainer content) {
		if(!Files.isRegularFile(path)) throw new IllegalArgumentException("Cannot define a non file path for a json file");
		
		this.path = path.getParent();
		this.name = path.getFileName().toString();
		this.content = content;
	}
	
	public JSONFile(Path path) {
		if(!Files.isRegularFile(path)) throw new IllegalArgumentException("Cannot define a non file path for a json file");
		
		this.path = path.getParent();
		this.name = path.getFileName().toString();
		this.content = null;
	}
	
	public synchronized void reset() {
		this.content = null;
	}
	
	public Path getPath() {
		return path;
	}
	
	public Path getFullPath() {
		return path.resolve(getFullName());
	}
	
	public synchronized void setPath(Path path) {
		if(!Files.isDirectory(path)) throw new IllegalArgumentException("Cannot define a non directory path for a json file");
		this.path = path;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFullName() {
		return name+".json";
	}
	
	public synchronized void setName(String name) {
		this.name = name;
	}
	
	public JSONContainer getContent() {
		return content;
	}
}
