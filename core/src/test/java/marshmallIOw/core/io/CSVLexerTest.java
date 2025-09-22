package marshmallIOw.core.io;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import marshmalliow.core.exceptions.CSVParseException;
import marshmalliow.core.file.csv.CSVProperties;
import marshmalliow.core.file.csv.CSVToken;
import marshmalliow.core.file.csv.CSVTokenEnum;
import marshmalliow.core.io.CSVLexer;

public class CSVLexerTest {
	
	private final static String LEXER_TEST_1 = "";
	
	private static final String LEXER_TEST_2 = "a,b,c\r\n1,\"2\",3\r\n4,5,\"6\"";
	
	private static final String LEXER_TEST_3 = "field_a, \"field_b\", field_c\r\nhello, \"field data\r\nnew line inside field\", world";
	
	private static final String LEXER_TEST_4 = "first, second, third\r\n\"embedded \"\"quote\"\" test\", simple, field";
	
	@Test
	public void lexerTest1() {
		assertEquals(LEXER_TEST_1.length(), 0);
		
		final CSVProperties props = CSVProperties.builder()
				.hasHeader(false)
				.lineSeparator(CSVProperties.DEFAULT_LINE_SEPARATOR)
				.build();
		
		assertNotNull(props);
		
		final StringReader reader = new StringReader(LEXER_TEST_1);
		assertNotNull(reader);
		
		final CSVLexer lexer = new CSVLexer(reader, props);
		assertNotNull(lexer);
		
		//Two call always do the same when no data is present (EOF)
		assertDoesNotThrow(() -> lexer.nextToken());
		assertDoesNotThrow(() -> lexer.nextToken()); 

		try {
			final CSVToken token = lexer.nextToken();
			final CSVToken token2 = lexer.nextToken();
			
			assertNotNull(token);
			assertNotNull(token2);
			
			assertEquals(token.getType(), CSVTokenEnum.EOF);
			assertThrows(ClassCastException.class, () -> token.getDataAsString());
			assertEquals(token, token2);
			
		} catch (CSVParseException e) {
			fail("Unexpected exception: " + e);
		}
		
		reader.close();
		assertThrows(IOException.class, () -> reader.read());
	}
	
	@Test
	public void testLexer2() {
		assertNotEquals(LEXER_TEST_2.length(), 0);
		
		final CSVProperties props = CSVProperties.builder()
				.hasHeader(true)
				.lineSeparator(CSVProperties.DEFAULT_LINE_SEPARATOR)
				.build();
		
		final StringReader reader = new StringReader(LEXER_TEST_2);
		assertNotNull(reader);
		
		final CSVLexer lexer = new CSVLexer(reader, props);
		assertNotNull(lexer);
		
		try {
			final CSVToken firstToken = lexer.nextToken();
			
			assertNotNull(firstToken);
			assertEquals(firstToken.getType(), CSVTokenEnum.FIELD_VALUE);
			assertEquals(firstToken.getDataAsString(), "a");
			
			int counter = 1;
			int valueCounter = 1;
			int separatorCounter = 0;
			int escapeCounter = 0;
			int lineBreakCounter = 0;
			CSVToken token;
			do {
				token = lexer.nextToken();
                assertNotNull(token);
                
				switch (token.getType()) {
					case FIELD_VALUE:
						valueCounter++;
						break;
					case FIELD_ESCAPING:
						escapeCounter++;
						break;
					case FIELD_SEPARATOR:
						separatorCounter++;
						break;
					case LINE_BREAK:
						lineBreakCounter++;
						break;
					default:
						break;
				}
                
                counter+=1;
			} while (token.getType() != CSVTokenEnum.EOF);
			
			assertEquals(22, counter);
			assertEquals(9, valueCounter); // 8 values
			assertEquals(4, escapeCounter); // 4 escapting quotes
			assertEquals(6, separatorCounter); // 6 line separators
			assertEquals(2, lineBreakCounter); // 2 line breaks
			
		}catch (CSVParseException e) {
			fail("Unexpected exception: " + e);
		}
	}
	
