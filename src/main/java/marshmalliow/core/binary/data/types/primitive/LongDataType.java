package marshmalliow.core.binary.data.types.primitive;

import java.io.IOException;

import marshmalliow.core.binary.data.types.DataType;
import marshmalliow.core.binary.io.BinaryReader;
import marshmalliow.core.binary.io.BinaryWriter;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;

/**
 * Long data type corresponding to the format specification.
 * @version	1.0.0
 * @author 278deco
 */
public class LongDataType extends DataType<Long> {

	public LongDataType() {
		super();
	}

	public LongDataType(String name, long value) {
		super(name, value);
	}
	
	public LongDataType(long value) {
		super(value);
	}

	@Override
	public void writeValue(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException {
		writer.writeLong(this.getValue());
	}

	@Override
	public void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		this.setValue(reader.readLong());
	}

	@Override
	public byte getId() {
		return DataTypeEnum.LONG.getId();
	}
	
	@Override
	public Category getCategory() {
		return Category.PRIMITIVE;
	}

}
