package marshmalliow.core.io;

import java.util.HashMap;
import java.util.Map;

import marshmalliow.core.exceptions.DotenvParseException;
import marshmalliow.core.file.dotenv.DotenvContainer;
import marshmalliow.core.file.dotenv.DotenvToken;
import marshmalliow.core.file.dotenv.DotenvTokenEnum;

/**
 * DotenvParser is to check if the provided sequence of {@link DotenvToken} is in compliance with .env file syntax.
 * @see DotenvLexer
 * @author 278deco
 * @version 1.0.0
 */
public class DotenvParser {

	private enum StateMachine {
		ST_INIT, ST_KEY, ST_SEPARATOR, ST_VALUE, ST_COMMENT, ST_COMMENT_VALUE, ST_EOF;
	}
	
	private DotenvLexer lexer;
	private volatile StateMachine state = StateMachine.ST_INIT;
	
	public DotenvParser(DotenvLexer lexer) {
		this.lexer = lexer;
	}
	
	/**
	 * Parse the {@link DotenvToken} provided by the {@link DotenvLexer}.<br/>
	 * Update the state machine depending on the token encountered.<br/>
	 * @throws DotenvParseException
	 * @return The {@link DotenvContainer} containing the mapping of the .env file
	 */
	public synchronized DotenvContainer parse() throws DotenvParseException {
		final Map<String, String> mapping = new HashMap<>();
		
		DotenvToken token;
		String lastTokenName = null;
		do {
			token = lexer.nextToken();

			switch(token.getType()) {
				case VALUE -> {
					switch(state) {
						case ST_COMMENT:
							state = StateMachine.ST_COMMENT_VALUE;
							break;
						case ST_SEPARATOR:
							state = StateMachine.ST_VALUE;
							mapping.put(lastTokenName.trim(), token.getDataAsString().trim());
							lastTokenName = null;
							break;
						case ST_INIT, ST_COMMENT_VALUE, ST_VALUE:
							state = StateMachine.ST_KEY;
							lastTokenName = token.getDataAsString();
						    break;
						default:
							throw new DotenvParseException("Unexpected value token found");
					}
				}
				case COMMENT -> {
					switch(state) {
						case ST_INIT, ST_VALUE: //Acceptable comment
							state = StateMachine.ST_COMMENT;
							break;
						default: //Not acceptable to have a comment at this position
							throw new DotenvParseException("Unexpected comment token found");
					}					
				}
				case KEY_VALUE_SEPARATOR -> {
					switch(state) {
						case ST_KEY: //Acceptable separator
							state = StateMachine.ST_SEPARATOR;
							break;
						default: //Not acceptable to have a separator at this position
							System.out.println(state);
							throw new DotenvParseException("Unexpected separator token found");
					}
				}
				case EOF -> { break; }
				
				default -> { throw new DotenvParseException(); }
			}
			
		}while(token.getType() != DotenvTokenEnum.EOF);
		
		return new DotenvContainer(mapping);
	}
	
}
