package marshmalliow.core.binary.data.container;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingFormatArgumentException;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import marshmalliow.core.binary.data.DataType;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;
import marshmalliow.core.io.BinaryReader;
import marshmalliow.core.io.BinaryWriter;

/**
 * Object data type corresponding to the format specification.
 * @version	1.0.0
 * @author 278deco
 */
public class ObjectDataType extends DataType<Map<String, DataType<?>>> {

	private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock(); 
	
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
		try {
			LOCK.writeLock().lock();
			for(Map.Entry<String, DataType<?>> entry : this.value.entrySet()) {
				writer.writeByte(entry.getValue().getId());
				if(entry.getValue().getName().isEmpty()) throw new IOException("Cannot save a Data Type without a name");
				writer.writeUTF(entry.getValue().getName().get(), charset);

				entry.getValue().write(writer, registry, charset);
			}

			writer.writeByte((byte)0);
			
			this.isModified.set(false);
		} finally {
			LOCK.writeLock().unlock();
		}
	}

	@Override
	public void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		try {
			LOCK.writeLock().lock();
			final Map<String, DataType<?>> data = new LinkedHashMap<>();
	
			this.isModified.set(false);
			
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
		} finally {
			LOCK.writeLock().unlock();
		}
	}
	
	public boolean add(DataType<?> data) {
		try {
			LOCK.writeLock().lock();
			this.isModified.set(true);
			if(data.getName().isEmpty()) throw new MissingFormatArgumentException("Cannot add a DataType without a name");
			return this.value.put(data.getName().get(), data) == null;
		} finally {
			LOCK.writeLock().unlock();
		}
	}
	
	public void add(DataType<?>... datas) {
		try {
			LOCK.writeLock().lock();
			this.isModified.set(true);
			for(DataType<?> data : datas) {
				if(data.getName().isEmpty()) throw new MissingFormatArgumentException("Cannot add a DataType without a name");
				this.value.put(data.getName().get(), data);
			}
		} finally {
			LOCK.writeLock().unlock();
		}
	}
	
	public DataType<?> get(String name) {
		try {
			LOCK.readLock().lock();
			this.isModified.set(true);
			return this.value.get(name);
		}finally {
			LOCK.readLock().unlock();
		}
	}
	
	public boolean remove(DataType<?> data) {
		try {
			LOCK.writeLock().lock();
			this.isModified.set(true);
			return this.value.remove(data.getName(), data);
		} finally {
			LOCK.writeLock().unlock();
		}
	}
	
	public boolean remove(String name) {
		try {
			LOCK.writeLock().lock();
			this.isModified.set(true);
			return this.value.remove(name) != null;
		} finally {
			LOCK.writeLock().unlock();
		}
	}
	
	public boolean contains(DataType<?> data) {
		try {
			LOCK.readLock().lock();
			return data.getName().isPresent() && (this.value.containsKey(data.getName().get()) && this.value.containsValue(data));
		} finally {
			LOCK.readLock().unlock();
		}
	}

	public Set<Entry<String, DataType<?>>> entrySet() {
		try {
			LOCK.writeLock().lock();
			return this.value.entrySet();
		} finally {
			LOCK.writeLock().unlock();
		}
	}
	
	public void unlockEntrySet() {
		LOCK.writeLock().unlock();
	}
	
	public int getSize() {
		try {
			LOCK.readLock().lock();
			return this.value.size();
		} finally {
			LOCK.readLock().unlock();
		}
	}
	
	@Override
	public Map<String, DataType<?>> getValue() {
		try {
			LOCK.readLock().lock();
			return Collections.unmodifiableMap(this.value);
		} finally {
			LOCK.readLock().unlock();
		}
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
