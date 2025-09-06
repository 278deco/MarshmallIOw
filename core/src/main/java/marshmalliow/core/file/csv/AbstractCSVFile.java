package marshmalliow.core.file.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import marshmalliow.core.file.AbstractFile;
import marshmalliow.core.file.FileType;

public class AbstractCSVFile extends AbstractFile {
	
	private CSVDocument document;
	private String separator;
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean hasBeenRead = false; // Indicates if the file has been read at least once from disk. Does not reset after a save.
	
	public AbstractCSVFile(AbstractCSVFileBuilder<?> builder) {
		super(builder.directory, builder.name, null);
	}

	@Override
	public void readFile(boolean forceRead) throws IOException {
		try {
			lock.writeLock().lock(); // We lock using write lock so nobody can write or read from memory while we are reading from disk
			
			// Check for the file existence on the disk
			final boolean exists = directory.exists(fileName) && directory.size(fileName) > 0;
			final boolean canRead = directory.isReadable(fileName);
			
			// If the content has been modified and we are not forcing a read, we do not read the file again because it would overwrite the modifications.
			if(this.document.isModified() && !forceRead) {
				throw new IOException("The content has been modified and cannot be read again without forcing a read.");
			}
			
			// If the file exists and is readable, we read it
			if(exists && canRead) {
				try (final BufferedReader reader = new BufferedReader(new InputStreamReader(directory.openInputStream(fileName), "UTF-8"))) {
					String line;
					while( (line = reader.readLine()) != null) {
						this.document.addRow(parseRow(line, this.separator));
					}
		        } catch (IOException e) {
		        	throw new IOException("Failed to read the text file: " + e.getMessage(), e);
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
			
			if(!this.document.isModified() && !forceSave) {
				throw new IOException("The content has not been modified and cannot be saved again without forcing a save.");
			}
			
			try(final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(directory.openOutputStream(fileName)))) {

				for(int i = 0; i < document.getRowCount(); i++) {
					writer.write(formatRow(document.getRow(i), separator));
					if(i != document.getRowCount()-1) writer.newLine();
				}
				
			} catch (IOException e) {
				throw new IOException("Failed to write the text file: " + e.getMessage(), e);
			}
			this.document.resetModified();
		} finally {
			lock.writeLock().unlock();
		}	
	}
	
	public void saveFile() throws IOException {
		this.saveFile(false);
	}
	
	private String formatRow(List<CSVCell> row, String separator) {
		final StringBuilder sb = new StringBuilder();
		row.forEach(cell -> sb.append(cell.getContent()).append(";"));
		sb.deleteCharAt(sb.length()-1);
		
		return sb.toString();
	}
	
	private List<CSVCell> parseRow(final  String line, final String separator) throws MissingFormatArgumentException {
		final List<CSVCell> result = new ArrayList<>();
		
		int offsetSepa = 0; int indexSepa = 0;
		int indexCancel = 0; int offsetCancel = 0;
		
		while((indexSepa = line.indexOf(separator, offsetSepa)) != -1) {
			if((indexCancel = line.indexOf("\"", offsetCancel)) != -1 && indexCancel < indexSepa) {
				final int endCancel = line.indexOf("\"",indexCancel+1)+1; //Search the second ' in the string
				if(endCancel == 0) throw new MissingFormatArgumentException("Couldn't find the second apostrophe needed to close the argument");
				
				result.add(new CSVCell(line.substring(indexCancel, endCancel)));
				offsetSepa = (endCancel==line.length() ? endCancel : endCancel+1);
				offsetCancel = endCancel;
			}else {
				result.add(new CSVCell(line.substring(offsetSepa, indexSepa)));
				offsetSepa = indexSepa+1;
			}
		}
		if(offsetSepa != line.length()) result.add(new CSVCell(line.substring(offsetSepa, line.length())));
		
		return result;
	}

	@Override
	public FileType getFileType() {
		return FileType.CSV;
	}
}
