package marshmalliow.core.objects;

/**
 * Null is a placeholder for a null value.
 * <p>
 * This class is used to represent a null value in JSON.
 * @author 278deco
 * @version 1.0.0
 * @since 0.3.2
 */
public class Null {
	
	public static final Null NULL = new Null();
	
	public Object getValue() {
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Null;
	}
}
