package marshmalliow.core.binary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import marshmalliow.core.binary.data.MOBFFileHeader;
import marshmalliow.core.binary.data.types.container.ObjectDataType;
import marshmalliow.core.binary.io.BinaryReader;
import marshmalliow.core.binary.io.BinaryWriter;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.CompressionType;

public class MOBFFile {

	private static final String EXTENSION = ".mobf";
	
	private final Object lock = new Object();
	
	private Path filePath;
	private MOBFFileHeader fileHeader;
	private CompressionType compression;
	private DataTypeRegistry registry;
	
	private boolean isOpen;
	
	private ObjectDataType root;
	
	public MOBFFile(Path filePath, String fileName, DataTypeRegistry registry) {
		if(!Files.isDirectory(filePath) && (fileName.isBlank() || fileName.isEmpty())) throw new IllegalArgumentException();
		this.filePath = filePath.resolve(fileName+EXTENSION);
		this.registry = registry;
		this.isOpen = false;
	}
	
	public MOBFFile(Path filePath, String fileName, DataTypeRegistry registry, MOBFFileHeader header, CompressionType compression, ObjectDataType root) {
		if(!Files.isDirectory(filePath) && (fileName.isBlank() || fileName.isEmpty())) throw new IllegalArgumentException();
		this.filePath = filePath.resolve(fileName+EXTENSION);
		this.compression = compression;
		this.registry = registry;
		this.fileHeader = header;
		this.root = root;
		
		this.isOpen = true;
	}
	
	public MOBFFile(Path filePath, String fileName, DataTypeRegistry registry, MOBFFileHeader header, CompressionType compression) {
		this(filePath, fileName, registry, header, compression, new ObjectDataType());
	}
	
	public MOBFFile(Path filePath, String fileName, DataTypeRegistry registry, MOBFFileHeader header, ObjectDataType root) {
		this(filePath, fileName, registry, header, CompressionType.NONE, root);
	}
	
	public MOBFFile(Path filePath, String fileName, DataTypeRegistry registry, MOBFFileHeader header) {
		this(filePath, fileName, registry, header, CompressionType.NONE, new ObjectDataType());
	}
	
	public void readFile(boolean forceRead) throws IOException {
		synchronized (lock) {
			// True if we force the overwriting of the loaded data or the document hasn't been opened
			if(forceRead || !this.isOpen) {
				BinaryReader reader = null;
				try {
					BufferedInputStream stream = new BufferedInputStream(Files.newInputStream(filePath));
					
					reader = new BinaryReader(determineCompression(stream));
	
					this.fileHeader = new MOBFFileHeader(reader);
					
					if(reader.readByte() != DataTypeEnum.OBJECT.getId()) throw new IOException();
					
					this.root = new ObjectDataType(reader.readUTF(this.fileHeader.getEncodingCharset()));
					this.root.read(reader, registry, this.fileHeader.getEncodingCharset());
					this.isOpen = true;
				}finally {
					if(reader != null) reader.close();
				}
			}
		}
	}
	
	public void readFile() throws IOException {
		this.readFile(false);
	}
	
	private InputStream determineCompression(BufferedInputStream bis) throws IOException {
		CompressionType compression = CompressionType.NONE;
		bis.mark(0);

		for(CompressionType ct : CompressionType.values()) {
			if(ct != CompressionType.NONE) {
				final byte[] sign = bis.readNBytes(ct.getSignatureLength());
				bis.reset();
				
				if(Arrays.equals(sign, CompressionType.GZIP.getSignature())) {
					compression = ct;
					break;
				}
			}
		}
		
		switch (compression) {
			case GZIP:
				return new GZIPInputStream(bis);
			default:
				return bis;
		}
	}
	
	public void writeFile(boolean forceWrite) throws IOException {
		synchronized (lock) {
			if(this.fileHeader == null || this.root == null) throw new IllegalStateException("Cannot write a MOBF file without an header or a content");
			
			// True if we force the overwriting of the loaded data or the document hasn't been opened
			if(this.isOpen && (forceWrite || this.root.isModified())) {
				BinaryWriter writer = null;
				try {
					final BufferedOutputStream stream = new BufferedOutputStream(Files.newOutputStream(filePath));
					writer = new BinaryWriter(this.compression == CompressionType.NONE ? stream : new GZIPOutputStream(stream));
					
					this.fileHeader.write(writer);
					
					writer.writeByte(DataTypeEnum.OBJECT.getId());
					writer.writeUTF(this.root.getName().isPresent() ? this.root.getName().get() : "", this.fileHeader.getEncodingCharset());
					
					this.root.write(writer, registry, this.fileHeader.getEncodingCharset());
				}finally {
					if(writer != null) {
						writer.flush();
						writer.close();
					}
				}
			}
		}
	}
	
	public void writeFile() throws IOException {
		this.writeFile(false);
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	public ObjectDataType getRoot() {
		return root;
	}
}
