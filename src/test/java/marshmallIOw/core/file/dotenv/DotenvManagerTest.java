package marshmallIOw.core.file.dotenv;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import marshmalliow.core.builder.DotenvManager;
import marshmalliow.core.exceptions.DotenvParseException;
import marshmalliow.core.objects.Directory;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) 
public class DotenvManagerTest {
	
	@Test
	@Order(1)
	public void addSystemEnvironmentTest() {
		DotenvManager manager = DotenvManager.get();
		
		assertNotNull(manager);
		assertNotNull(manager.getEnvMapping());
		
		manager.addSystemEnvironment();
		final int initialSize = manager.getEnvMapping().size();
		assertNotEquals(initialSize, 0);
		
		manager.addSystemEnvironment();
		final int endSize = manager.getEnvMapping().size();
		assertNotEquals(endSize, 0);
		assertEquals(initialSize, endSize);
	}
	
	@Test
	@Order(2)
	public void addDotenvFileTest() {
		DotenvManager manager = DotenvManager.get();

		assertNotNull(manager);
		assertNotNull(manager.getEnvMapping());
		assertNotEquals(manager.getEnvMapping().size(), 0);
		
		final int initialSize = manager.getEnvMapping().size();
		
		final Directory testDir = new Directory("src/test/resources");

		assertDoesNotThrow(() -> manager.addEnvFile(testDir, "Dotenv_test1"));
		assertEquals(manager.getEnvMapping().size(), initialSize + 4);	
		
		assertDoesNotThrow(() -> manager.addEnvFile(testDir, "Dotenv_test2.env"));
		assertEquals(manager.getEnvMapping().size(), initialSize + 4 + 5);
		
		assertThrows(DotenvParseException.class, () -> manager.addEnvFile(testDir, "Dotenv_test_error"));
		assertEquals(manager.getEnvMapping().size(), initialSize + 4 + 5);
	}
	
	@Test
	@Order(3)
	public void checkEnvMappingTest() {
		DotenvManager manager = DotenvManager.get();

		assertNotNull(manager);
		assertNotNull(manager.getEnvMapping());
		assertNotEquals(manager.getEnvMapping().size(), 0);
				
		assertEquals("WORLD", manager.getEnv("HELLO"));
		assertNull(manager.getEnv("hello"));
		assertEquals(Optional.empty(), manager.getEnvAsInt("HELLO"));

		assertEquals("https://www.wikipedia.org", manager.getEnv("URL"));
		assertEquals("https://www.wikipedia.org", manager.getEnvOrElse("URL2", "URL"));
		assertEquals("empty", manager.getEnvOrDefault("URL2", "empty"));

		assertEquals("78911562", manager.getEnv("INT_VALUE"));
		assertEquals(Optional.of(78911562), manager.getEnvAsInt("INT_VALUE"));
		
		assertEquals("FALSE", manager.getEnv("BUSY"));
		assertEquals(Optional.of(false), manager.getEnvAsBoolean("BUSY"));
		
		assertEquals("78.65", manager.getEnv("THRESHOLD"));
		assertEquals(Optional.of(78.65D), manager.getEnvAsDouble("THRESHOLD"));
	}
}
