package marshmalliow.core.binary.data.types.primitive;

import java.io.IOException;

import marshmalliow.core.binary.data.types.DataType;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;
import marshmalliow.core.io.BinaryReader;
import marshmalliow.core.io.BinaryWriter;

/**
 * Float data type corresponding to the format specification.
 * @version	1.0.0
 * @author 278deco
 */
public class FloatDataType extends DataType<Float> {

	public FloatDataType() {
		super();
	}

	public FloatDataType(String name, float value) {
		super(name, value);
	}
	
	public FloatDataType(float value) {
		super(value);
	}
	
	@Override
	public void writeValue(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException {
		writer.writeFloat(this.getValue());
	}

	@Override
	public void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		this.setValue(reader.readFloat());
	}

	@Override
	public byte getId() {
		return DataTypeEnum.FLOAT.getId();
	}
	
	@Override
	public Category getCategory() {
		return Category.PRIMITIVE;
	}

}
