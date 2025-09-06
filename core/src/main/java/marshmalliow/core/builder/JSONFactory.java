package marshmalliow.core.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReentrantLock;

import marshmalliow.core.io.JSONLexer;
import marshmalliow.core.io.JSONParser;
import marshmalliow.core.io.JSONWriter;
import marshmalliow.core.json.objects.JSONContainer;
import marshmalliow.core.json.objects.JSONObject;

public class JSONFactory {

	public static final Integer DEFAULT_HTTP_TIMEOUT = 300000;

	private static final ReentrantLock MUTEX = new ReentrantLock();
	private static volatile JSONFactory instance;
		
	private JSONFactory() { }

	/**
	 * Get the instance of {@link JSONFactory} as a singleton.
	 * 
	 * @return the instance of JSONFactory or <code>null</code>
	 */
	public static JSONFactory get() {
		if(instance == null) {
			try {
				MUTEX.lock();
				if(instance == null) instance = new JSONFactory();
			}finally {
				MUTEX.unlock();
			}
		}

		return instance;
	}
	
	/*
	 * Parse and Write JSON Container methods 
	 */
	
	/**
	 * Parse a Reader to a JSON Container.
	 * <p>
	 * This method closes the reader after the parsing is done.
	 * 
	 * @param <E> An object extending {@link JSONContainer}
	 * @param reader The reader to read
	 * @param outputContainer The JSON structure to be read (Object or Array)
	 * @return The container with the data of the JSON
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONContainer> E parseJSON(Reader reader, Class<E> outputContainer) throws IOException {
		JSONLexer lexer = null;
		try {
			lexer = new JSONLexer(reader);
			return outputContainer.cast(new JSONParser(lexer).parse());
		}finally {
			if (lexer != null) lexer.close();
			if (reader != null) reader.close(); // Just in case
		}
	}
	
	/**
	 * Parse an Input Stream to a JSON Container.
	 * <p>
	 * This method uses the UTF-8 charset as a default charset to read the byte array.
	 * 
	 * @param <E> An object extending {@link JSONContainer}
	 * @param stream The input stream to read
	 * @param outputContainer The JSON structure to be read (Object or Array)
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 * @see #parseJSON(InputStream, Charset, Class)
	 */
	public <E extends JSONContainer> E parseJSON(InputStream stream, Class<E> outputContainer) throws IOException {
		return parseJSON(stream, StandardCharsets.UTF_8, outputContainer);
	}
	
	/**
	 * Parse an Input Stream to a JSON Container.
	 * <p>
	 * The charset is used by the {@link InputStreamReader} to read the byte array.
	 * 
	 * @param <E> An object extending {@link JSONContainer}
	 * @param stream The input stream to read
	 * @param charset The charset to use to read the byte array
	 * @param outputContainer The JSON structure to be read (Object or Array)
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONContainer> E parseJSON(InputStream stream, Charset charset, Class<E> outputContainer) throws IOException {
		InputStreamReader reader = null;
		JSONLexer lexer = null;
		try {
			reader = new InputStreamReader(stream, charset);
			lexer = new JSONLexer(reader);
			
			return outputContainer.cast(new JSONParser(lexer).parse());
		}finally {
			if(lexer != null) lexer.close();
			if(reader != null) reader.close(); //Just in case
		}
	}
	
	/**
	 * Parse a byte array to a JSON Container.
	 * <p>
	 * This method uses the UTF-8 charset as a default charset to read the byte array.
	 * 
	 * @param <E> An object extending {@link JSONContainer}
	 * @param byteArray The byte array
	 * @param outputContainer The JSON structure to be read (Object or Array)
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 * @see #parseJSON(byte[], Charset, Class)
	 */
	public <E extends JSONContainer> E parseJSON(byte[] byteArray, Class<E> outputContainer) throws IOException {
		return parseJSON(byteArray, StandardCharsets.UTF_8, outputContainer);
	}
	
	/**
	 * Parse a byte array to a JSON Container.
	 * <p>
	 * The charset is used by the {@link InputStreamReader} to read the byte array.
	 * 
	 * @param <E> An object extending {@link JSONContainer}
	 * @param byteArray The byte array
	 * @param charset The charset to use to read the byte array
	 * @param outputContainer The JSON structure to be read (Object or Array)
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 */
	public <E extends JSONContainer> E parseJSON(byte[] byteArray, Charset charset, Class<E> outputContainer) throws IOException {
		InputStreamReader reader = null;
		JSONLexer lexer = null;
		try {
			final ByteArrayInputStream stream = new ByteArrayInputStream(byteArray);
			reader = new InputStreamReader(stream, charset);
			lexer = new JSONLexer(reader);
			
			return outputContainer.cast(new JSONParser(lexer).parse());
		}finally {
			if(lexer != null) lexer.close();
			if(reader != null) reader.close(); //Just in case
		}
	}
	
	/**
	 * Write the content of a {@link JSONContainer} to a String using {@link JSONWriter}.
	 * 
	 * @param container The JSON container to write
	 * @return The JSON string
	 * @throws IOException If an IO error occurs
	 */
	public String writeJSONToString(JSONContainer container) throws IOException {
		final StringWriter writer = new StringWriter();
		
		new JSONWriter(container).write(writer);
		
		return writer.toString();
	}
	
	/**
	 * Write the content of a {@link JSONContainer} to a byte array using {@link JSONWriter}.
	 * 
	 * @param container The JSON container to write
	 * @return The JSON byte array
	 * @throws IOException If an IO error occurs
	 */
	public byte[] writeJSONToByte(JSONContainer container) throws IOException {
		ByteArrayOutputStream output = null;
		OutputStreamWriter writer = null;
		try {
			output = new ByteArrayOutputStream();
			writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);
			
			new JSONWriter(container).write(writer);
			writer.flush();
			
			return output.toByteArray();
		}finally {
			if(output != null) output.close();
			if(writer != null) writer.close();
		}
	}

	/*
	 * HTTP GET Request
	 */

	/**
	 * Open a {@link HttpURLConnection} and attempt to gather the content of the page as JSON.
	 * 
	 * @param stringURL The URL as a string where the date is stored
	 * @return The container with the data of the JSON File
	 * @throws IOException If the URL is malformed or an IO error occurs
	 */
	public JSONContainer getHttpContentAsJSON(String stringURL) throws IOException {
		return getHttpContentAsJSON(URI.create(stringURL).toURL());
	}
	
	/**
	 * Open a {@link HttpURLConnection} and attempt to gather the content of the page as JSON.
	 * 
	 * @param url an {@link URL} where the date is stored
	 * @return The container with the data of the JSON File
	 * @throws IOException If an IO error occurs
	 */
	public JSONContainer getHttpContentAsJSON(URL url) throws IOException {
		JSONContainer result = new JSONObject();
		HttpURLConnection connection = null;
		InputStreamReader reader = null;
		
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(DEFAULT_HTTP_TIMEOUT);
			connection.addRequestProperty("User-Agent", "Mozilla/5.0");
			connection.connect();
			
			final int responseCode = connection.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK) {
				reader = new InputStreamReader(connection.getInputStream());
				
				result = new JSONParser(new JSONLexer(reader)).parse();
				
			}else {
				throw new IOException("HTTP connection ended with response code "+responseCode);
			}
			
		}finally {
			if(connection != null) connection.disconnect();
			if(reader != null) reader.close();
		}
		
		return result;
	}
}
