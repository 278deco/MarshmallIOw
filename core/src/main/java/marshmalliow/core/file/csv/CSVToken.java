package marshmalliow.core.file.csv;

public class CSVToken {

	private Object data;
	private CSVTokenEnum type;
	
	public CSVToken(CSVTokenEnum type, Object data) {
		this.type = type;
		this.data = data;
	}
	
	public CSVToken(CSVTokenEnum type) {
		this.type = type;
		this.data = null;
	}
	
	public CSVTokenEnum getType() {
		return type;
	}
	
	public boolean containsData() {
		return data != null;
	}
	
	public Object getData() {
		return data;
	}
	
	public String getDataAsString() throws ClassCastException {
		if(this.type != CSVTokenEnum.FIELD_VALUE) throw new ClassCastException();
		return (String) data;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof CSVToken)) return false;
        final CSVToken token = (CSVToken) obj;
        return token.type == this.type && (token.data == null ? this.data == null : token.data.equals(data));
	}
	
	@Override
	public String toString() {
		return data != null ? "CSVToken[type:"+this.type+", data:"+this.data.toString()+"]" : "CSVToken[type:"+this.type+"]";
	}
	
}
