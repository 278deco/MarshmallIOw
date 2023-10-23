package marshmalliow.core.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import marshmalliow.core.objects.Directory;
import marshmalliow.core.objects.FileType;
import marshmalliow.core.objects.IOClass;

/**
 * Manage text file easily (like .txt file) with reading, writing methods
 * @version 1.0.0
 * @author 278deco
 */
public class TextFile extends IOClass {

	private static final Logger LOGGER = LogManager.getLogger(TextFile.class);
	
	private List<String> content;
	
	/**
	 * Create new TextFile instance
	 * @param args arguments required by the TextFile class
	 * <p><strong>Required arguments :</strong><br>
	 * - The textfile's path<br>
	 * - The textfile's name</p>
	 * @throws IOException
	 */
	public TextFile(Directory dir, String name) {
		super(dir, name);
		
		try {
			
			if(!Files.exists(getFullPath()))
				Files.createFile(getFullPath());
			this.content = new ArrayList<>();
			
			readFile(false);
			
		} catch (IOException e) {
			LOGGER.error("Unexpected error while loading text file [dir: {}, name: {}] with message {}", this.directory.getName(), this.fileName, e.getMessage());
		}
	}
	
	/**
	 * Read and add all file's content in a list
	 * @param forceRead Isn't used in this function
	 * @return true if the file has been read successfully
	 */
	@Override
	public void readFile(boolean forceRead) {
		FileInputStream input = null;
		InputStreamReader fileReader = null;
		BufferedReader buffer = null;
		
		try {
			input = new FileInputStream(this.directory.getPath().resolve(fileName).toFile());
			fileReader = new InputStreamReader(input, "UTF-8");
			buffer = new BufferedReader(fileReader);
			
			if(!this.content.isEmpty()) this.content.clear();
			
			String line;
			while( (line = buffer.readLine()) != null) {
				this.content.add(line);
			}
			
		}catch(IOException e) {
			LOGGER.error("Unexpected error while loading text file [dir: {}, name: {}] with message {}", this.directory.getName(), this.fileName, e.getMessage());
		}finally {
			try { if(input != null) input.close(); }catch(IOException e) {}
			try { if(buffer != null) buffer.close(); }catch(IOException e) {}
			try { if(fileReader != null) fileReader.close(); }catch(IOException e) {}
		}
	}
	
	public void readFile() {
		this.readFile(false);
	}
	
	/**
	 * Write all lines contained in the list to the disk<br>
	 * This method will always return true as the file is saved in his own thread
	 * @param forceSave Isn't used in this function
	 * @return true
	 */
	@Override
	public void saveFile(boolean forceSave) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				FileOutputStream output = null;
				OutputStreamWriter fileWriter = null;
				BufferedWriter buffer = null;
				
				try {
					output = new FileOutputStream(directory.getPath().resolve(fileName).toFile());
					fileWriter = new OutputStreamWriter(output, "UTF-8");
					buffer = new BufferedWriter(fileWriter);
					
					for(int i = 0; i < content.size(); i++) {
						buffer.write(content.get(i));
						if(i != content.size()-1) buffer.newLine();
					}
					
				}catch(IOException e) {
					LOGGER.error("Unexpected error while writing to text file [dir: {}, name: {}] with message {}", directory.getName(), fileName, e.getMessage());
				}finally {
					try { if(output != null) output.close(); }catch(IOException e) {}
					try { if(buffer != null) buffer.close(); }catch(IOException e) {}
					try { if(fileWriter != null) fileWriter.close(); }catch(IOException e) {}
				}
			}
		},"File-Save-Thread").start();
		
	}
	
	public void saveFile() {
		this.saveFile(false);
	}

	/**
	 * Add a new line to content of the file<br>
	 * If the file is never saved, the line while only be added to this instance of the TextFileManager
	 * @param lines all the line which needs to added
	 * @see fr.o278deco.devbot.file.TextFileManager#saveTextFile()
	 */
	public void addNewLine(String... lines) {
		for(String line : lines) {
			if(line != null && line != "" && line.isBlank() && !line.isEmpty()) this.content.add(line);
		}
	}
	
	/**
	 * Clear all file's content
	 */
	public void clearContent() {
		this.content.clear();
	}
	
	/**
	 * Return the content of the i line
	 * @param i The index of the line
	 * @return the line of the file
	 */
	public String getLine(int i) {
		return this.content.size() <= i ? null : this.content.get(i);
	}
	
	/**
	 * Get file's content size
	 * @return the size of the content
	 */
	public int getContentSize() {
		return this.content.size();
	}
	
	/**
	 * Get a random line of file's content
	 * @return a random line of the file
	 */
	public String getRandomLine(Random randomGenerator) {
		return getContentSize() > 0 ? getLine(randomGenerator.nextInt(getContentSize())) : "";
	}

	@Override
	public FileType getFileType() {
		return FileType.PLAIN_TEXT;
	}

	@Override
	public String getFullName() {
		return this.fileName+".txt";
	}
	
}