package marshmalliow.core.json.objects;

public interface JSONContainer {
	
	/**
	 * Set the contentModified flag to the given value.
	 * @param value
	 */
	public void setContentModified(boolean value);
	
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
