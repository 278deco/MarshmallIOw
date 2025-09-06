package marshmallIOw.core.algorithm;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import marshmalliow.core.algorithm.ShuntingYardImpl;

public class ShuntingYardImplTest {

	@ParameterizedTest
	@CsvFileSource(resources = "/infix_to_postfix_test.csv", delimiter = '$', numLinesToSkip = 1)
	public void InfixToPostfixTest(String input, String expected) {
		final ShuntingYardImpl algorithm = new ShuntingYardImpl(input);
		assertNotNull(algorithm);
		
		assertDoesNotThrow(() -> algorithm.infixToPostfix());
		
		final String result = algorithm.getOutputAsString();
		assertNotNull(result);
		assertNotNull(expected);
		assertEquals(expected, result);
	}
	
	@ParameterizedTest
	@CsvFileSource(resources = "/postfix_to_infix_test.csv", delimiter = '$', numLinesToSkip = 1)
	public void PostfixToInfixTest(String expected, String input) {
		final ShuntingYardImpl algorithm = new ShuntingYardImpl(input);
		assertNotNull(algorithm);
		
		assertDoesNotThrow(() -> algorithm.postfixToInfix());
		
		final String result = algorithm.getOutputAsString();
		assertNotNull(result);
		assertNotNull(expected);
		assertEquals(expected, result);
	}
	
	@ParameterizedTest
	@CsvFileSource(resources = "/infix_to_postfix_test.csv", delimiter = '$', numLinesToSkip = 1)
	public void evaluatePostfixTest(String input, String expected, String evaluated) {
		
		final ShuntingYardImpl algorithm = new ShuntingYardImpl(input);
		assertNotNull(algorithm);
		
		assertDoesNotThrow(() -> algorithm.infixToPostfix());
		
		final String result = algorithm.getOutputAsString();
		assertNotNull(result);
		assertNotNull(expected);
		assertEquals(expected, result);
		
		final Optional<Number> resultNumber = algorithm.evaluate();
		assertNotNull(resultNumber);
		assertTrue(resultNumber.isPresent());
		assertNotNull(evaluated);
		
		final Number evalutatedNumber = Double.parseDouble(evaluated);
		assertNotNull(evalutatedNumber);
		
		assertEquals(evalutatedNumber, resultNumber.get());
	}
	
}
