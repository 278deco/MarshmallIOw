package marshmalliow.core.binary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import marshmalliow.core.binary.data.MOBFFileHeader;
import marshmalliow.core.binary.data.container.ObjectDataType;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.CompressionType;
import marshmalliow.core.io.BinaryReader;
import marshmalliow.core.io.BinaryWriter;
import marshmalliow.core.objects.Directory;
import marshmalliow.core.objects.FileType;
import marshmalliow.core.objects.IOClass;

public class MOBFFile extends IOClass {

	private static final String EXTENSION = ".mobf";
		
	private MOBFFileHeader fileHeader;
	private CompressionType compression;
	private DataTypeRegistry registry;
		
	private ObjectDataType root;
	
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean hasBeenRead = false; // Indicates if the file has been read at least once from disk. Does not reset after a save.
	
	protected MOBFFile(MOBFFileBuilder<?> builder) {
		super(builder.directory, builder.name, null);
		this.compression = builder.compression;
		this.registry = builder.registry;
		this.fileHeader = builder.header;
		this.root = new ObjectDataType();
	}
	
	@Override
	public void readFile(boolean forceRead) throws IOException {
		try {
			lock.writeLock().lock(); // We lock using write lock so nobody can write or read from memory while we are reading from disk
			
			// Check for the file existence on the disk
			final boolean exists = Files.exists(getFullPath()) && Files.size(getFullPath()) > 0;
			final boolean canRead = Files.isReadable(getFullPath());
			
			// If the content has been modified and we are not forcing a read, we do not read the file again because it would overwrite the modifications.
			if(this.root.isModified() && !forceRead) {
				throw new IOException("The content has been modified and cannot be read again without forcing a read.");
			}
			
			// If the file exists and is readable, we read it
			if(exists && canRead) {
				BinaryReader reader = null;
				BufferedInputStream stream = null;
				try {
					stream = new BufferedInputStream(Files.newInputStream(getFullPath()));
					
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
					this.root.read(reader, registry, this.fileHeader.getEncodingCharset());
				}finally {
					if(reader != null) reader.close();
					if(stream != null) stream.close();
				}
			}else if(!exists) {
				this.hasBeenRead = false; // If the file does not exist, mark as not read
			}else {
				throw new IOException("The file is not readable or does not exist.");
			}
			
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public void readFile() throws IOException {
		this.readFile(false);
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
	public void saveFile(boolean forceSave) throws IOException {
		try {
			lock.writeLock().lock(); // We lock using write lock so nobody can write or read from memory while we are reading from disk
			
			final boolean exists = Files.exists(getFullPath()) && Files.size(getFullPath()) > 0;
			
			if(!forceSave && exists && !this.hasBeenRead) {
				throw new IOException("The file has not been read before saving. Please read the file first or force the save.");
			}
			
			// If the content has not been modified and we are not forcing a save, we do not save the file again.
			if(!this.root.isModified() && !forceSave) {
				throw new IOException("The content has not been modified and cannot be saved again without forcing a save.");
			}
			
			BinaryWriter writer = null;
			BufferedOutputStream stream = null;
			try {
				stream = new BufferedOutputStream(Files.newOutputStream(getFullPath()));
				writer = new BinaryWriter(determineOutputCompression(stream));
				
				this.fileHeader.write(writer);
				
				writer.writeByte(DataTypeEnum.OBJECT.getId());
				writer.writeUTF(this.root.getName().isPresent() ? this.root.getName().get() : "", this.fileHeader.getEncodingCharset());
				
				this.root.write(writer, registry, this.fileHeader.getEncodingCharset());
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
	
	public void saveFile() throws IOException {
		this.saveFile(false);
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
	
	@Override
	public String getFileWithExtension() {
		return this.fileName+EXTENSION;
	}
	
	public static <T extends MOBFFile> MOBFFileBuilder<T> builder(Class<T> clazz) {
		return new MOBFFileBuilder<>(clazz);
	}
	
	public static MOBFFileBuilder<MOBFFile> builder() {
		return new MOBFFileBuilder<>(MOBFFile.class);
	}
	
	public static class MOBFFileBuilder<T extends MOBFFile> {
		private final Class<T> clazz;
		
		private Directory directory;
		private String name;
		private DataTypeRegistry registry;
		
		private MOBFFileHeader header = MOBFFileHeader.DEFAULT_HEADER;
		private CompressionType compression = CompressionType.AUTOMATIC;
		
		public MOBFFileBuilder(Class<T> clazz) {
			this.clazz = clazz;
		}
		
		public MOBFFileBuilder<T> directory(Directory dir) {
			this.directory = dir;
			return this;
		}
		
		public MOBFFileBuilder<T> name(String name) {
			this.name = name;
			return this;
		}
		
		public MOBFFileBuilder<T> registry(DataTypeRegistry registry) {
			this.registry = registry;
			return this;
		}
		
		public MOBFFileBuilder<T> header(MOBFFileHeader header) {
			this.header = header;
			return this;
		}
		
		public MOBFFileBuilder<T> compression(CompressionType compression) {
			this.compression = compression;
			return this;
		}

		public T build() {
			if(this.directory == null || this.name == null) throw new IllegalArgumentException("The directory or name cannot be null");
			
			try {
				return clazz.getConstructor(MOBFFileBuilder.class)
						.newInstance(this);
			}catch(ReflectiveOperationException e) {
				throw new RuntimeException("Failed to create MOBFFile instance", e);
			}
		}
	}
}
