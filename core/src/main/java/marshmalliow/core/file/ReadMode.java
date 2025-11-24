package marshmalliow.core.file;

/**
 * Defines the mode for reading files.<br/><br/>
 * 
 * NORMAL: Only read if no unsaved modifications exist.<br/>
 * FORCE: Force read even if unsaved modifications exist.
 * @author 278deco
 * @version 1.0.0 
 */
public enum ReadMode {
	
	/**
	 * Only read if no unsaved modifications exist.
	 */
	NORMAL,
	
	/**
	 * Force read even if unsaved modifications exist.
	 */
	FORCE; 
}
