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

	/**
	 * Create an array of {@link BooleanDataType} for a given {@code boolean[]}.
	 * @param name The name of the array.
	 * @param values The array of {@code boolean}.
	 * @return The {@link ArrayDataType} of {@link BooleanDataType}.
	 */
	public static ArrayDataType<BooleanDataType> asArray(String name, boolean...values) {
		final BooleanDataType[] result = new BooleanDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new BooleanDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	/**
     * Convert an {@link ArrayDataType} of {@link BooleanDataType} to a {@code boolean[]}.
     * @param values The {@link ArrayDataType} of {@link BooleanDataType}.
     * @return The {@code boolean[]} array
     */
	public static boolean[] fromBooleanArray(ArrayDataType<BooleanDataType> values) {
		if(values == null) return new boolean[0]; // Prevent array is null
		final boolean[] result = new boolean[values.getValue().length];
		for(int i = 0; i < result.length; i++) result[i] = values.getValue()[i].getValue();
		
		return result;
	}
	
	/**
	 * Create an array of {@link ByteDataType} for a given {@code byte[]}.
	 * 
	 * @param name   The name of the array.
	 * @param values The array of {@code byte}.
	 * @return The {@link ArrayDataType} of {@link ByteDataType}.
	 */
	public static ArrayDataType<ByteDataType> asArray(String name, byte...values) {
		final ByteDataType[] result = new ByteDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new ByteDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	/**
     * Convert an {@link ArrayDataType} of {@link ByteDataType} to a {@code byte[]}.
     * 
     * @param values The {@link ArrayDataType} of {@link ByteDataType}.
     * @return The {@code byte[]} array
     */
	public static byte[] fromByteArray(ArrayDataType<ByteDataType> values) {
		if(values == null) return new byte[0]; // Prevent array is null
		final byte[] result = new byte[values.getValue().length];
		for(int i = 0; i < result.length; i++) result[i] = values.getValue()[i].getValue();
		
		return result;
	}
	
	/**
	 * Create an array of {@link DoubleDataType} for a given {@code double[]}.
	 * 
	 * @param name   The name of the array.
	 * @param values The array of {@code double}.
	 * @return The {@link ArrayDataType} of {@link DoubleDataType}.
	 */
	public static ArrayDataType<DoubleDataType> asArray(String name, double...values) {
		final DoubleDataType[] result = new DoubleDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new DoubleDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	/**
     * Convert an {@link ArrayDataType} of {@link DoubleDataType} to a {@code double[]}.
     * 
     * @param values The {@link ArrayDataType} of {@link DoubleDataType}.
     * @return The {@code double[]} array
     */
	public static double[] fromDoubleArray(ArrayDataType<DoubleDataType> values) {
		if(values == null) return new double[0]; // Prevent array is null
		final double[] result = new double[values.getValue().length];
		for(int i = 0; i < result.length; i++) result[i] = values.getValue()[i].getValue();
		
		return result;
	}
	
	/**
     * Create an array of {@link FloatDataType} for a given {@code float[]}.
     * 
     * @param name   The name of the array.
     * @param values The array of {@code float}.
     * @return The {@link ArrayDataType} of {@link FloatDataType
     */
	public static ArrayDataType<FloatDataType> asArray(String name, float...values) {
		final FloatDataType[] result = new FloatDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new FloatDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	/**
     * Convert an {@link ArrayDataType} of {@link FloatDataType} to a {@code float[]}.
     * 
     * @param values The {@link ArrayDataType} of {@link FloatDataType}.
     * @return The {@code float[]} array
     */
	public static float[] fromFloatArray(ArrayDataType<FloatDataType> values) {
		if(values == null) return new float[0]; // Prevent array is null
		final float[] result = new float[values.getValue().length];
		for(int i = 0; i < result.length; i++) result[i] = values.getValue()[i].getValue();
		
		return result;
	}

	/**
	 * Create an array of {@link IntegerDataType} for a given {@code int[]}.
	 * 
	 * @param name   The name of the array.
	 * @param values The array of {@code int}.
	 * @return The {@link ArrayDataType} of {@link IntegerDataType}.
	 */
	public static ArrayDataType<IntegerDataType> asArray(String name, int...values) {
		final IntegerDataType[] result = new IntegerDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new IntegerDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	/**
	 * Convert an {@link ArrayDataType} of {@link IntegerDataType} to a
	 * {@code int[]}.
	 * 
	 * @param values The {@link ArrayDataType} of {@link IntegerDataType}.
	 * @return The {@code int[]} array
	 */
	public static int[] fromIntArray(ArrayDataType<IntegerDataType> values) {
		if(values == null) return new int[0]; // Prevent array is null
		final int[] result = new int[values.getValue().length];
		for(int i = 0; i < result.length; i++) result[i] = values.getValue()[i].getValue();
		
		return result;
	}

	/**
     * Create an array of {@link LongDataType} for a given {@code long[]}.
     * 
     * @param name   The name of the array.
     * @param values The array of {@code long}.
     * @return The {@link ArrayDataType} of {@link LongDataType
     */
	public static ArrayDataType<LongDataType> asArray(String name, long...values) {
		final LongDataType[] result = new LongDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new LongDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	/**
     * Convert an {@link ArrayDataType} of {@link LongDataType} to a {@code long[]}.
     * 
     * @param values The {@link ArrayDataType} of {@link LongDataType}.
     * @return The {@code long[]} array
     */
	public static long[] fromLongArray(ArrayDataType<LongDataType> values) {
		if(values == null) return new long[0]; // Prevent array is null
		final long[] result = new long[values.getValue().length];
		for(int i = 0; i < result.length; i++) result[i] = values.getValue()[i].getValue();
		
		return result;
	}
	
	/**
	 * Create an array of {@link ShortDataType} for a given {@code short[]}.
	 * 
	 * @param name   The name of the array.
	 * @param values The array of {@code short}.
	 * @return The {@link ArrayDataType} of {@link ShortDataType}.
	 */
	public static ArrayDataType<ShortDataType> asArray(String name, short...values) {
		final ShortDataType[] result = new ShortDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new ShortDataType(values[i]);
		
		return new ArrayDataType<>(name, result);
	}
	
	/**
     * Convert an {@link ArrayDataType} of {@link ShortDataType} to a {@code short[]}.
     * 
     * @param values The {@link ArrayDataType} of {@link ShortDataType}.
     * @return The {@code short[]} array
     */
	public static short[] fromShortArray(ArrayDataType<ShortDataType> values) {
		if(values == null) return new short[0]; // Prevent array is null
		final short[] result = new short[values.getValue().length];
		for(int i = 0; i < result.length; i++) result[i] = values.getValue()[i].getValue();
		
		return result;
	}
	
	/**
	 * Create a list of {@link DataType} for a given {@code DataType[]}.
	 * @param <E> The type of the list.
	 * @param name The name of the list.
	 * @param values The array of {@code DataType}.
	 * @return The {@link ListDataType} of {@link DataType}.
	 */
	@SuppressWarnings("unchecked")
	public static <E extends DataType<?>> ListDataType<E> asList(String name, E... values) {
		return new ListDataType<>(name, Arrays.stream(values).toList());
	}
	
	/**
	 * Create a list of {@link StringDataType} for a given {@code List<String>}.
	 * @param name The name of the list.
	 * @param values The list of {@code String}.
	 * @return The {@link ListDataType} of {@link StringDataType}.
	 */
	public static ListDataType<StringDataType> asStringList(String name, List<String> values) {
		final List<StringDataType> result = new ArrayList<>();
		for(int i = 0; i < values.size(); i++) result.add(new StringDataType("value_"+i, values.get(i)));
		
		return new ListDataType<>(name, result);
	}
	
	/**
	 * Convert a {@link ListDataType} of {@link StringDataType} to a {@code List<String>}.
	 * @param values The {@link ListDataType} of {@link String}.
	 * @return The {@code List<String>} list
	 */
	public static List<String> fromStringList(ListDataType<StringDataType> values) {
		final List<String> result = new ArrayList<>();
		values.getValue().forEach(v -> result.add(v.getValue()));

		return result;
	}
	
	/**
	 * Create a list of {@link DatetimeDataType} for a given {@code List<DateTime>}.
	 * 
	 * @param name   The name of the list.
	 * @param values The list of {@code DateTime}.
	 * @return The {@link ListDataType} of {@link DatetimeDataType}.
	 */
	public static ListDataType<DatetimeDataType> asDatetimeList(String name, List<DateTime> values) {
		final List<DatetimeDataType> result = new ArrayList<>();
		for(int i = 0; i < values.size(); i++) result.add(new DatetimeDataType("value_"+i, values.get(i)));
		
		return new ListDataType<>(name, result);
	}
	
	/**
	 * Convert a {@link ListDataType} of {@link DatetimeDataType} to a
	 * {@code List<DateTime>}.
	 * 
	 * @param values The {@link ListDataType} of {@link DatetimeDataType}.
	 * @return The {@code List<DateTime>} list
	 */
	public static List<DateTime> fromDatetimeList(ListDataType<DatetimeDataType> values) {
		final List<DateTime> result = new ArrayList<>();
		values.getValue().forEach(v -> result.add(v.getValue()));

		return result;
	}
}
