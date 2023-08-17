package marshmalliow.core.binary.data.types.container;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingFormatArgumentException;

import marshmalliow.core.binary.data.types.DataType;
import marshmalliow.core.binary.io.BinaryReader;
import marshmalliow.core.binary.io.BinaryWriter;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;

public class ObjectDataType extends DataType<Map<String, DataType<?>>> {

	private final Object lock = new Object();
	
	public ObjectDataType() {
		super("", new LinkedHashMap<String, DataType<?>>());
	}

	public ObjectDataType(String name) {
		super(name, new LinkedHashMap<String, DataType<?>>());
	}

	public ObjectDataType(String name, Map<String, DataType<?>> value) {
		super(name, value);
	}
	
	@Override
	public void writeValue(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException {
		for(Map.Entry<String, DataType<?>> entry : this.value.entrySet()) {
			writer.writeByte(entry.getValue().getId());
			if(entry.getValue().getName().isEmpty()) throw new IOException("Cannot save a Data Type without a name");
			writer.writeUTF(entry.getValue().getName().get(), charset);

			entry.getValue().write(writer, registry, charset);
		}

		writer.writeByte((byte)0);
		
		this.isModified = false;
	}

	@Override
	public void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		final Map<String, DataType<?>> data = new LinkedHashMap<>();

		this.isModified = false;
		
		byte readByte;
		DataType<?> readDataType;

		while((readByte = reader.readByte()) != DataTypeEnum.NULL.getId()) {
			final Class<? extends DataType<?>> dataTypeClass = registry.getDataTypeByID(readByte);

			if(dataTypeClass == null) throw new IOException();

			try {
				final Constructor<? extends DataType<?>> constructor = dataTypeClass.getDeclaredConstructor();

				readDataType = constructor.newInstance();
			} catch (ReflectiveOperationException e) {
				throw new IOException();
			}

			readDataType.setName(reader.readUTF(charset));
			readDataType.read(reader, registry, charset);

			data.put(readDataType.getName().get(), readDataType);
		}

		this.setValue(data);
	}
	
	public boolean add(DataType<?> data) {
		synchronized (lock) {
			this.isModified = true;
			if(data.getName().isEmpty()) throw new MissingFormatArgumentException("Cannot add a DataType without a name");
			return this.value.put(data.getName().get(), data) == null;
		}
	}
	
	public void add(DataType<?>... datas) {
		synchronized (lock) {
			this.isModified = true;
			for(DataType<?> data : datas) {
				if(data.getName().isEmpty()) throw new MissingFormatArgumentException("Cannot add a DataType without a name");
				this.value.put(data.getName().get(), data);
			}
		}
	}
	
	public DataType<?> get(String name) {
		synchronized (lock) {
			this.isModified = true;
			return this.value.get(name);
		}
	}
	
	public boolean remove(DataType<?> data) {
		synchronized (lock) {
			this.isModified = true;
			return this.value.remove(data.getName(), data);
		}
	}
	
	public boolean contains(DataType<?> data) {
		return data.getName().isPresent() && (this.value.containsKey(data.getName().get()) && this.value.containsValue(data));
	}

	
	@Override
	public Map<String, DataType<?>> getValue() {
		return Collections.unmodifiableMap(this.value);
	}
	
	@Override
	public byte getId() {
		return DataTypeEnum.OBJECT.getId();
	}
	
	@Override
	public Category getCategory() {
		return Category.CONTAINER;
	}
	
	@Override
	public String toString() {
		return this.getName().isPresent() ?  "Object["+this.getName().get()+"]"+this.getValue() : "Object"+this.getValue();
	}

}
