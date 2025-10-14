package marshmalliow.core.file;

import marshmalliow.core.binary.AbstractMOBFFile;
import marshmalliow.core.binary.AbstractMOBFFileBuilder;
import marshmalliow.core.binary.data.MOBFFileHeader;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.CompressionType;
import marshmalliow.core.directory.Directory;
import marshmalliow.core.directory.DirectoryRegistry;

public class MOBFFile extends AbstractMOBFFile {

	protected MOBFFile(AbstractMOBFFileBuilder<?> builder) {
		super(builder);
	}

	public static class MOBFFileBuilder extends AbstractMOBFFileBuilder<MOBFFile> {
		
		private final DirectoryRegistry registry;		
		private MOBFFileBuilder(DirectoryRegistry registry) { this.registry = registry; }
		
		public static MOBFFileBuilder builder(DirectoryRegistry registry) { return new MOBFFileBuilder(registry); }
		
		public MOBFFileBuilder directory(Directory d) { this.directory = d; return this; }
		public MOBFFileBuilder directoryId(String id) { this.directoryId = id; return this; }
		public MOBFFileBuilder registry(DataTypeRegistry r) { this.dataTypeRegistry = r; return this; }
		public MOBFFileBuilder header(MOBFFileHeader h) { this.header = h; return this; }
		public MOBFFileBuilder compression(CompressionType c) { this.compression = c; return this; }
		public MOBFFileBuilder name(String n) {
        	if(!n.toLowerCase().endsWith(FileType.MOBF.getExtension())) n += FileType.MOBF.getExtension();
			this.name = n; return this;
		}
		
		@Override
		public MOBFFile build() { 
			if(this.directory == null && directoryId != null) {
				this.directory = registry.get(directoryId).orElseThrow(() -> new IllegalStateException("No directory found with id: " + directoryId));
			}
			if(this.directory == null) throw new IllegalStateException("No directory provided or resolved by registry.");
			
			return new MOBFFile(this); 
		}
	}
	
}
