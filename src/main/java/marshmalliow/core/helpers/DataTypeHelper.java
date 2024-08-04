package marshmalliow.core.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import marshmalliow.core.binary.data.types.DataType;
import marshmalliow.core.binary.data.types.advanced.DatetimeDataType;
import marshmalliow.core.binary.data.types.advanced.StringDataType;
import marshmalliow.core.binary.data.types.container.ArrayDataType;
import marshmalliow.core.binary.data.types.container.ListDataType;
import marshmalliow.core.binary.data.types.primitive.BooleanDataType;
import marshmalliow.core.binary.data.types.primitive.ByteDataType;
import marshmalliow.core.binary.data.types.primitive.DoubleDataType;
import marshmalliow.core.binary.data.types.primitive.FloatDataType;
import marshmalliow.core.binary.data.types.primitive.IntegerDataType;
import marshmalliow.core.binary.data.types.primitive.LongDataType;
import marshmalliow.core.binary.data.types.primitive.ShortDataType;
import marshmalliow.core.objects.DateTime;

public class DataTypeHelper {

	public static ArrayDataType<BooleanDataType> asArray(String name, boolean...values) {
		final BooleanDataType[] result = new BooleanDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new BooleanDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	public static boolean[] fromBooleanArray(ArrayDataType<BooleanDataType> values) {
		if(values == null) return new boolean[0]; // Prevent array is null
		final boolean[] result = new boolean[values.getValue().length];
		for(int i = 0; i < result.length; i++) result[i] = values.getValue()[i].getValue();
		
		return result;
	}
	
	public static ArrayDataType<ByteDataType> asArray(String name, byte...values) {
		final ByteDataType[] result = new ByteDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new ByteDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	public static byte[] fromByteArray(ArrayDataType<ByteDataType> values) {
		if(values == null) return new byte[0]; // Prevent array is null
		final byte[] result = new byte[values.getValue().length];
		for(int i = 0; i < result.length; i++) result[i] = values.getValue()[i].getValue();
		
		return result;
	}
	
	public static ArrayDataType<DoubleDataType> asArray(String name, double...values) {
		final DoubleDataType[] result = new DoubleDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new DoubleDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	public static double[] fromDoubleArray(ArrayDataType<DoubleDataType> values) {
		if(values == null) return new double[0]; // Prevent array is null
		final double[] result = new double[values.getValue().length];
		for(int i = 0; i < result.length; i++) result[i] = values.getValue()[i].getValue();
		
		return result;
	}
	
	public static ArrayDataType<FloatDataType> asArray(String name, float...values) {
		final FloatDataType[] result = new FloatDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new FloatDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	public static float[] fromFloatArray(ArrayDataType<FloatDataType> values) {
		if(values == null) return new float[0]; // Prevent array is null
		final float[] result = new float[values.getValue().length];
		for(int i = 0; i < result.length; i++) result[i] = values.getValue()[i].getValue();
		
		return result;
	}

	public static ArrayDataType<IntegerDataType> asArray(String name, int...values) {
		final IntegerDataType[] result = new IntegerDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new IntegerDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	public static int[] fromIntArray(ArrayDataType<IntegerDataType> values) {
		if(values == null) return new int[0]; // Prevent array is null
		final int[] result = new int[values.getValue().length];
		for(int i = 0; i < result.length; i++) result[i] = values.getValue()[i].getValue();
		
		return result;
	}

	public static ArrayDataType<LongDataType> asArray(String name, long...values) {
		final LongDataType[] result = new LongDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new LongDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	public static long[] fromLongArray(ArrayDataType<LongDataType> values) {
		if(values == null) return new long[0]; // Prevent array is null
		final long[] result = new long[values.getValue().length];
		for(int i = 0; i < result.length; i++) result[i] = values.getValue()[i].getValue();
		
		return result;
	}
	
	public static ArrayDataType<ShortDataType> asArray(String name, short...values) {
		final ShortDataType[] result = new ShortDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new ShortDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	public static short[] fromShortArray(ArrayDataType<ShortDataType> values) {
		if(values == null) return new short[0]; // Prevent array is null
		final short[] result = new short[values.getValue().length];
		for(int i = 0; i < result.length; i++) result[i] = values.getValue()[i].getValue();
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <E extends DataType<?>> ListDataType<E> asList(String name, E... values) {
		return new ListDataType<>(name, Arrays.stream(values).toList());
	}
	
	public static ListDataType<StringDataType> asStringList(String name, List<String> values) {
		final List<StringDataType> result = new ArrayList<>();
		for(int i = 0; i < values.size(); i++) result.add(new StringDataType("value_"+i, values.get(i)));
		
		return new ListDataType<>(name, result);
	}
	
	public static List<String> fromStringList(ListDataType<StringDataType> values) {
		final List<String> result = new ArrayList<>();
		values.getValue().forEach(v -> result.add(v.getValue()));

		return result;
	}
	
	public static ListDataType<DatetimeDataType> asDatetimeList(String name, List<DateTime> values) {
		final List<DatetimeDataType> result = new ArrayList<>();
		for(int i = 0; i < values.size(); i++) result.add(new DatetimeDataType("value_"+i, values.get(i)));
		
		return new ListDataType<>(name, result);
	}
	
	public static List<DateTime> fromDatetimeList(ListDataType<DatetimeDataType> values) {
		final List<DateTime> result = new ArrayList<>();
		values.getValue().forEach(v -> result.add(v.getValue()));

		return result;
	}
}
