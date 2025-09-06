package marshmalliow.core.database.objects;

public class NullValue {
	
	private Class<?> obj;
	
	private NullValue(Class<?> obj) {
		this.obj = obj;
	}
	
	public static NullValue of(Class<?> obj) {
        return new NullValue(obj);
    }
	
	public Class<?> getType() {
		return obj;
	}
}
