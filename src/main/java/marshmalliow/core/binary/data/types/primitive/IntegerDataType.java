package marshmalliow.core.binary.data.types.primitive;

import java.io.IOException;

import marshmalliow.core.binary.data.types.DataType;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;
import marshmalliow.core.io.BinaryReader;
import marshmalliow.core.io.BinaryWriter;

/**
 * Integer data type corresponding to the format specification.
 * @version	1.0.0
 * @author 278deco
 */
public class IntegerDataType extends DataType<Integer> {

	public IntegerDataType() {
		super();
	}

	public IntegerDataType(String name, int value) {
		super(name, value);
	}
	
	public IntegerDataType(int value) {
		super(value);
	}

	@Override
	public void writeValue(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException {
		writer.writeInt(this.getValue());
	}

	@Override
	public void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		this.setValue(reader.readInt());
	}

	@Override
	public byte getId() {
		return DataTypeEnum.INT.getId();
	}
	
	@Override
	public Category getCategory() {
		return Category.PRIMITIVE;
	}

}
