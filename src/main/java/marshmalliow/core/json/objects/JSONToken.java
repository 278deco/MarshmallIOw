package marshmalliow.core.json.objects;

import marshmalliow.core.json.utils.JSONTokenEnum;
import marshmalliow.core.objects.Null;

public class JSONToken {

	private Object data;
	private JSONTokenEnum type;
	
	public JSONToken(JSONTokenEnum type, Object data) {
		this.type = type;
		this.data = data;
	}
	
	public JSONToken(JSONTokenEnum type) {
		this.type = type;
		this.data = null;
	}
	
	public JSONTokenEnum getType() {
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
		case VALUE_STRING:
			return (String) data;
		case VALUE_INTEGER:
			return (int) data;
		case VALUE_LONG:
			return (long) data;
		case VALUE_DOUBLE:
			return (double) data;
		case VALUE_FLOAT:
			return (float) data;
		case VALUE_TRUE:
		case VALUE_FALSE:
			return (boolean) data;
		default:
			return Null.NULL;
		}
	}
	
	public String getDataAsString() throws ClassCastException {
		if(this.type != JSONTokenEnum.VALUE_STRING) throw new ClassCastException();
		return (String) data;
	}
	
	public int getDataAsInteger() throws ClassCastException {
		if(this.type != JSONTokenEnum.VALUE_INTEGER) throw new ClassCastException();
		return (int) data;
	}
	
	public long getDataAsLong() throws ClassCastException {
		if(this.type != JSONTokenEnum.VALUE_LONG) throw new ClassCastException();
		return (long) data;
	}
	
	public float getDataAsFloat() throws ClassCastException {
		if(this.type != JSONTokenEnum.VALUE_FLOAT) throw new ClassCastException();
		return (float) data;
	}
	
	public double getDataAsDouble() throws ClassCastException {
		if(this.type != JSONTokenEnum.VALUE_DOUBLE) throw new ClassCastException();
		return (double) data;
	}
	
	public boolean getDataAsBoolean() throws ClassCastException {
		if(this.type != JSONTokenEnum.VALUE_FALSE && this.type != JSONTokenEnum.VALUE_TRUE) throw new ClassCastException();
		return (boolean) data;
	}
	
	@Override
	public String toString() {
		return data != null ? "JSONToken[type:"+this.type+", data:"+this.data.toString()+"]" : "JSONToken[type:"+this.type+"]";
	}
	
}
