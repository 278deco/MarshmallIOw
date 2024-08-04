package marshmalliow.core.binary.data.types.primitive;

import java.io.IOException;

import marshmalliow.core.binary.data.types.DataType;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;
import marshmalliow.core.io.BinaryReader;
import marshmalliow.core.io.BinaryWriter;

/**
 * Short data type corresponding to the format specification.
 * @version	1.0.0
 * @author 278deco
 */
public class ShortDataType extends DataType<Short> {

	public ShortDataType() {
		super();
	}

	public ShortDataType(String name, short value) {
		super(name, value);
	}
	
	public ShortDataType(short value) {
		super(value);
	}

	@Override
	public void writeValue(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException {
		writer.writeShort(this.getValue());
	}

	@Override
	public void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		this.setValue(reader.readShort());
	}

	@Override
	public byte getId() {
		return DataTypeEnum.SHORT.getId();
	}
	
	@Override
	public Category getCategory() {
		return Category.PRIMITIVE;
	}

}
