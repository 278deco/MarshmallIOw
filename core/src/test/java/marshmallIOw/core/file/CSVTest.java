package marshmallIOw.core.file;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import marshmalliow.core.directory.DirectoryRegistry;
import marshmalliow.core.directory.GlobalDirectoryRegistry;
import marshmalliow.core.file.CSVFile;
import marshmalliow.core.file.ReadMode;
import marshmalliow.core.file.CSVFile.CSVFileBuilder;
import marshmalliow.core.file.csv.CSVProperties;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) 
public class CSVTest {
	
	@Test 
	@Order(1)
	public void createRegistryTest() {
		final DirectoryRegistry registry = new DirectoryRegistry();
		assertNotNull(registry);
		
		final DirectoryRegistry globalRegistry = GlobalDirectoryRegistry.get();
		
		globalRegistry.register("test:resources", Path.of("src/test/resources").toUri());
		
		assertEquals(1, globalRegistry.directoriesSize());
		assertNotNull(globalRegistry.get("test:resources"));		
	}
	
	@Test
	public void readCSV() {
		assertNotNull(GlobalDirectoryRegistry.get());

		final CSVProperties props = CSVProperties
				.builder()
				.hasHeader(false)
				.lineSeparator(CSVProperties.DEFAULT_LINE_SEPARATOR)
				.build();
		
		assertNotNull(props);
		
		final CSVFile file = CSVFileBuilder.builder(GlobalDirectoryRegistry.get())
				.directoryId("test:resources")
				.name("CSV_test1.csv")
				.properties(props)
				.build();
		
		assertNotNull(file);
		assertDoesNotThrow(() -> file.readFile(ReadMode.NORMAL));
	}

}
