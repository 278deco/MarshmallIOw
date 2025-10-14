package marshmalliow.core.file;

import marshmalliow.core.directory.Directory;
import marshmalliow.core.directory.DirectoryRegistry;
import marshmalliow.core.json.AbstractJSONFile;
import marshmalliow.core.json.AbstractJSONFileBuilder;
import marshmalliow.core.json.objects.JSONArray;
import marshmalliow.core.json.objects.JSONContainer;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.security.FileCredentials;

public class JSONFile extends AbstractJSONFile {

	protected JSONFile(AbstractJSONFileBuilder<?> builder) {
		super(builder);
	}

	@Override
	public JSONContainer getContent() {
		return super.getContent();
	}
	
	@Override
	public JSONObject getContentAsObject() {
		return super.getContentAsObject();
	}
	
	@Override
	public JSONArray getContentAsArray() {
		return super.getContentAsArray();
	}
	
	public static class JSONFileBuilder extends AbstractJSONFileBuilder<JSONFile> {
		
		private final DirectoryRegistry registry;
		private JSONFileBuilder(DirectoryRegistry registry) { this.registry = registry; }
		
		public static JSONFileBuilder builder(final DirectoryRegistry registry) { return new JSONFileBuilder(registry); }		
		
        public JSONFileBuilder directory(Directory d) { this.directory = d; return this; }
        public JSONFileBuilder directoryId(String id) { this.directoryId = id; return this; }
        public JSONFileBuilder base(JSONContainer b) { this.base = b; return this; }
        public JSONFileBuilder credentials(FileCredentials c) { this.credentials = c; return this; }
        public JSONFileBuilder name(String n) { 
        	if(!n.toLowerCase().endsWith(FileType.JSON.getExtension())) n += FileType.JSON.getExtension();
        	this.name = n; return this; 
        }

        @Override
        public JSONFile build() {
        	if(this.name == null || this.name.isBlank()) throw new IllegalStateException("File name cannot be null or blank.");
    		if(this.base == null) throw new IllegalStateException("JSONContainer base cannot be null.");
			if(this.directory == null && directoryId != null) {
				this.directory = registry.get(directoryId).orElseThrow(() -> new IllegalStateException("No directory found with id: " + directoryId));
			}
			if(this.directory == null) throw new IllegalStateException("No directory provided or resolved by registry.");
			
			return new JSONFile(this); 
        }
    }
}
