package marshmallIOw.core.builder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import marshmalliow.core.builder.DirectoryManager;
import marshmalliow.core.builder.JSONFactory;
import marshmalliow.core.json.JSONFile;
import marshmalliow.core.json.objects.JSONArray;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.objects.Directory;
import marshmalliow.core.objects.Null;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) 
public class JSONFactoryTest {

	@Test 
	@Order(1)
	public void createFactoryTest() {
		assertDoesNotThrow(() -> {
			JSONFactory.get();
		});
		
		final DirectoryManager manager = new DirectoryManager();
		assertNotNull(manager);
		
		final Directory resources = new Directory("test:resources", "src/test/resources");
		assertNotNull(resources);
		
		manager.registerNewDirectory(resources);
		
		assertEquals(1, manager.getDirectoriesNumber());
		assertNotNull(manager.getLoadedDirectory("test:resources"));
		
		JSONFactory.withDirectoryManager(manager);
	}
	
	@Test
	public void parsingFromStringTest() {
		assertNotNull(JSONFactory.get());
		
		final String jsonTest = "{\"key_1\":\"hello world\", \"key_2\": 1234}";
		final JSONObject jsonObject = new JSONObject(Map.of("key_1", "hello world", "key_2", 1234));
		assertNotNull(jsonTest);
		assertNotNull(jsonObject);
		
		final StringReader reader = new StringReader(jsonTest);
		assertNotNull(reader);
		
		try {
			final JSONObject jsonObjectParsed = JSONFactory.get().parseJSON(reader, JSONObject.class);
			assertNotNull(jsonObjectParsed);
			
			assertEquals(jsonObject, jsonObjectParsed);
		} catch (Exception e) {
			fail("An exception was thrown while parsing the JSON string.");
		}
	}
	
	@Test
	public void parsingFromByteTest() {
		assertNotNull(JSONFactory.get());

		final byte[] jsonTest = {0x7b, 0x22, 0x6b, 0x65, 0x79, 0x5f, 0x31, 0x22, 0x3a, 0x22, 0x68, 0x65, 0x6c, 0x6c,
				0x6f, 0x20, 0x77, 0x6f, 0x72, 0x6c, 0x64, 0x22, 0x2c, 0x20, 0x22, 0x6b, 0x65, 0x79, 0x5f, 0x32, 0x22,
				0x3a, 0x20, 0x31, 0x32, 0x33, 0x34, 0x7d};
		
		final JSONObject jsonObject = new JSONObject(Map.of("key_1", "hello world", "key_2", 1234));
		assertNotNull(jsonTest);
		assertNotNull(jsonObject);

		try {
			final JSONObject jsonObjectParsedAuto = JSONFactory.get().parseJSON(jsonTest, JSONObject.class);
			final JSONObject jsonObjectParsed = JSONFactory.get().parseJSON(jsonTest, StandardCharsets.UTF_8, JSONObject.class);
			assertNotNull(jsonObjectParsedAuto);
			assertNotNull(jsonObjectParsed);
			
			assertEquals(jsonObject, jsonObjectParsed);
			assertEquals(jsonObject, jsonObjectParsedAuto);
		} catch (Exception e) {
			fail("An exception was thrown while parsing the JSON string.");
		}
	}
	
	@Test
	public void readFromFileTest() {
		assertNotNull(JSONFactory.get());
        
		final JSONFile file = JSONFactory.get().createJSONFile("test:resources", "JSONFactory_test1", null);
		
		assertNotNull(file);
		assertDoesNotThrow(() -> file.readFile());
		
		final JSONObject jsonObject = file.getContentAsObject();
		assertNotNull(jsonObject);
		assertNotEquals(0, jsonObject.size());
				
		assertNotNull(jsonObject.get("id"));
		assertEquals(1, jsonObject.get("id"));
		
		assertNotNull(jsonObject.get("user"));
		assertEquals("marshmallIOw", jsonObject.get("user", String.class));
		
		assertNotNull(jsonObject.get("is_present"));
		assertEquals(true, jsonObject.get("is_present"));
		
		assertNotNull(jsonObject.get("mapping", JSONObject.class));
		final JSONObject mapping = jsonObject.get("mapping", JSONObject.class);
		assertNotNull(mapping);
		
		assertNotNull(mapping.get("aliases"));
		final JSONArray aliases = mapping.get("aliases", JSONArray.class);
		assertNotNull(aliases);
		assertEquals(4, aliases.size());
		assertEquals("a", aliases.get(0));
		
		assertNotNull(mapping.get("expired_when"));
		assertEquals(15, mapping.get("expired_when"));
		
		assertNotNull(mapping.get("attachement"));
		assertEquals(Null.NULL, mapping.get("attachement"));
	}
	
	@Test
	public void readFromFile2Test() {
		assertNotNull(JSONFactory.get());
        
		final JSONFile file = JSONFactory.get().createJSONFile("test:resources", "JSONFactory_test2.json", null);
		
		assertNotNull(file);
		assertDoesNotThrow(() -> file.readFile());
		
		final JSONObject jsonObject = file.getContentAsObject();
		assertNull(jsonObject);
		
		final JSONArray jsonArray = file.getContentAsArray();
		assertNotNull(jsonArray);
		
		assertEquals(6, jsonArray.size());
		final List<String> expected = List.of("carrot", "potato", "salad", "zucchini", "tomato", "onion");
		assertNotNull(expected);
		
		assertEquals(expected, jsonArray);
	}
	
}
