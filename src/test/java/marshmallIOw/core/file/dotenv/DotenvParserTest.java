package marshmallIOw.core.file.dotenv;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import marshmalliow.core.exceptions.DotenvParseException;
import marshmalliow.core.file.dotenv.DotenvContainer;
import marshmalliow.core.file.dotenv.DotenvToken;
import marshmalliow.core.file.dotenv.DotenvTokenEnum;
import marshmalliow.core.io.DotenvLexer;
import marshmalliow.core.io.DotenvParser;

public class DotenvParserTest {

	private static final String DOTENV_TEST_0 = "";
	
	private static final String DOTENV_TEST_1 = 
			"KEY_1=FIRST STRING INSIDE#ceci est un comm\r\n"
			+ "KEY_2=NOW ONTO THE SECOND, GREAT\r\n"
			+ "# A COMMENT\r\n"
			+ "KEY_3=VROOOOOOOOOOO\\\r\n"
			+ "OOOOOOOOOOOOOOOOOOO\\\r\n"
			+ "OOOOOOOOOOOOOOM\r\n"
			+ "KEY_4=29875\r\n";
	
	@Test
	public void emptyLexerTest() {
		assertEquals(DOTENV_TEST_0.length(), 0);
		
		final StringReader reader = new StringReader(DOTENV_TEST_0);
		assertNotNull(reader);
		
		final DotenvLexer lexer = new DotenvLexer(reader);
		assertNotNull(lexer);
		
		//Two call always do the same when no data is present (EOF)
		assertDoesNotThrow(() -> lexer.nextToken());
		assertDoesNotThrow(() -> lexer.nextToken()); 

		try {
			final DotenvToken token = lexer.nextToken();
			final DotenvToken token2 = lexer.nextToken();
			
			assertNotNull(token);
			assertNotNull(token2);
			
			assertEquals(token.getType(), DotenvTokenEnum.EOF);
			assertThrows(ClassCastException.class, () -> token.getDataAsString());
			assertEquals(token, token2);
			
		} catch (DotenvParseException e) {
			fail("Unexpected exception: " + e);
		}
		
		reader.close();
		assertThrows(IOException.class, () -> reader.read());
	}
	
	@Test
	public void completeLexerTest() {
		assertNotEquals(DOTENV_TEST_1.length(), 0);
		
		final StringReader reader = new StringReader(DOTENV_TEST_1);
		assertNotNull(reader);
		
		final DotenvLexer lexer = new DotenvLexer(reader);
		assertNotNull(lexer);
		
		try {
			final DotenvToken firstToken = lexer.nextToken();
			
			assertNotNull(firstToken);
			assertEquals(firstToken.getType(), DotenvTokenEnum.VALUE);
			assertEquals(firstToken.getDataAsString(), "KEY_1");
			
			int counter = 1;
			int valueCounter = 1;
			int commentCounter = 0;
			int separatorCounter = 0;
			DotenvToken token;
			do {
				token = lexer.nextToken();
                assertNotNull(token);
                
				switch (token.getType()) {
					case VALUE:
						valueCounter++;
						break;
					case COMMENT:
						commentCounter++;
						break;
					case KEY_VALUE_SEPARATOR:
						separatorCounter++;
						break;
					default:
						break;
				}
                
                counter+=1;
			} while (token.getType() != DotenvTokenEnum.EOF);
			
			assertEquals(17, counter); // 14 tokens in the .env file
			assertEquals(10, valueCounter); // 8 values
			assertEquals(2, commentCounter); // 2 comments
			assertEquals(4, separatorCounter); // 3 separators
			
		}catch (DotenvParseException e) {
			fail("Unexpected exception: " + e);
		}
	}
	
	@Test
	public void emptyParserTest() {
		assertEquals(DOTENV_TEST_0.length(), 0);
		
		final StringReader reader = new StringReader(DOTENV_TEST_0);
		assertNotNull(reader);
		
		final DotenvLexer lexer = new DotenvLexer(reader);
		assertNotNull(lexer);
		
		final DotenvParser parser = new DotenvParser(lexer);
		assertNotNull(parser);
		
		try {
			final DotenvContainer container = parser.parse();
			assertNotNull(container);
			assertNotNull(container.getEnvMapping());
			assertEquals(0, container.getEnvMapping().size());

		} catch (DotenvParseException e) {
			fail("Unexpected exception: " + e);
		}
		
		reader.close();
		assertThrows(IOException.class, () -> reader.read());
	}
	
	@Test
	public void completeParserTest() {
        assertNotEquals(DOTENV_TEST_1.length(), 0);
        
        final StringReader reader = new StringReader(DOTENV_TEST_1);
        assertNotNull(reader);
        
        final DotenvLexer lexer = new DotenvLexer(reader);
        assertNotNull(lexer);
        
        final DotenvParser parser = new DotenvParser(lexer);
        assertNotNull(parser);
        
        try {
            final DotenvContainer container = parser.parse();
            assertNotNull(container);
            assertNotNull(container.getEnvMapping());
            assertEquals(4, container.getEnvMapping().size());
            
            assertEquals("FIRST STRING INSIDE", container.getEnvMapping().get("KEY_1"));
            assertEquals("NOW ONTO THE SECOND, GREAT", container.getEnvMapping().get("KEY_2"));
            assertEquals("VROOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOM", container.getEnvMapping().get("KEY_3"));
            
            assertEquals("Backup string", container.getEnvOrDefault("KEY_5", "Backup string"));
            assertEquals("NOW ONTO THE SECOND, GREAT", container.getEnvOrElse("KEY_8", "KEY_2"));
            
            assertEquals("29875", container.getEnv("KEY_4"));
            assertEquals(Optional.of(29875), container.getEnvAsInt("KEY_4"));
            assertEquals(Optional.empty(), container.getEnvAsInt("KEY_2"));
            
        } catch (DotenvParseException e) {
            fail("Unexpected exception: " + e);
        }
        
        reader.close();
        assertThrows(IOException.class, () -> reader.read());
	}
	
}
