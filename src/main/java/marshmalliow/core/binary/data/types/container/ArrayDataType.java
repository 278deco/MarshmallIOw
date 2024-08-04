package marshmalliow.core.binary.data.types.container;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import marshmalliow.core.binary.data.types.DataType;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;
import marshmalliow.core.io.BinaryReader;
import marshmalliow.core.io.BinaryWriter;

/**
 * Array data type corresponding to the format specification.
 * @version	1.0.0
 * @author 278deco
 */
public class ArrayDataType<T extends DataType<?>> extends DataType<T[]> {

	public ArrayDataType() {
		super();
	}

	public ArrayDataType(String name, T[] value) {
		super(name, value);
	}

	@Override
	public void writeValue(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException {
		if(this.getValue().length > 0) {
			if(this.getValue()[0].getCategory() != Category.PRIMITIVE) throw new IOException("Array type cannot contains non primitive datatype");
			
			writer.writeByte(this.getValue()[0].getId());
			writer.writeInt(this.getValue().length);
			
			for(DataType<?> data : this.getValue()) {
				data.write(writer, registry, charset);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		final byte dataTypeId = reader.readByte();
		final Class<? extends DataType<?>> dataTypeClass = registry.getDataTypeByID(dataTypeId);
		final int size = reader.readInt();
		
		final DataType<?>[] content = new DataType<?>[size];
		
		if(dataTypeClass == null) throw new IOException();

		DataType<?> readDataType;
		for(int i = 0; i < size; i++) {
			try {
				final Constructor<? extends DataType<?>> constructor = dataTypeClass.getDeclaredConstructor();

				readDataType = constructor.newInstance();
			} catch (ReflectiveOperationException e) {
				throw new IOException();
			}

			readDataType.read(reader, registry, charset);

			content[i] = readDataType;
		}

		this.setValue((T[]) content);
		
	}
	
	@Override
	public String toString() {
		return Arrays.asList(this.value).toString();
	}
	
	@Override
	public byte getId() {
		return DataTypeEnum.ARRAY.getId();
	}
	
	@Override
	public Category getCategory() {
		return Category.CONTAINER;
	}

}
