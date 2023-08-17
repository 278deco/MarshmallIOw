package marshmalliow.core.helpers;

import java.util.Arrays;

import marshmalliow.core.binary.data.types.DataType;
import marshmalliow.core.binary.data.types.container.ArrayDataType;
import marshmalliow.core.binary.data.types.container.ListDataType;
import marshmalliow.core.binary.data.types.primitive.BooleanDataType;
import marshmalliow.core.binary.data.types.primitive.ByteDataType;
import marshmalliow.core.binary.data.types.primitive.DoubleDataType;
import marshmalliow.core.binary.data.types.primitive.FloatDataType;
import marshmalliow.core.binary.data.types.primitive.IntegerDataType;
import marshmalliow.core.binary.data.types.primitive.LongDataType;
import marshmalliow.core.binary.data.types.primitive.ShortDataType;

public class DataTypeHelper {

	public static ArrayDataType<BooleanDataType> asArray(String name, boolean...values) {
		final BooleanDataType[] result = new BooleanDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new BooleanDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	public static ArrayDataType<ByteDataType> asArray(String name, byte...values) {
		final ByteDataType[] result = new ByteDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new ByteDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	public static ArrayDataType<DoubleDataType> asArray(String name, double...values) {
		final DoubleDataType[] result = new DoubleDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new DoubleDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	public static ArrayDataType<FloatDataType> asArray(String name, float...values) {
		final FloatDataType[] result = new FloatDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new FloatDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}

	public static ArrayDataType<IntegerDataType> asArray(String name, int...values) {
		final IntegerDataType[] result = new IntegerDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new IntegerDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}

	public static ArrayDataType<LongDataType> asArray(String name, long...values) {
		final LongDataType[] result = new LongDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new LongDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	public static ArrayDataType<ShortDataType> asArray(String name, short...values) {
		final ShortDataType[] result = new ShortDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new ShortDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	@SuppressWarnings("unchecked")
	public static <E extends DataType<?>> ListDataType<E> asList(String name, E... values) {
		return new ListDataType<>(name, Arrays.stream(values).toList());
	}
}