	@Test
	public void testLexer3() {
		assertNotEquals(LEXER_TEST_3.length(), 0);
		
		final CSVProperties props = CSVProperties.builder()
				.hasHeader(true)
				.lineSeparator(CSVProperties.DEFAULT_LINE_SEPARATOR)
				.build();
		
		final StringReader reader = new StringReader(LEXER_TEST_3);
		assertNotNull(reader);
		
		final CSVLexer lexer = new CSVLexer(reader, props);
		assertNotNull(lexer);
		
		try {
			final CSVToken firstToken = lexer.nextToken();
			
			assertNotNull(firstToken);
			assertEquals(firstToken.getType(), CSVTokenEnum.FIELD_VALUE);
			assertEquals(firstToken.getDataAsString(), "field_a");
			
			int counter = 1;
			int valueCounter = 1;
			int separatorCounter = 0;
			int escapeCounter = 0;
			int lineBreakCounter = 0;
			CSVToken token;
			do {
				token = lexer.nextToken();
				assertNotNull(token);
                
				switch (token.getType()) {
					case FIELD_VALUE:
						valueCounter++;
						break;
					case FIELD_ESCAPING:
						escapeCounter++;
						break;
					case FIELD_SEPARATOR:
						separatorCounter++;
						break;
					case LINE_BREAK:
						lineBreakCounter++;
						break;
					default:
						break;
				}
                
                counter+=1;
			} while (token.getType() != CSVTokenEnum.EOF);
			
			assertEquals(18, counter);
			assertEquals(7, valueCounter); // 7 values
			assertEquals(4, escapeCounter); // 4 escapting quotes
			assertEquals(4, separatorCounter); // 4 line separators
			assertEquals(2, lineBreakCounter); // 2 line breaks with one inside a escaping string
			
		}catch (CSVParseException e) {
			fail("Unexpected exception: " + e);
		}
	}
	
	@Test
	public void testLexer4() {
		assertNotEquals(LEXER_TEST_4.length(), 0);
		
		final CSVProperties props = CSVProperties.builder()
				.hasHeader(true)
				.lineSeparator(CSVProperties.DEFAULT_LINE_SEPARATOR)
				.build();
		
		final StringReader reader = new StringReader(LEXER_TEST_4);
		assertNotNull(reader);
		
		final CSVLexer lexer = new CSVLexer(reader, props);
		assertNotNull(lexer);
		
		try {
			final CSVToken firstToken = lexer.nextToken();
			
			assertNotNull(firstToken);
			assertEquals(firstToken.getType(), CSVTokenEnum.FIELD_VALUE);
			assertEquals(firstToken.getDataAsString(), "first");
			
			int counter = 1;
			int valueCounter = 1;
			int separatorCounter = 0;
			int escapeCounter = 0;
			int lineBreakCounter = 0;
			CSVToken token;
			do {
				token = lexer.nextToken();
				assertNotNull(token);
                
				switch (token.getType()) {
					case FIELD_VALUE:
						valueCounter++;
						break;
					case FIELD_ESCAPING:
						escapeCounter++;
						break;
					case FIELD_SEPARATOR:
						separatorCounter++;
						break;
					case LINE_BREAK:
						lineBreakCounter++;
						break;
					default:
						break;
				}
                
                counter+=1;
			} while (token.getType() != CSVTokenEnum.EOF);
			
			assertEquals(14, counter);
			assertEquals(6, valueCounter); // 6 values
			assertEquals(2, escapeCounter); // 2 escapting quotes
			assertEquals(4, separatorCounter); // 4 line separators
			assertEquals(1, lineBreakCounter); // 1 line break
			
		}catch (CSVParseException e) {
			fail("Unexpected exception: " + e);
		}
	}
}
