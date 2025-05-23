package marshmalliow.core.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import marshmalliow.core.json.objects.JSONArray;
import marshmalliow.core.json.objects.JSONContainer;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.json.utils.JSONTokenEnum;

/**
 * JSONWriter purpose is to write a JSONContainer (the root of a JSON file) with a specific {@link Writer}<br/>
 * <em>See RFC 4627 and RFC 8259.</em>
 * @author 278deco
 * @version 1.0.0
 */
public class JSONWriter {

	protected volatile char lastCharWritten;
	
	protected JSONContainer source;
	private static final String INDENT = "   ";

	/**
	 * Constructor of {@link JSONWriter}
	 * @param source The root of a JSON file (represented as a {@link JSONContainer})
	 */
	public JSONWriter(JSONContainer source) {
		this.source = source;
	}
	
	/**
	 * Write the content of the JSON file with a specific {@link Writer}<br/>
	 * The content of the file will be written as one single line without any formatting
	 * @param writer The writer used to write down the container
	 * @throws IOException
	 */
	public void write(Writer writer) throws IOException {
		if(this.source instanceof JSONObject) {
			writeObject(writer, (JSONObject)this.source, 0, false);
		}else if(this.source instanceof JSONArray) {
			writeArray(writer, (JSONArray)this.source, 0, false);
		}
	}
	
	/**
	 * Write the content of the JSON file with a specific {@link Writer}<br/>
	 * The content of the file will be written with a more human readable format.
	 * @param writer The writer used to write down the container
	 * @throws IOException
	 */
	public void writeWithPrettyPrint(Writer writer) throws IOException {
		if(this.source instanceof JSONObject) {
			writeObject(writer, (JSONObject)this.source, 0, true);
		}else if(this.source instanceof JSONArray) {
			writeArray(writer, (JSONArray)this.source, 0, true);
		}
	}

	private void writeObject(Writer writer, JSONObject obj, int depth, boolean prettyPrint) throws IOException {
		writeOutput(writer, parseStartingToken(JSONTokenEnum.LEFT_BRACE.getStringToken(), depth, prettyPrint));
		
		int i = 0;
		for(Map.Entry<String, Object> entry : obj.entrySet()) {
			writeOutput(writer, prettyPrint ? INDENT.repeat(depth+1)+"\""+entry.getKey()+"\":" : "\""+entry.getKey()+"\":");
			parseElement(writer, entry.getValue(), depth, prettyPrint);
			
			i+=1;
			if(i < obj.size()) {
				writer.write(',');
				if(prettyPrint) writer.write('\n');
			}
		}
		
		writeOutput(writer, (prettyPrint) ? '\n'+INDENT.repeat(depth)+JSONTokenEnum.RIGHT_BRACE.getStringToken() : JSONTokenEnum.RIGHT_BRACE.getStringToken());
	}
	
	private void writeArray(Writer writer, JSONArray arr, int depth, boolean prettyPrint) throws IOException {
		writeOutput(writer, parseStartingToken(JSONTokenEnum.LEFT_BRACKET.getStringToken(), depth, prettyPrint));
		
		for(int i = 0; i < arr.size(); i++) {
			parseElement(writer, arr.get(i), depth, prettyPrint);
			
			if(i < arr.size()-1) {
				writer.write(',');
				if(prettyPrint) writer.write('\n');
			}
		};
		
		writeOutput(writer, (prettyPrint) ? '\n'+INDENT.repeat(depth)+JSONTokenEnum.RIGHT_BRACKET.getStringToken() : JSONTokenEnum.RIGHT_BRACKET.getStringToken());
	}
	
	private void writeOutput(Writer writer, String out) throws IOException {
		this.lastCharWritten = out.charAt(out.length()-1);
		writer.write(out);
	}

	private String parseStartingToken(String token, int depth, boolean prettyPrint) {
		if(prettyPrint) {
			if(this.lastCharWritten == '\n' || this.lastCharWritten == '\0') {
				return INDENT.repeat(depth)+token+'\n';
			}
			return token+'\n';
		}
		return token;
	}

	private void parseElement(Writer writer, Object element, int depth, boolean prettyPrint) throws IOException {
		if(element instanceof JSONArray) {
			writeArray(writer, ((JSONArray)element), depth+1, prettyPrint);
		}else if(element instanceof JSONObject) {
			writeObject(writer, ((JSONObject)element), depth+1, prettyPrint);
		}else {
			writeOutput(writer, writeValue(element));
		}
	}
	
	private String writeValue(Object value) {
		if(value instanceof String) {
			final StringBuilder sb = new StringBuilder("\"");
			for(char c : value.toString().toCharArray()) {
				switch(c) {
					case '\b':
						sb.append("\\b");
						break;
					case '\f':
						sb.append("\\f");
						break;
					case '\n':
						sb.append("\\n");
						break;
					case '\r':
						sb.append("\\r");
						break;
					case '\t':
						sb.append("\\t");
						break;
					case '\"':
						sb.append("\\\"");
						break;
					case '\\':
						sb.append("\\\\");
						break;
					default:
						if(c < 0x20 || c > 0x7E) {
							sb.append("\\u");
							sb.append(String.format("%04X", (int)c));
						}else {
							sb.append(c);
						}
				}
			}
			sb.append("\"");
			
			return sb.toString();
		}else {
			return value.toString();
		}
	}

}
