package marshmallIOw.core.io;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import marshmalliow.core.io.JSONLexer;
import marshmalliow.core.json.JSONTokenEnum;
import marshmalliow.core.json.objects.JSONToken;

public class JSONLexerTest {
	
	public static final String[] LEXER_TEST = new String[] {
	                                           "{\"foo\":\"bar\", \"numbers\":{\"n1\":154,\"n2\":5996}, \"vio\":[\"j\",\"t\",\"m\"]}",
	                                           "{\"coord\":{\"lon\":139.01,\"lat\":35.02},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01n\"}],\"base\":\"stations\",\"main\":{\"temp\":285.514,\"pressure\":1013.75,\"humidity\":100,\"temp_min\":285.514,\"temp_max\":285.514,\"sea_level\":1023.22,\"grnd_level\":1013.75},\"wind\":{\"speed\":5.52,\"deg\":311},\"clouds\":{\"all\":0},\"dt\":1485792967,\"sys\":{\"message\":0.0025,\"country\":\"JP\",\"sunrise\":1485726240,\"sunset\":1485763863},\"id\":1907296,\"name\":\"Tawarano\",\"cod\":200,\"geo\":{}}",
	                                           "{\"coord\":{\"lon\":139.01,\"lat\":35.02},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01n\"}],\"base\":\"stations\",\"main\":{\"temp\":285.514,\"pressure\":1013.75,\"humidity\":100,\"temp_min\":285.514,\"temp_max\":285.514,\"sea_level\":1023.22,\"grnd_level\":1013.75},\"wind\":{\"speed\":5.52,\"deg\":311},\"clouds\":{\"all\":0},\"dt\":1485792967,\"sys\":{\"message\":0.0025,\"country\":\"JP\",\"sunrise\":1485726240,\"sunset\":1485763863},\"id\":1907296,\"name\":\"Tawarano\",\"cod\":200}",
	                                           "{ \"test\": \"\bquoi*\b\", \"other\": \"\nLine\tTab\u0021!\" }"};
	
	@Test
	public void lexerTest() {
		for(final String test : LEXER_TEST) {
			final StringReader reader = new StringReader(test);
			assertNotNull(reader);
			
			final JSONLexer lexer = new JSONLexer(reader);
			assertNotNull(lexer);
			
			assertDoesNotThrow(() -> {
				final JSONToken token = lexer.nextToken();
				
				assertNotNull(token);
			});
			
			assertDoesNotThrow(() -> {
				JSONToken token = null;
				do {
					token = lexer.nextToken();
					assertNotNull(token);
				} while(token.getType() != JSONTokenEnum.EOF);
			});
		}
		
	}

}
