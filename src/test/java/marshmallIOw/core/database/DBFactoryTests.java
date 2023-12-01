package marshmallIOw.core.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import marshmalliow.core.database.DBFactory;
import marshmalliow.core.database.security.DBCredentials;
import marshmalliow.core.database.utils.DatabaseType;
import marshmalliow.core.json.io.JSONLexer;
import marshmalliow.core.json.io.JSONParser;
import marshmalliow.core.json.objects.JSONObject;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DBFactoryTests {
	
	@BeforeAll
	public static void initFactory() {
		JSONObject content = new JSONObject();
		BufferedReader reader = null;
		JSONLexer lexer = null;
		try {
			
			reader = Files.newBufferedReader(Path.of("src/test/resources/.credentials"));
			lexer = new JSONLexer(reader);
			final JSONParser parser = new JSONParser(lexer);
			
			content = (JSONObject)parser.parse();			
		}catch(IOException e) {
			System.err.println(e);
			return;
		}finally {
			try {
				if(lexer != null) lexer.close();
				if(reader != null) reader.close();
			}catch(NumberFormatException | IOException e) {
				fail("Error was throwned when getting credentials");
				return;
			}
		}
		
		if(!content.isEmpty())  {
			
			DBCredentials credentials = DBCredentials.builder()
				.host(content.get("host", String.class), content.get("port", Integer.class))
				.username(content.get("username", String.class))
				.password(content.get("password", String.class))
				.build();
			
			DBFactory.newInstance(credentials);
		}else {
			fail("Credentials content is empty");
		}
	}
	
	@Test
	@Order(1)
	public void factoryPresent() {
		assertNotNull(DBFactory.get());
	}
	
	@Test
	@Order(2)
	public void setupDatabase() {
		assertNotNull(DBFactory.get().getTable(DBTableTest.class, DatabaseType.MARIADB));
		
		try {
			DBFactory.get().getTable(DBTableTest.class, DatabaseType.MARIADB).createDatabase();
			DBFactory.get().getTable(DBTableTest.class, DatabaseType.MARIADB).createTable();
		} catch (SQLException e) {
			fail("SQLException was thrown.");
		}
	}
	
	@Test
	@Order(3)
	public void testInsertStatement() {
		try {
			DBFactory.get().getTable(DBTableTest.class, DatabaseType.MARIADB).addNewTestValues();
		} catch (SQLException e) {
			fail("SQLException was thrown.");
		}
	}
	
	@Test
	@Order(4)
	public void testSelectStatement() {
		try {
			DBFactory.get().getTable(DBTableTest.class, DatabaseType.MARIADB).getNewTestValues();
		} catch (SQLException e) {
			fail("SQLException was thrown when getting all the table.");
		}
		
		try {
			DBFactory.get().getTable(DBTableTest.class, DatabaseType.MARIADB).getNewTestValuesWithValue();
		} catch (SQLException e) {
			fail("SQLException was thrown when getting data with specifics columns.");
		}
		
		try {
			DBFactory.get().getTable(DBTableTest.class, DatabaseType.MARIADB).getNewTestValuesWithWhereClause();
		} catch (SQLException e) {
			fail("SQLException was thrown when getting data with where clause.");
		}
	}
	
	@Test
	@Order(5)
	public void testCountStatement() {
		try {
			int result = DBFactory.get().getTable(DBTableTest.class, DatabaseType.MARIADB).countTestValues();
			assertTrue(result >= 1);
		} catch (SQLException e) {
			fail("SQLException was thrown.");
		}
	}
	
	@Test
	@Order(6)
	public void testUpdateStatement() {
		try {
			DBFactory.get().getTable(DBTableTest.class, DatabaseType.MARIADB).updateTestValue(1);
			final List<Object> result = DBFactory.get().getTable(DBTableTest.class, DatabaseType.MARIADB).getTestValue(1);
			
			assertTrue(result.size() == 5); //The number of columns present in the table
			assertInstanceOf(String.class, result.get(2)); //The column index 2 is 'surname'
			assertEquals((String)result.get(2), "Updated");
		} catch (SQLException e) {
			fail("SQLException was thrown.");
		}
	}
	
	@Test
	@Order(7)
	public void testDeleteStatement() {
		try {
			int before = DBFactory.get().getTable(DBTableTest.class, DatabaseType.MARIADB).countTestValues();
			DBFactory.get().getTable(DBTableTest.class, DatabaseType.MARIADB).deleteTestValue();
			int after = DBFactory.get().getTable(DBTableTest.class, DatabaseType.MARIADB).countTestValues();

			assertTrue(before == (after+1));
		} catch (SQLException e) {
			fail("SQLException was thrown.");
		}
	}
}
