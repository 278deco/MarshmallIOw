package marshmallIOw.core.io;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import marshmalliow.core.exceptions.CSVParseException;
import marshmalliow.core.file.csv.CSVDocument;
import marshmalliow.core.file.csv.CSVProperties;
import marshmalliow.core.io.CSVLexer;
import marshmalliow.core.io.CSVParser;

public class CSVParserTest {

	private static final String[] GOOD_LEXER_TEST = new String[] {
			"a,b,c\r\n1,\"2\",3\r\n4,5,\"6\"", 
			"field_a, \"field_b\", field_c\r\nhello, \"field data\r\nnew line inside field\", world",
			"first, second, third\r\n\"embedded \"\"quote\"\" test\", simple, field"
	}; // Good format

	private static final String[] BAD_LEXER_TEST = new String[] { 
			"a,b,c\r\n1,\"2\",3\r\n4,\"6\"",
			"field_a, \"field_b\", field_c\r\nhello, field data\r\nnew line inside field, world"
	}; // Bad format

	
	@Test
	public void parserGoodTest() {
		assertNotEquals(0, GOOD_LEXER_TEST.length);
		
		for(final String LEXER_TEST : GOOD_LEXER_TEST) {
			assertNotEquals(LEXER_TEST.length(), 0);
			
			final CSVProperties props = CSVProperties.builder()
					.hasHeader(true)
					.lineSeparator(CSVProperties.DEFAULT_LINE_SEPARATOR)
					.build();
			
			assertNotNull(props);
			
			final StringReader reader = new StringReader(LEXER_TEST);
			assertNotNull(reader);
			
			final CSVLexer lexer = new CSVLexer(reader, props);
			assertNotNull(lexer);
			
			final CSVParser parser = new CSVParser(lexer);
			assertNotNull(parser);
			
			assertDoesNotThrow(() -> {
				CSVDocument doc = parser.parse();
				assertNotNull(doc);
			});
		}
	}
	
	@Test
	public void parserBadTest() {
		assertNotEquals(0, BAD_LEXER_TEST.length);
		
		for(final String LEXER_TEST : BAD_LEXER_TEST) {
			final CSVProperties props = CSVProperties.builder()
					.hasHeader(true)
					.lineSeparator(CSVProperties.DEFAULT_LINE_SEPARATOR)
					.build();
			
			assertNotNull(props);
			
			final StringReader reader = new StringReader(LEXER_TEST);
			assertNotNull(reader);
			
			final CSVLexer lexer = new CSVLexer(reader, props);
			assertNotNull(lexer);
			
			final CSVParser parser = new CSVParser(lexer);
			assertNotNull(parser);
			
			assertThrows(CSVParseException.class, () -> parser.parse());
		}
	}
	
}
