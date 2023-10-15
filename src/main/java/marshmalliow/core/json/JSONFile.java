package marshmalliow.core.json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;

import marshmalliow.core.json.io.JSONWriter;
import marshmalliow.core.json.objects.JSONArray;
import marshmalliow.core.json.objects.JSONContainer;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.json.parser.JSONLexer;
import marshmalliow.core.json.parser.JSONParser;
import marshmalliow.core.objects.Directory;
import marshmalliow.core.objects.FileType;
import marshmalliow.core.objects.IOClass;

public class JSONFile extends IOClass {
	
	private JSONContainer content;
	
	private final Object mutex = new Object();
	
	private boolean isOpen;
	
	public JSONFile(Directory dir, String name, JSONContainer content) {
		super(dir, name);
		this.content = content;
		this.isOpen = true;
	}
	
	public JSONFile(Directory dir, String name) {
		super(dir, name);
		this.content = null;
		this.isOpen = false;
	}
	
	@Override
	public void readFile(boolean forceRead) throws IOException {
		synchronized (mutex) {
			if(forceRead || !this.isOpen) {
				BufferedReader reader = null;
				try {
					reader = Files.newBufferedReader(getFullPath());
					
					final JSONParser parser = new JSONParser(new JSONLexer(reader));
					
					this.content = parser.parse();
				}finally {
					if(reader != null) reader.close();
				}
				
			}
		}
	}
	
	public void readFile() throws IOException {
		this.readFile(false);
	}

	@Override
	public void saveFile(boolean forceSave) throws IOException {
		synchronized (mutex) {
			if(this.isOpen && (forceSave || this.content.isModified())) {
				BufferedWriter writer = null;
				try {
					writer = Files.newBufferedWriter(getFullPath());
					
					final JSONWriter jsonWriter = new JSONWriter(this.content);
					jsonWriter.write(writer);
				}finally {
					if(writer != null) {
						writer.flush();
						writer.close();
					}
				}
				
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
