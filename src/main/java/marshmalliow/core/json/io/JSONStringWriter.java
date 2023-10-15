package marshmalliow.core.json.io;

import java.util.Map;
import java.util.Objects;

import marshmalliow.core.json.objects.JSONArray;
import marshmalliow.core.json.objects.JSONContainer;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.json.utils.JSONTokenEnum;

@Deprecated
public class JSONStringWriter {
	
	private JSONContainer source;	
	private StringBuilder result; //Using StringBuilder because the write function is sync
	
	private boolean prettyPrinter;

	public JSONStringWriter(JSONContainer source) {
		this.source = Objects.requireNonNull(source);
		this.result = new StringBuilder();
	}
	
	public synchronized void setPrettyPrinter(boolean val) {
		this.prettyPrinter = val;
	}
	
	public synchronized String writeToString() {
		if(!this.result.isEmpty()) return result.toString();
		
		if(this.source instanceof JSONObject) {
			writeObject((JSONObject)this.source, 0);
		}else if(this.source instanceof JSONArray) {
			writeArray((JSONArray)this.source, 0);
		}
		
		return result.toString();
	}
	
	private void writeObject(JSONObject obj, int depth) {
		result.append(JSONTokenEnum.LEFT_BRACE.getStringToken());
		
		int i = 0;
		for(Map.Entry<String, Object> entry : obj.entrySet()) {
			result.append("\""+entry.getKey()+"\":");
			final Object value = entry.getValue();
			if(value instanceof JSONArray) {
				writeArray(((JSONArray)value), depth+1);
			}else if(value instanceof JSONObject) {
				writeObject(((JSONObject)value), depth+1);
			}else {
				result.append(writeValue(value));
			}
			
			i+=1;
			if(i < obj.size()) result.append(',');
		}

		result.append(JSONTokenEnum.RIGHT_BRACE.getStringToken());
	}
	
	private void writeArray(JSONArray obj, int depth) {
		result.append(JSONTokenEnum.LEFT_BRACKET.getStringToken());
		
		for(int i = 0; i < obj.size(); i++) {
			final Object element = obj.get(i);
			
			if(element instanceof JSONArray) {
				writeArray(((JSONArray)element), depth+1);
			}else if(element instanceof JSONObject) {
				writeObject(((JSONObject)element), depth+1);
			}else {
				result.append(writeValue(element));
			}
			if(i < obj.size()-1) result.append(',');
		};
		
		result.append(JSONTokenEnum.RIGHT_BRACKET.getStringToken());
	}
	
	private String writeValue(Object value) {
		if(value instanceof String) {
			return "\""+value+"\"";
		}else {
			return value.toString();
		}
	}
	
}
