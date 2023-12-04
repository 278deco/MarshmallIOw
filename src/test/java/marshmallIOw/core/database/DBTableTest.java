package marshmallIOw.core.database;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mariadb.r2dbc.api.MariadbStatement;

import marshmalliow.core.database.implementation.DBImplementation;
import marshmalliow.core.database.objects.DBTable;

public class DBTableTest extends DBTable {
	
	private static final Logger LOGGER = LogManager.getLogger(DBTableTest.class);
	
	public DBTableTest(DBImplementation implementation) {
		super(implementation);
	}
	
	public void createDatabase() throws SQLException {
		
		((MariadbStatement)this.implementation.createStatement("CREATE DATABASE IF NOT EXISTS testing_db;")).execute().then().block();
	}
	
	public void createTable() throws SQLException {
		this.implementation.setDatabase("testing_db");
		((MariadbStatement)this.implementation.createStatement("CREATE TABLE IF NOT EXISTS "+getSQLTableName()+" "
				+ "(id INT AUTO_INCREMENT, "
				+ "name VARCHAR(100), "
				+ "surname VARCHAR(100), "
				+ "age TINYINT, "
				+ "isTrusted BOOLEAN,"
				+ "CONSTRAINT "+getSQLTableName()+"_pk PRIMARY KEY (id));")
		).execute().then().block();
	}
	
	public void addNewTestValues() throws SQLException {
		this.implementation.setDatabase("testing_db");
		this.implementation.insert("INSERT INTO "+getSQLTableName()+" (name, surname, age, isTrusted) VALUES(?, ?, ?, ?);", Arrays.asList("Demo", "Test", 20, true));
	}
	
	public void getNewTestValues() throws SQLException {
		this.implementation.setDatabase("testing_db");
		List<List<String>> result = this.implementation.select("SELECT * FROM "+getSQLTableName()+";", Arrays.asList(), String.class);
		
		LOGGER.info("Results for basic get");
		for(int i = 0; i < result.size(); i++) {
			LOGGER.info("Row n{}: {}", i, result.get(i));
		}
	}
	
	public void getNewTestValuesWithValue() throws SQLException {
		this.implementation.setDatabase("testing_db");
		List<List<Object>> result = this.implementation.select("SELECT surname, age, isTrusted FROM "+getSQLTableName()+";", Arrays.asList());
		
		LOGGER.info("Results for selective get");
		for(int i = 0; i < result.size(); i++) {
			LOGGER.info("Row n{}: {}", i, result.get(i));
		}
	}
	
	public void getNewTestValuesWithWhereClause() throws SQLException {
		this.implementation.setDatabase("testing_db");
		List<List<Object>> result = this.implementation.select("SELECT surname, age, isTrusted FROM "+getSQLTableName()+" WHERE name=?;", Arrays.asList("Demo"));
		
		LOGGER.info("Results for selective get with WHERE Clause");
		for(int i = 0; i < result.size(); i++) {
			LOGGER.info("Row n{}: {}", i, result.get(i));
		}
	}
	
	public List<Object> getTestValue(int id) throws SQLException {
		this.implementation.setDatabase("testing_db");
		List<List<Object>> result = this.implementation.select("SELECT * FROM "+getSQLTableName()+" WHERE id=?;", Arrays.asList(id));
		
		return result.isEmpty() ? List.of() : result.get(0);
	}
	
	public void deleteTestValue() throws SQLException {
		this.implementation.setDatabase("testing_db");
		this.implementation.delete("DELETE from "+getSQLTableName()+" ORDER BY id DESC LIMIT 1", Arrays.asList());
	}
	
	public void updateTestValue(int id) throws SQLException {
		this.implementation.setDatabase("testing_db");
		this.implementation.update("UPDATE "+getSQLTableName()+" SET surname=? WHERE id=?", Arrays.asList("Updated", id));
	}
	
	public int countTestValues() throws SQLException {
		this.implementation.setDatabase("testing_db");
		return this.implementation.count("SELECT COUNT(*) FROM "+getSQLTableName(), Arrays.asList());
	}

	@Override
	public String getSQLTableName() {
		return "test_main";
	}
}