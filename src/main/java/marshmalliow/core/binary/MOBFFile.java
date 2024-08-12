package marshmalliow.core.binary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
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
	
	private final Object lock = new Object();
	
	private MOBFFileHeader fileHeader;
	private CompressionType compression;
	private DataTypeRegistry registry;
	
	private boolean isOpen;
	
	private ObjectDataType root;
	
	public MOBFFile(Directory fileDir, String fileName, DataTypeRegistry registry) {
		super(fileDir, fileName);
		this.registry = registry;
		this.isOpen = false;
	}
	
	public MOBFFile(Directory fileDir, String fileName, DataTypeRegistry registry, MOBFFileHeader header, CompressionType compression, ObjectDataType root) {
		super(fileDir, fileName);
		this.compression = compression;
		this.registry = registry;
		this.fileHeader = header;
		this.root = root;
		
		this.isOpen = true;
	}
	
	public MOBFFile(Directory fileDir, String fileName, DataTypeRegistry registry, MOBFFileHeader header, CompressionType compression) {
		this(fileDir, fileName, registry, header, compression, new ObjectDataType());
	}
	
	public MOBFFile(Directory fileDir, String fileName, DataTypeRegistry registry, MOBFFileHeader header, ObjectDataType root) {
		this(fileDir, fileName, registry, header, CompressionType.NONE, root);
	}
	
	public MOBFFile(Directory fileDir, String fileName, DataTypeRegistry registry, MOBFFileHeader header) {
		this(fileDir, fileName, registry, header, CompressionType.NONE, new ObjectDataType());
	}
	
	@Override
	public void readFile(boolean forceRead) throws IOException {
		synchronized (lock) {
			// True if we force the overwriting of the loaded data or the document hasn't been opened
			if(forceRead || !this.isOpen) {
				BinaryReader reader = null;
				BufferedInputStream stream = null;
				try {
					stream = new BufferedInputStream(Files.newInputStream(getFullPath()));
							
					reader = new BinaryReader(determineInputCompression(stream));
	
					this.fileHeader = new MOBFFileHeader(reader);
					
					if(reader.readByte() != DataTypeEnum.OBJECT.getId()) throw new IOException();
					
					this.root = new ObjectDataType(reader.readUTF(this.fileHeader.getEncodingCharset()));
					this.root.read(reader, registry, this.fileHeader.getEncodingCharset());
					this.isOpen = true;
				}finally {
					if(reader != null) reader.close();
					if(stream != null) stream.close();
				}
			}
		}
	}
	
	public void readFile() throws IOException {
		this.readFile(false);
	}
	
	@Override
	public void saveFile(boolean forceWrite) throws IOException {
		synchronized (lock) {
			if(this.fileHeader == null || this.root == null) throw new IllegalStateException("Cannot write a MOBF file without an header or a content");
			
			// True if we force the overwriting of the loaded data or the document hasn't been opened
			if(this.isOpen && (forceWrite || this.root.isModified())) {
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
			}
		}
	}
	
	public void saveFile() throws IOException {
		this.saveFile(false);
	}
	
	private InputStream determineInputCompression(BufferedInputStream bis) throws IOException {
		CompressionType compression = CompressionType.NONE;
		bis.mark(0);

		for(CompressionType ct : CompressionType.values()) {
			if(ct != CompressionType.NONE) {
				final byte[] sign = bis.readNBytes(ct.getSignatureLength());
				bis.reset();
				
				if(Arrays.equals(sign, ct.getSignature())) {
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
	
	private OutputStream determineOutputCompression(BufferedOutputStream bis) throws IOException {
		switch (this.compression) {
			case GZIP:
				return new GZIPOutputStream(bis);
			default:
				return bis;
		}
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	public ObjectDataType getRoot() {
		return root;
	}

	@Override
	public FileType getFileType() {
		return FileType.MOBF;
	}

	@Override
	public String getFullName() {
		return this.fileName+EXTENSION;
	}
}
