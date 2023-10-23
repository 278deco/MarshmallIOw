package marshmalliow.core.binary.registry;

import marshmalliow.core.binary.data.types.advanced.DatetimeDataType;
import marshmalliow.core.binary.data.types.advanced.StringDataType;
import marshmalliow.core.binary.data.types.container.ArrayDataType;
import marshmalliow.core.binary.data.types.container.ListDataType;
import marshmalliow.core.binary.data.types.container.ObjectDataType;
import marshmalliow.core.binary.data.types.primitive.BooleanDataType;
import marshmalliow.core.binary.data.types.primitive.ByteDataType;
import marshmalliow.core.binary.data.types.primitive.DoubleDataType;
import marshmalliow.core.binary.data.types.primitive.FloatDataType;
import marshmalliow.core.binary.data.types.primitive.IntegerDataType;
import marshmalliow.core.binary.data.types.primitive.LongDataType;
import marshmalliow.core.binary.data.types.primitive.ShortDataType;
import marshmalliow.core.exceptions.DatatypeRegistryException;

public enum DataTypeEnum {

	NULL(0),
	OBJECT(1),
	BYTE(2),
	BOOLEAN(3),
	SHORT(4),
	INT(5),
	LONG(6),
	FLOAT(7),
	DOUBLE(8),
	STRING(9),
	DATETIME(10),
	LIST(11),
	ARRAY(12);

	private byte id;
	private DataTypeEnum(int id) {
		this.id = (byte)id;
	}

	public byte getId() {
		return id;
	}

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

}
