package marshmalliow.core.binary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import marshmalliow.core.binary.data.MOBFFileHeader;
import marshmalliow.core.binary.data.container.ObjectDataType;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.CompressionType;
import marshmalliow.core.file.AbstractFile;
import marshmalliow.core.file.FileType;
import marshmalliow.core.file.ReadMode;
import marshmalliow.core.file.ReadResult;
import marshmalliow.core.file.SaveMode;
import marshmalliow.core.file.SaveResult;
import marshmalliow.core.io.BinaryReader;
import marshmalliow.core.io.BinaryWriter;

public class AbstractMOBFFile extends AbstractFile {
		
	private MOBFFileHeader fileHeader;
	private CompressionType compression;
	private DataTypeRegistry dataTypeRegistry;
		
	private ObjectDataType root;
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean hasBeenRead = false; // Indicates if the file has been read at least once from disk. Does not reset after a save.
	
	protected AbstractMOBFFile(AbstractMOBFFileBuilder<?> builder) {
		super(builder.directory, builder.name, null);
		this.compression = builder.compression;
		this.dataTypeRegistry = builder.dataTypeRegistry;
		this.fileHeader = builder.header;
		this.root = new ObjectDataType();
	}
	
	@Override
	public ReadResult readFile(ReadMode mode) throws IOException {
		try {
			lock.writeLock().lock(); // We lock using write lock so nobody can write or read from memory while we are reading from disk
			
			// Check for the file existence on the disk
			final boolean exists = directory.exists(this.fileName) && directory.size(this.fileName) > 0;
			final boolean canRead = directory.isReadable(this.fileName);
			
			// If the content has been modified and we are not forcing a read, we do not read the file again because it would overwrite the modifications.
			if(this.root.isModified() && !mode.equals(ReadMode.FORCE)) {
				return ReadResult.SKIPPED_MODIFIED;
			}
			
			// If the file exists and is readable, we read it
			if(exists && canRead) {
				BinaryReader reader = null;
				BufferedInputStream stream = null;
				try {
					stream = new BufferedInputStream(directory.openInputStream(this.fileName));
					
					// If the compression is set to automatic, we try to determine it from the file signature
					// If the compression is set to a specific type, we use that type
					if(this.compression != CompressionType.AUTOMATIC) {
						reader = new BinaryReader(getCompressionStream(stream));
					}else {
						reader = new BinaryReader(determineInputCompression(stream));
					}	
					
					this.fileHeader = new MOBFFileHeader(reader);
					
					if(reader.readByte() != DataTypeEnum.OBJECT.getId()) throw new IOException();
					
					this.root = new ObjectDataType(reader.readUTF(this.fileHeader.getEncodingCharset()));
					this.root.read(reader, dataTypeRegistry, this.fileHeader.getEncodingCharset());
					this.hasBeenRead = true; // Mark as read
					return ReadResult.READ;
				}finally {
					if(reader != null) reader.close();
					if(stream != null) stream.close();
				}
			}else if(!exists) {
				this.hasBeenRead = false; // If the file does not exist, mark as not read
				return ReadResult.FILE_NOT_FOUND;
			}else {
				throw new IOException("The file is not readable or does not exist.");
			}
			
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	private InputStream determineInputCompression(BufferedInputStream bis) throws IOException {
		CompressionType compression = CompressionType.NONE;
		bis.mark(0);

		for(CompressionType ct : CompressionType.values()) {
			if(!ct.isRealAlgorithm()) continue;
			
			final byte[] sign = bis.readNBytes(ct.getSignatureLength());
			bis.reset();
			
			if(Arrays.equals(sign, ct.getSignature())) {
				compression = ct;
				break;
			}
		}
		this.compression = compression; // Save the detected compression type
		
		return getCompressionStream(bis);
	}
	
	private InputStream getCompressionStream(BufferedInputStream bis) throws IOException {
		switch (this.compression) {
			case GZIP:
				return new GZIPInputStream(bis);
			default:
				return bis;
		}
	}
	
	@Override
	public SaveResult saveFile(SaveMode mode) throws IOException {
		try {
			lock.writeLock().lock(); // We lock using write lock so nobody can write or read from memory while we are reading from disk
			
			final boolean exists = directory.exists(this.fileName) && directory.size(this.fileName) > 0;
			
			if(!mode.equals(SaveMode.OVERWRITE) && exists && !this.hasBeenRead) {
				return SaveResult.SKIPPED_NOT_LOADED;
			}
			
			// If the content has not been modified and we are not forcing a save, we do not save the file again.
			if(!this.root.isModified() && exists && !mode.equals(SaveMode.FORCE)) {
				return SaveResult.SKIPPED_NO_CHANGES;
			}
			
			BinaryWriter writer = null;
			BufferedOutputStream stream = null;
			try {
				stream = new BufferedOutputStream(directory.openOutputStream(this.fileName));
				writer = new BinaryWriter(determineOutputCompression(stream));
				
				this.fileHeader.write(writer);
				
				writer.writeByte(DataTypeEnum.OBJECT.getId());
				writer.writeUTF(this.root.getName().isPresent() ? this.root.getName().get() : "", this.fileHeader.getEncodingCharset());
				
				this.root.write(writer, dataTypeRegistry, this.fileHeader.getEncodingCharset());
				return SaveResult.SAVED;
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
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	private OutputStream determineOutputCompression(BufferedOutputStream bis) throws IOException {
		switch (this.compression) {
			case GZIP:
				return new GZIPOutputStream(bis);
			default:
				return bis;
		}
	}
	
	public ObjectDataType getRoot() {
		try {
			lock.readLock().lock();
			return root;
		}finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public FileType getFileType() {
		return FileType.MOBF;
	}
}
