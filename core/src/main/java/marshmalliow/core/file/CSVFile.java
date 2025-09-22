package marshmalliow.core.file;

import marshmalliow.core.directory.Directory;
import marshmalliow.core.directory.DirectoryRegistry;
import marshmalliow.core.file.csv.AbstractCSVFile;
import marshmalliow.core.file.csv.AbstractCSVFileBuilder;
import marshmalliow.core.file.csv.CSVProperties;

public class CSVFile extends AbstractCSVFile {

	protected CSVFile(AbstractCSVFileBuilder<?> builder) {
		super(builder);
	}
	
	public static class CSVFileBuilder extends AbstractCSVFileBuilder<CSVFile> {
		
		private final DirectoryRegistry registry;
		private CSVFileBuilder(DirectoryRegistry registry) { this.registry = registry; }
		
		public static CSVFileBuilder builder(final DirectoryRegistry registry) { return new CSVFileBuilder(registry); }			
		
        public CSVFileBuilder directory(Directory d) { this.directory = d; return this; }
        public CSVFileBuilder directoryId(String id) { this.directoryId = id; return this; }
        public CSVFileBuilder name(String n) { this.name = n; return this; }
        public CSVFileBuilder properties(CSVProperties p) { this.properties = p; return this; }

        @Override
        public CSVFile build() {
        	if(this.name == null || this.name.isBlank()) throw new IllegalStateException("File name cannot be null or blank.");
			if(this.directory == null && directoryId != null) {
				this.directory = registry.get(directoryId).orElseThrow(() -> new IllegalStateException("No directory found with id: " + directoryId));
			}
			if(this.directory == null) throw new IllegalStateException("No directory provided or resolved by registry.");
			
			return new CSVFile(this); 
        }
    }
	

}
