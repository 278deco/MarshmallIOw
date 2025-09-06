package marshmalliow.core.file.dotenv;

public class DotenvToken {
	
	private Object data;
	private DotenvTokenEnum type;
	
	public DotenvToken(DotenvTokenEnum type, Object data) {
		this.type = type;
		this.data = data;
	}
	
	public DotenvToken(DotenvTokenEnum type) {
		this.type = type;
		this.data = null;
	}
	
	public DotenvTokenEnum getType() {
		return type;
	}
	
	public boolean containsData() {
		return data != null;
	}
	
	public Object getData() {
		return data;
	}
	
	public Object getDataParsed() throws ClassCastException {
		switch (this.type) {
			case VALUE:
				return (String)data;
			default:
				return null;
		}
	}
	
	public String getDataAsString() throws ClassCastException {
		if(this.type != DotenvTokenEnum.VALUE) throw new ClassCastException();
		return (String) data;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof DotenvToken)) return false;
        final DotenvToken token = (DotenvToken) obj;
        return token.type == this.type && (token.data == null ? this.data == null : token.data.equals(data));
	}
	
	@Override
	public String toString() {
		return data != null ? "DotenvToken[type:"+this.type+", data:"+this.data.toString()+"]" : "DotenvToken[type:"+this.type+"]";
	}
}
