package marshmalliow.core.binary.data.types.primitive;

import java.io.IOException;

import marshmalliow.core.binary.data.types.DataType;
import marshmalliow.core.binary.io.BinaryReader;
import marshmalliow.core.binary.io.BinaryWriter;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;

public class BooleanDataType extends DataType<Boolean> {

	public BooleanDataType() {
		super();
	}

	public BooleanDataType(String name, boolean value) {
		super(name, value);
	}
	
	public BooleanDataType(boolean value) {
		super(value);
	}
	
	@Override
	public void writeValue(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException {
		writer.writeBoolean(this.getValue());
	}

	@Override
	public void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		this.setValue(reader.readBoolean());
	}

	@Override
	public byte getId() {
		return DataTypeEnum.BOOLEAN.getId();
	}
	
	@Override
	public Category getCategory() {
		return Category.PRIMITIVE;
	}

}
