package marshmalliow.core.io;

import java.util.ArrayList;
import java.util.List;

import marshmalliow.core.exceptions.CSVParseException;
import marshmalliow.core.file.csv.CSVCell;
import marshmalliow.core.file.csv.CSVDocument;
import marshmalliow.core.file.csv.CSVToken;
import marshmalliow.core.file.csv.CSVTokenEnum;

/**
 * CSVParser is to check if the provided sequence of {@link CSVToken} is in compliance with CSV syntax.<br/>
 * <em>See RFC 4180.</em>
 * @see CSVLexer
 * @author 278deco
 * @version 1.0.1
 */
public class CSVParser {

	/**
	 * State machine
	 */
	private static final int INIT = 0;
	private static final int BEGIN_HEADER = 1;
	private static final int END_HEADER = 2;
	private static final int BEGIN_LINE = 3;
	private static final int END_LINE = 4;
	private static final int BEGIN_QUOTES = 5;
	private static final int END_QUOTES = 6;
	private static final int VALUE = 7;
	private static final int VALUE_SEPARATOR = 8;
	private static final int EOF = 10;
	
	private CSVLexer lexer;
	private volatile int state = INIT;
	private volatile int lastRowSize = -1;
	private volatile int currentRow = 0;
	
	public CSVParser(final CSVLexer lexer) {
		this.lexer = lexer;
	}
	
	public synchronized CSVDocument parse() throws CSVParseException {	
		final CSVDocument doc = new CSVDocument();
		CSVToken token;
		do {
			token = lexer.nextToken();
			
			switch (token.getType()) {
			case EOF:
				state = EOF;
				break;
			case FIELD_ESCAPING:
			case FIELD_VALUE:
				doc.addRow(parseRow(token));
				break;
			case FIELD_SEPARATOR:
				if(lexer.getProperties().hasHeader()) throw new CSVParseException("Unexpected token: "+token.getType().name()+" at state "+state);
				doc.addRow(parseRow(token));
			default:
				throw new CSVParseException("Unexpected token: "+token.getType().name()+" at state "+state);
			}
			
		}while(token.getType() != CSVTokenEnum.EOF);
		
		state = EOF;
		
		return doc;
	}

	
	private List<CSVCell> parseRow(CSVToken readToken) throws CSVParseException {
		final boolean isHeader = state == INIT && lexer.getProperties().hasHeader();
		CSVToken token = readToken;
		
		state = isHeader ? BEGIN_HEADER : BEGIN_LINE;
		final List<CSVCell> row = new ArrayList<>();
		
		int innerState = INIT;
		int previousInnerState = INIT;
		int innerLoopCount = 0;
		int currentColumn = 0;
		
		while(token.getType() != CSVTokenEnum.LINE_BREAK && token.getType() != CSVTokenEnum.EOF) {
			CSVCell cell = null;
			switch (token.getType()) {
			case FIELD_ESCAPING:
				if(previousInnerState != VALUE_SEPARATOR && previousInnerState != INIT) throw new CSVParseException();
				
				innerState = BEGIN_QUOTES;
				
				final StringBuilder sb = new StringBuilder();
				do {
					token = lexer.nextToken();
					
					switch (token.getType()) {
					case FIELD_VALUE:
						sb.append(token.getData());
						break;
					case LINE_BREAK:
						sb.append("\n");
						break;
					case FIELD_SEPARATOR:
						sb.append(lexer.getProperties().getFieldSeparator());
						break;
					default:
						break;
					}
					innerLoopCount++;
				}while(innerLoopCount < 255 && token.getType() != CSVTokenEnum.FIELD_ESCAPING && token.getType() != CSVTokenEnum.EOF);
				
				cell = new CSVCell(currentColumn, currentRow, sb.toString());
				
				innerState = END_QUOTES;
				break;
			case FIELD_VALUE:
				if(previousInnerState != VALUE_SEPARATOR && previousInnerState != INIT) throw new CSVParseException();
				
				innerState = VALUE;
				cell = new CSVCell(currentColumn, currentRow, token.getDataAsString());
				break;
			case FIELD_SEPARATOR:
				if(previousInnerState == VALUE_SEPARATOR && isHeader) throw new CSVParseException("Header fields cannot be empty");
				innerState = VALUE_SEPARATOR;
				if(previousInnerState == VALUE_SEPARATOR) {
					cell = new CSVCell(currentColumn, currentRow, null);
				}
				break;
			default:
				throw new CSVParseException("Unexpected token: "+token.getType().name()+" at state "+state);
			}
			
			if(cell != null) {
				row.add(cell);
				currentColumn++;
			}
			
			innerLoopCount = 0;
			token = lexer.nextToken();
			previousInnerState = innerState;
		}
		
		if(isHeader && state != BEGIN_HEADER) throw new CSVParseException();
		if(!isHeader && state != BEGIN_LINE) throw new CSVParseException();
		state = isHeader ? END_HEADER : END_LINE;
		
		if(lastRowSize == -1) {
			lastRowSize = row.size();
		}else if(lastRowSize != row.size()) {
			throw new CSVParseException("The rows needs to contains the same number of cell each time");
		}
		currentRow++;
		
		return row;
	}
	
	
	/**
	 * Define a new lexer to be used by the this {@link CSVParser} instance.
	 * @param lexer The new lexer
	 */
	public synchronized void setLexer(final CSVLexer lexer) {
		this.lexer = lexer;
	}
	
}
