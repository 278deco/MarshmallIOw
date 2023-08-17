package marshmalliow.core.binary.data.types;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import marshmalliow.core.binary.io.BinaryReader;
import marshmalliow.core.binary.io.BinaryWriter;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;

public abstract class DataType<T> {

	public static enum Category { PRIMITIVE, CONTAINER, ADVANCED }
	
	protected Optional<String> name;
	protected T value;
	protected boolean isModified;

	public DataType(String name, T value) {
		Objects.requireNonNull(value);
		this.name = Optional.of(name);
		this.value = value;
		this.isModified = true;
	}
	
	public DataType(T value) {
		Objects.requireNonNull(value);
		this.name = Optional.empty();
		this.value = value;
		this.isModified = true;
	}

	public DataType() {
		this.name = Optional.empty();
		this.value = null;
	}

	public void write(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException {
		this.isModified = false;
		writeValue(writer, registry, charset);
	}

	public void read(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		this.isModified = false;
		readValue(reader, registry, charset);
	}
	
	protected abstract void writeValue(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException;

	protected abstract void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException;

	public final void setName(String newName) {
		this.name = Optional.of(newName);
	}

	public final void setValue(T value) {
		this.isModified = true;
		this.value = value;
	}

	public abstract byte getId();
	
	public abstract Category getCategory();

	public T getValue() {
		return value;
	}

	public final Optional<String> getName() {
		return name;
	}
	
	/**
	 * Tell if the data stored has been modified and isn't saved</br>
	 * The field is updated in the following situations :
	 * <ul>
	 * <li>By default when creating a new {@link DataType}, the object is defined as modified</li>
	 * <li>When calling {@link DataType#read(BinaryReader, DataTypeRegistry)}, the data is considered in pair with the saved data, therefore not modified</li>
	 * <li>When calling {@link DataType#write(BinaryWriter, DataTypeRegistry)} the data is considered as saved, therefore no longer modified</li>
	 * <li>Every methods that modify the data will set the field to true</li>
	 * </ul>
	 * </br>
	 * @return the value of modified field
	 */
	public boolean isModified() {
		return isModified;
	}

	@Override
	public String toString() {
		return getName().isPresent() ?
				getClass().getSimpleName()+"[name:"+getName().get()+", value:"+getValue()+"]" :
				getClass().getSimpleName()+"[value:"+getValue()+"]"	;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		return obj instanceof DataType<?> && areEquals((DataType<?>)obj);
	}

	private final boolean areEquals(DataType<?> obj) {
		return (this.name.isPresent() == obj.name.isPresent()) &&
				(this.name.isPresent() ? this.name.get().equals(obj.name.get()) : true) &&
				this.value == obj.value;
	}
}
