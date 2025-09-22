package marshmalliow.core.file.csv;

import marshmalliow.core.directory.Directory;

public abstract class AbstractCSVFileBuilder<T extends AbstractCSVFile> {
	
	protected Directory directory;
	protected String directoryId;
	protected String name;
	protected CSVProperties properties = CSVProperties.getDefault();

	public abstract T build();

}
