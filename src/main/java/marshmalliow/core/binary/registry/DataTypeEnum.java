package marshmalliow.core.binary.registry;


import org.apache.logging.log4j.Logger;

import marshmalliow.core.binary.data.advanced.DatetimeDataType;
import marshmalliow.core.binary.data.advanced.StringDataType;
import marshmalliow.core.binary.data.container.ArrayDataType;
import marshmalliow.core.binary.data.container.ListDataType;
import marshmalliow.core.binary.data.container.ObjectDataType;
import marshmalliow.core.binary.data.primitive.BooleanDataType;
import marshmalliow.core.binary.data.primitive.ByteDataType;
import marshmalliow.core.binary.data.primitive.DoubleDataType;
import marshmalliow.core.binary.data.primitive.FloatDataType;
import marshmalliow.core.binary.data.primitive.IntegerDataType;
import marshmalliow.core.binary.data.primitive.LongDataType;
import marshmalliow.core.binary.data.primitive.ShortDataType;
import marshmalliow.core.exceptions.DatatypeRegistryException;

public enum DataTypeEnum {

	/**
	 * NULL value.
	 */
	NULL(0),
	
	/**
	 * Object data
	 * @see ObjectDataType
	 */
	OBJECT(1),
	
	/**
	 * Byte data
	 * @see ByteDataType
	 */
	BYTE(2),
	
	/**
	 * Boolean data
	 * @see BooleanDataType
	 */
	BOOLEAN(3),
	
	/**
	 * Short data
	 * @see ShortDataType
	 */
	SHORT(4),
	
	/**
	 * Integer data
	 * @see IntegerDataType
     */
	INT(5),
	
	/**
	 * Long data
	 * @see LongDataType
	 */
	LONG(6),
	
	/**
	 * Float data
	 * @see FloatDataType
	 */
	FLOAT(7),
	
	/**
	 * Double data
	 * @see Double
	 */
	DOUBLE(8),
	
	/**
	 * String data
	 * @see StringDataType
	 */
	STRING(9),
	
	/**
	 * Datetime data
	 * @see DatetimeDataType
	 */
	DATETIME(10),
	
	/**
	 * List data
	 * @see ListDataType
	 */
	LIST(11),
	
	/**
	 * Array data
	 * @see ArrayDataType
	 */
	ARRAY(12);

	private byte id;
	private DataTypeEnum(int id) {
		this.id = (byte)id;
	}

	/**
	 * Get the id of the data type.
	 * @return the id of the data type.
	 */
	public byte getId() {
		return id;
	}

	/**
	 * Create a new registry with all default data types.
	 * 
	 * @return a new registry with all default data
	 * @throws DatatypeRegistryException if an error occurs while registering
	 */
	public static DataTypeRegistry.Builder createNewRegistry() throws DatatypeRegistryException {
		final DataTypeRegistry.Builder builder = DataTypeRegistry.builder();

		builder.register(OBJECT.getId(), ObjectDataType.class);
		builder.register(BYTE.getId(), ByteDataType.class);
		builder.register(BOOLEAN.getId(), BooleanDataType.class);
		builder.register(SHORT.getId(), ShortDataType.class);
		builder.register(INT.getId(), IntegerDataType.class);
		builder.register(LONG.getId(), LongDataType.class);
		builder.register(FLOAT.getId(), FloatDataType.class);
		builder.register(DOUBLE.getId(), DoubleDataType.class);
		builder.register(STRING.getId(), StringDataType.class);
		builder.register(DATETIME.getId(), DatetimeDataType.class);
		builder.register(LIST.getId(), ListDataType.class);
		builder.register(ARRAY.getId(), ArrayDataType.class);

		return builder;
	}
	
	/**
	 * Create a new registry with all default data types.
	 * <p>
	 * This method is safe and will log any error that occurs while registering 
	 * rather than throwing an exception.
	 * @param logger The logger to log any errors that occur
	 * @return a new registry with all default data
	 */
	public static DataTypeRegistry.Builder createSafeNewRegistry(Logger logger) {
		final DataTypeRegistry.Builder builder = DataTypeRegistry.builder();

		try {
		builder.register(OBJECT.getId(), ObjectDataType.class);
		builder.register(BYTE.getId(), ByteDataType.class);
		builder.register(BOOLEAN.getId(), BooleanDataType.class);
		builder.register(SHORT.getId(), ShortDataType.class);
		builder.register(INT.getId(), IntegerDataType.class);
		builder.register(LONG.getId(), LongDataType.class);
		builder.register(FLOAT.getId(), FloatDataType.class);
		builder.register(DOUBLE.getId(), DoubleDataType.class);
		builder.register(STRING.getId(), StringDataType.class);
		builder.register(DATETIME.getId(), DatetimeDataType.class);
		builder.register(LIST.getId(), ListDataType.class);
		builder.register(ARRAY.getId(), ArrayDataType.class);
		}catch(DatatypeRegistryException e) {
			logger.fatal("Couldn't initialize a safe data type registry with default types");
		}
		return builder;
	}

}
