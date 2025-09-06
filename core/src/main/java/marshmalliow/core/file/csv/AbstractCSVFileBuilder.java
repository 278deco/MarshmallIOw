package marshmalliow.core.file.csv;

import marshmalliow.core.directory.Directory;

public abstract class AbstractCSVFileBuilder<T extends AbstractCSVFile> {

	private static final String DEFAULT_SEPARATOR = ",";
	
	protected Directory directory;
	protected String directoryId;
	protected String name;
	protected String separator = DEFAULT_SEPARATOR;

	public abstract T build();

}
