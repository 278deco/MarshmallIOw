package marshmalliow.core.binary.data.container;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.Optional;

import marshmalliow.core.binary.data.DataType;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;
import marshmalliow.core.io.BinaryReader;
import marshmalliow.core.io.BinaryWriter;

/**
 * List data type corresponding to the format specification.
 * @version	1.0.0
 * @author 278deco
 */
public class ListDataType<T extends DataType<?>> extends DataType<List<T>> {

	private final Object lock = new Object();
	
	public ListDataType() {
		super("", new ArrayList<T>());
	}

	public ListDataType(String name) {
		super(name, new ArrayList<T>());
	}

	public ListDataType(String name, List<T> value) {
		super(name, value);
	}

	@Override
	public void writeValue(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException {
		if(this.value.size() > 0) {
			
			writer.writeByte(this.value.get(0).getId());
			writer.writeInt(this.value.size());
			
			for(DataType<?> data : this.value) {
				if(data.getName().isEmpty()) throw new IOException("Cannot save a Data Type without a name");
				writer.writeUTF(data.getName().get(), charset);
				data.write(writer, registry, charset);
			}
			
			this.isModified.set(false);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		final byte dataTypeId = reader.readByte();
		final Class<T> dataTypeClass = (Class<T>) registry.getDataTypeByID(dataTypeId);
		final int size = reader.readInt();
		
		final ArrayList<T> content = new ArrayList<>();
		
		if(dataTypeClass == null) throw new IOException();
		
		this.isModified.set(false);
		
		T readDataType;
		for(int i = 0; i < size; i++) {
			try {
				final Constructor<T> constructor = dataTypeClass.getDeclaredConstructor();

				readDataType = constructor.newInstance();
			} catch (ReflectiveOperationException e) {
				throw new IOException();
			}

			readDataType.setName(reader.readUTF(charset));
			readDataType.read(reader, registry, charset);

			content.add(readDataType);
		}

		this.setValue(content);
	}

	public boolean add(T data) {
		synchronized (lock) {
			this.isModified.set(true);
			if(data.getName().isEmpty()) throw new MissingFormatArgumentException("Cannot add a DataType without a name");
			return this.value.add(data);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void add(T... datas) {
		synchronized (lock) {
			this.isModified.set(true);
			for(T data : datas) {
				if(data.getName().isEmpty()) throw new MissingFormatArgumentException("Cannot add a DataType without a name");
				if(data instanceof T) this.value.add(data);
			}
		}
	}
	
	public T get(int i) {
		synchronized (lock) {
			this.isModified.set(true);
			return this.value.get(i);
		}
	}

	public Optional<T> get(String name) {
		synchronized (lock) {
			this.isModified.set(true);
			return this.value.stream().filter(data -> data.getName().isPresent() && data.getName().get().equals(name)).findFirst();
		}
	}
	
	public boolean remove(T data) {
		synchronized (lock) {
			this.isModified.set(true);
			return this.value.remove(data);
		}
	}
	
	public boolean contains(T data) {
		return this.value.contains(data);
	}
	
	@Override
	public List<T> getValue() {
		return Collections.unmodifiableList(value);
	}
	
	@Override
	public byte getId() {
		return DataTypeEnum.LIST.getId();
	}
	
	@Override
	public Category getCategory() {
		return Category.CONTAINER;
	}
	
	@Override
	public String toString() {
		return ""+this.getValue();
	}

}
