package marshmalliow.core.file;

/**
 * Defines the save mode for file operations.<br/><br/>
 * NORMAL: Only save the file if loaded and modified.<br/>
 * FORCE: Force save the file if not modified but prevent saving if not loaded.<br/>
 * OVERWRITE: Force save the file even if not loaded.
 * @author 278deco
 * @version 1.0.0
 */
public enum SaveMode {
	
	
	/**
	 *  Only save the file if loaded and modified
	 */
	NORMAL,
	
	/**
	 * Force save the file if not modified but prevent saving if not loaded
	 */
	FORCE,
	
	/**
	 * Force save the file even if not loaded
	 */
	OVERWRITE;
}
