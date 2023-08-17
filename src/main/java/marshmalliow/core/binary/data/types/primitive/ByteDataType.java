package marshmalliow.core.binary.data.types.primitive;

import java.io.IOException;

import marshmalliow.core.binary.data.types.DataType;
import marshmalliow.core.binary.io.BinaryReader;
import marshmalliow.core.binary.io.BinaryWriter;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;

public class ByteDataType extends DataType<Byte> {

	public ByteDataType() {
		super();
	}

	public ByteDataType(String name, byte value) {
		super(name, value);
	}
	
	public ByteDataType(byte value) {
		super(value);
	}
	
	public static ByteDataType[] asArray(byte...values) {
		final ByteDataType[] result = new ByteDataType[values.length];
		for(int i = 0; i < result.length; i++) result[i] = new ByteDataType(values[i]);
		
		return result;
	}

	@Override
	public void writeValue(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException {
		writer.writeByte(this.getValue());
	}

	@Override
	public void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		this.setValue(reader.readByte());
	}

	@Override
	public byte getId() {
		return DataTypeEnum.BYTE.getId();
	}

	@Override
	public Category getCategory() {
		return Category.PRIMITIVE;
	}
	
}
