package marshmalliow.core.file;

import marshmalliow.core.directory.Directory;
import marshmalliow.core.directory.DirectoryRegistry;
import marshmalliow.core.file.text.AbstractTextFile;
import marshmalliow.core.file.text.AbstractTextFileBuilder;
import marshmalliow.core.security.FileCredentials;

public class TextFile extends AbstractTextFile {

	protected TextFile(AbstractTextFileBuilder<?> builder) {
		super(builder);
	}

	public static class TextFileBuilder extends AbstractTextFileBuilder<TextFile> {
		
		private final DirectoryRegistry registry;
		private TextFileBuilder(DirectoryRegistry registry) { this.registry = registry; }
		
		public static TextFileBuilder builder(DirectoryRegistry registry) { return new TextFileBuilder(registry); }		
		
		public TextFileBuilder directory(Directory d) { this.directory = d; return this; }
		public TextFileBuilder directoryId(String id) { this.directoryId = id; return this; }
		public TextFileBuilder name(String n) { this.name = n; return this; }
		public TextFileBuilder credentials(FileCredentials c) { this.credentials = c; return this; }

		@Override
		public TextFile build() { 
			if(this.directory == null && directoryId != null) {
				this.directory = registry.get(directoryId).orElseThrow(() -> new IllegalStateException("No directory found with id: " + directoryId));
			}
			if(this.directory == null) throw new IllegalStateException("No directory provided or resolved by registry.");
			
			return new TextFile(this); 
		}
	}

}
