package marshmalliow.core.binary;

import marshmalliow.core.binary.data.MOBFFileHeader;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.CompressionType;
import marshmalliow.core.directory.Directory;

public abstract class AbstractMOBFFileBuilder<T extends AbstractMOBFFile> {
	
	protected Directory directory;
	protected String directoryId;
	protected String name;
	protected DataTypeRegistry dataTypeRegistry;

	protected MOBFFileHeader header = MOBFFileHeader.DEFAULT_HEADER;
	protected CompressionType compression = CompressionType.AUTOMATIC;
	
	public abstract T build();

}
