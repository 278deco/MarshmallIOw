package marshmalliow.core.file.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import marshmalliow.core.file.AbstractFile;
import marshmalliow.core.file.FileType;
import marshmalliow.core.io.CSVLexer;
import marshmalliow.core.io.CSVParser;
import marshmalliow.core.io.CSVWriter;

public class AbstractCSVFile extends AbstractFile {
	
	private CSVDocument document;
	private CSVProperties properties;
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean hasBeenRead = false; // Indicates if the file has been read at least once from disk. Does not reset after a save.
	
	public AbstractCSVFile(AbstractCSVFileBuilder<?> builder) {
		super(builder.directory, builder.name, null);
		this.properties = builder.properties;
	}

	@Override
	public void readFile(boolean forceRead) throws IOException {
		try {
			lock.writeLock().lock(); // We lock using write lock so nobody can write or read from memory while we are reading from disk
			
			// Check for the file existence on the disk
			final boolean exists = directory.exists(fileName) && directory.size(fileName) > 0;
			final boolean canRead = directory.isReadable(fileName);
			
			// If the content has been modified and we are not forcing a read, we do not read the file again because it would overwrite the modifications.
			if(this.document != null && this.document.isModified() && !forceRead) {
				throw new IOException("The content has been modified and cannot be read again without forcing a read.");
			}
			
			// If the file exists and is readable, we read it
			if(exists && canRead) {
				try (final BufferedReader reader = new BufferedReader(new InputStreamReader(this.directory.openInputStream(this.fileName))) ) {
					final CSVLexer lexer = new CSVLexer(reader, properties);
					final CSVParser parser = new CSVParser(lexer);
					this.document = parser.parse();
		        } catch (IOException e) {
		        	throw new IOException("Failed to read the CSV file: " + e.getMessage(), e);
		        }
				this.document.resetModified();
				this.hasBeenRead = true; // Mark as read
			}else if (!exists) {
				this.hasBeenRead = false; // If the file does not exist, mark as not read
			} else {
				throw new IOException("The file is not readable or does not exist.");
			}
			
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public void readFile() throws IOException {
		this.readFile(false);
	}

	@Override
	public void saveFile(boolean forceSave) throws IOException {
		try {
			lock.writeLock().lock(); // We lock using write lock so nobody can write or read from memory while we are writing to disk
			
			final boolean exists = directory.exists(fileName) && directory.size(fileName) > 0;
			
			if(!forceSave && exists && !this.hasBeenRead) {
				throw new IOException("The file has not been read before saving. Please read the file first or force the save.");
			}
			
			if(this.document != null && !this.document.isModified() && !forceSave) {
				throw new IOException("The content has not been modified and cannot be saved again without forcing a save.");
			}
			
			try(final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(directory.openOutputStream(fileName)))) {
				final CSVWriter csvWriter = new CSVWriter(this.document, this.properties);
				csvWriter.write(writer);
			} catch (IOException e) {
				throw new IOException("Failed to write the CSV file: " + e.getMessage(), e);
			}
			this.document.resetModified();
		} finally {
			lock.writeLock().unlock();
		}	
	}
	
	public void saveFile() throws IOException {
		this.saveFile(false);
	}
	
	public CSVDocument getDocument() {
		try {
			lock.readLock().lock();
			return this.document;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public FileType getFileType() {
		return FileType.CSV;
	}
}
