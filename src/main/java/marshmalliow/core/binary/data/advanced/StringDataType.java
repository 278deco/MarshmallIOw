package marshmalliow.core.binary.data.advanced;

import java.io.IOException;

import marshmalliow.core.binary.data.DataType;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;
import marshmalliow.core.io.BinaryReader;
import marshmalliow.core.io.BinaryWriter;

/**
 * String data type corresponding to the format specification.
 * @version	1.0.0
 * @author 278deco
 */
public class StringDataType extends DataType<String> {

	public StringDataType() {
		super();
	}

	public StringDataType(String name, String value) {
		super(name, value);
	}

	@Override
	public void writeValue(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException {
		writer.writeUTF(this.getValue(), charset);
	}

	@Override
	public void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		this.setValue(reader.readUTF(charset));
	}

	@Override
	public byte getId() {
		return DataTypeEnum.STRING.getId();
	}
	
	@Override
	public Category getCategory() {
		return Category.ADVANCED;
	}

}
