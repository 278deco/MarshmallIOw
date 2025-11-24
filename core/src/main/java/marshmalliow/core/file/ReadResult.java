package marshmalliow.core.file;

/**
 * Result of reading a file.<br/><br/>
 * 
 * READ - File was read successfully.<br/>
 * SKIPPED_MODIFIED - File was skipped because it was modified.<br/>
 * FILE_NOT_FOUND - File was not found.<br/>
 * @author 278deco
 * @version 1.0.0
 * 
 */
public enum ReadResult {
	READ,
	SKIPPED_MODIFIED,
	FILE_NOT_FOUND;
}
