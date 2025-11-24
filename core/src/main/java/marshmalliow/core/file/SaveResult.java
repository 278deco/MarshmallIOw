package marshmalliow.core.file;

/**
 * Result of a save operation.<br/><br/>
 * 
 * SAVED - The file was saved successfully.<br/>
 * SKIPPED_NO_CHANGES - The file was not saved because there were no changes to save.<br/>
 * SKIPPED_NOT_LOADED - The file was not saved because it was not loaded.<br/>
 * 
 * @author 278deco
 * @version 1.0.0
 */
public enum SaveResult {
	SAVED,
	SKIPPED_NO_CHANGES,
	SKIPPED_NOT_LOADED;
}
