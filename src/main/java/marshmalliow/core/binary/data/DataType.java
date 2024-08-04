package marshmalliow.core.binary.data;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;
import marshmalliow.core.io.BinaryReader;
import marshmalliow.core.io.BinaryWriter;

/**
 * Abstract class that represents a data type<br/>
 * The class is abstract and should be extended to create a new data type<br/>
 * @param <T> the type of the value stored in the data type
 * @author 278deco
 */
public abstract class DataType<T> implements RegisteredDataType {

	/**
	 * Enum that represents the category of the data type
	 */
	public static enum Category { PRIMITIVE, CONTAINER, ADVANCED }
	
	protected Optional<String> name;
	protected T value;
	protected final AtomicBoolean isModified = new AtomicBoolean(false);

	/**
	 * Constructor that initializes the data type with a name and a value
	 * @param name The name of the data type
	 * @param value The value contained
	 */
	public DataType(String name, T value) {
		Objects.requireNonNull(value);
		this.name = Optional.of(name);
		this.value = value;
		this.isModified.set(true);
	}
	
	/**
	 * Constructor that initializes the data type with a value and no name
	 * @param value The value contained
	 */
	public DataType(T value) {
		Objects.requireNonNull(value);
		this.name = Optional.empty();
		this.value = value;
		this.isModified.set(true);
	}

	/**
	 * Constructor that initializes the data type with no name and no value
	 */
	public DataType() {
		this.name = Optional.empty();
		this.value = null;
	}

	/**
	 * Given a {@link BinaryWriter}, a {@link DataTypeRegistry} and a {@link Charset}, write the data type to the writer
	 * @param writer The writer to write the data type to
	 * @param registry The registry that contains every {@link DataType} registered in the application
	 * @param charset The charset to use to write the data
	 * @throws IOException
	 */
	public void write(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException {
		this.isModified.set(false);
		writeValue(writer, registry, charset);
	}

	/**
	 * Given a {@link BinaryReader}, a {@link DataTypeRegistry} and a {@link Charset}, read the data type from the reader
	 * @param reader The reader to read the data type from
	 * @param registry The registry that contains every {@link DataType} registered in the application
	 * @param charset The charset to use to read the data
	 * @throws IOException
	 */
	public void read(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		this.isModified.set(false);
		readValue(reader, registry, charset);
	}
	
	protected abstract void writeValue(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException;

	protected abstract void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException;

	/**
	 * Set the name of the data type
	 * @param newName The new name of the data type
	 */
	public final void setName(String newName) {
		this.name = Optional.of(newName);
	}

	/**
	 * Set the value of the data type
	 * @param value The new value contained
	 */
	public final void setValue(T value) {
		this.isModified.set(true);
		this.value = value;
	}

	/**
	 * Get the id of the data type
	 * @return The id of the data type
	 */
	public abstract byte getId();
	
	/**
	 * Get the {@link Category} of the data type
	 * @return The category
	 */
	public abstract Category getCategory();

	/**
	 * Get the value of the data type
	 * @return The value contained
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Get the name of the data type
	 * @return The name of the data type
	 */
	public final Optional<String> getName() {
		return name;
	}
	
	/**
	 * Tell if the data stored has been modified and isn't saved<br/>
	 * The field is updated in the following situations :
	 * <ul>
	 * <li>By default when creating a new {@link DataType}, the object is defined as modified</li>
	 * <li>When calling {@link DataType#read(BinaryReader, DataTypeRegistry, Charset)}, the data is considered in pair with the saved data, therefore not modified</li>
	 * <li>When calling {@link DataType#write(BinaryWriter, DataTypeRegistry, Charset)} the data is considered as saved, therefore no longer modified</li>
	 * <li>Every methods that modify the data will set the field to true</li>
	 * </ul>
	 * <br/>
	 * @return the value of modified field
	 */
	public boolean isModified() {
		return this.isModified.get();
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
