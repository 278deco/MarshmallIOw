package marshmalliow.core.json.objects;

public interface JSONContainer {
	
	/**
	 * Reset the contentModified flag to false. <br/>
	 * This method should be called after the content has been saved or 
	 * after the modifications have been acknowledged.
	 * 
	 */
	public void resetModified();
	
	/**
	 * Get the contentModified flag.
	 * 
	 * @return the contentModified flag.
	 */
	public boolean isModified();
	
	/**
	 * Clear the content of this container.
	 */
	public void clear();
}
