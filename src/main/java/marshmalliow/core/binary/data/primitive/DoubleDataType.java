package marshmalliow.core.binary.data.primitive;

import java.io.IOException;

import marshmalliow.core.binary.data.DataType;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;
import marshmalliow.core.io.BinaryReader;
import marshmalliow.core.io.BinaryWriter;

/**
 * Double data type corresponding to the format specification.
 * @version	1.0.0
 * @author 278deco
 */
public class DoubleDataType extends DataType<Double> {

	public DoubleDataType() {
		super();
	}

	public DoubleDataType(String name, double value) {
		super(name, value);
	}
	
	public DoubleDataType(double value) {
		super(value);
	}
	
	public static DoubleDataType[] asArray(double...values) {
		final DoubleDataType[] result = new DoubleDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new DoubleDataType(values[i]);
		
		return result;
	}

	@Override
	public void writeValue(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException {
		writer.writeDouble(this.getValue());

	}

	@Override
	public void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		this.setValue(reader.readDouble());
	}

	@Override
	public byte getId() {
		return DataTypeEnum.DOUBLE.getId();
	}
	
	@Override
	public Category getCategory() {
		return Category.PRIMITIVE;
	}

}
