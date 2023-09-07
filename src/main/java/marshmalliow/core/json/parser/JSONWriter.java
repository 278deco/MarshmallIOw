package marshmalliow.core.json.parser;

import java.util.Objects;

import marshmalliow.core.json.objects.JSONArray;
import marshmalliow.core.json.objects.JSONContainer;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.json.utils.JSONTokenEnum;

public class JSONWriter {
	
	private JSONContainer source;	
	private StringBuilder result; //Using StringBuilder because the write function is sync
	
	private boolean prettyPrinter;

	public JSONWriter(JSONContainer source) {
		this.source = Objects.requireNonNull(source);
		this.result = new StringBuilder();
	}
	
	public synchronized void setPrettyPrinter(boolean val) {
		this.prettyPrinter = val;
	}
	
	public synchronized String writeToString() {
		if(this.source instanceof JSONObject) {
			writeObject((JSONObject)this.source, 0);
		}else if(this.source instanceof JSONArray) {
			writeArray((JSONArray)this.source, 0);
		}
		
		return result.toString();
	}
	
	private void writeObject(JSONObject obj, int depth) {
		result.append(JSONTokenEnum.LEFT_BRACE.getStringToken()+(prettyPrinter ? "\n" : ""));
		
		obj.forEach((key, value) -> {
			result.append("\""+key+"\":");
			if(value instanceof JSONArray) {
				writeArray(((JSONArray)value), depth+1);
			}else if(value instanceof JSONObject) {
				writeObject(((JSONObject)value), depth+1);
			}else {
				result.append(writeValue(value));
			}
			result.append(","+(prettyPrinter ? "\n" : ""));
		});
		if(prettyPrinter) {
			result.setLength(result.length()-2);
			result.append((prettyPrinter ? "\n" : "")+JSONTokenEnum.RIGHT_BRACE.getStringToken());
		}
		else {
			result.setCharAt(result.length()-1, JSONTokenEnum.RIGHT_BRACE.getStringToken().charAt(0));
		}
	}
	
	private void writeArray(JSONArray obj, int depth) {
		result.append(JSONTokenEnum.LEFT_BRACKET.getStringToken()+(prettyPrinter ? "\n" : ""));
		
		obj.forEach(element -> {
			if(element instanceof JSONArray) {
				writeArray(((JSONArray)element), depth+1);
			}else if(element instanceof JSONObject) {
				writeObject(((JSONObject)element), depth+1);
			}else {
				result.append(writeValue(element));
			}
			result.append(","+(prettyPrinter ? "\n" : ""));
		});
		
		if(prettyPrinter) {
			result.setLength(result.length()-2);
			result.append((prettyPrinter ? "\n" : "")+JSONTokenEnum.RIGHT_BRACKET.getStringToken());
		}
		else {
			result.setCharAt(result.length()-1, JSONTokenEnum.RIGHT_BRACKET.getStringToken().charAt(0));
		}
	}
	
	private String writeValue(Object value) {
		if(value instanceof String) {
			return "\""+value+"\"";
		}else {
			return value.toString();
		}
	}
	
}
