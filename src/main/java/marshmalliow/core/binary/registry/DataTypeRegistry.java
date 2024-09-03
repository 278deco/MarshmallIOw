package marshmalliow.core.binary.registry;

import java.util.HashMap;
import java.util.Map;

import marshmalliow.core.binary.data.DataType;
import marshmalliow.core.binary.data.RegisteredDataType;
import marshmalliow.core.exceptions.DatatypeRegistryException;
import marshmalliow.core.io.BinaryReader;
import marshmalliow.core.io.BinaryWriter;

/**
 * This class is used to register DataTypes for the MarshmallIOw binary format.
 * <p>
 * Only data types that are registered here can be read and written when 
 * provided to the {@link BinaryReader} and {@link BinaryWriter}.
 * If a data type is not registered and present in the binary data,
 * the reader/writer will throw an exception or the data could be corrupted.
 * <p>
 * This class is immutable and can only be created using the builder, 
 * accessible via the static method {@link #builder()}.
 * @author 278deco
 * @version 1.0.0
 * @since 0.1.0
 * @see DataType
 * @see RegisteredDataType
 * @see DataTypeEnum
 */
public class DataTypeRegistry {

	/**
	 * A map that contains all registered DataTypes with their ID.
	 */
	private final Map<Byte, Class<? extends RegisteredDataType>> registry;

	private DataTypeRegistry(DataTypeRegistry.Builder builder) {
		this.registry = builder.registry;
	}

	/**
	 * Returns the {@link DataType} class for the given ID searched in the registry.
	 *
	 * @param id the ID of the data type
	 * @return the {@link DataType} class for the given ID or null if not found
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends DataType<?>> getDataTypeByID(byte id) {
		final Class<? extends RegisteredDataType> result = this.registry.get(id);
		
		return result != null && result.isInstance(DataType.class) ? (Class<? extends DataType<?>>) result : null;
	}

	/**
	 * Create a new {@link DataTypeRegistry} instance using the builder.
	 * @return a new {@link DataTypeRegistry.Builder} instance
	 */
	public static DataTypeRegistry.Builder builder() {
		return new DataTypeRegistry.Builder();
	}

	/**
	 * The builder class for the {@link DataTypeRegistry}.
	 * <p>
	 * The builder is used to create a new {@link DataTypeRegistry} instance.
	 * It provides methods to register data types and build the registry.
	 * <p>
	 * @author 278deco
	 * @version 1.0.0
	 * @since 0.1.0
	 */
	public static final class Builder {

		private final Map<Byte, Class<? extends RegisteredDataType>> registry = new HashMap<>();

		private Builder() { }

		/**
		 * Register a data type with the given ID.
		 * 
		 * @param id the ID of the data type to register
		 * @param cls the class of the data type to register
		 * @throws DatatypeRegistryException If the class is not a subclass of {@link DataType} or the ID is 0 or already registered
		 */
		public void register(byte id, Class<? extends RegisteredDataType> cls) throws DatatypeRegistryException {
			if(!DataType.class.isAssignableFrom(cls) || (id == 0)) throw new DatatypeRegistryException("");
			if(this.registry.containsKey(id)) throw new DatatypeRegistryException();

			if(this.registry.containsValue(cls)) {
				Byte currentId = this.registry.entrySet().stream().filter(entry -> entry.getValue().equals(cls)).map(Map.Entry::getKey).findFirst().orElse((byte)0);

				throw new DatatypeRegistryException("currID "+currentId);
			}

			this.registry.put(id, cls);
		}

		/**
		 * Register all data types in the given map.
		 * <p>
		 * This method will try to register all data types in the given map. 
		 * If one or more data types cannot be registered, it will throw a {@link DatatypeRegistryException} 
		 * with the first exception that occurred but still try to register the remaining data types.
		 * 
		 * @param map the map containing the data types to register
		 * @throws DatatypeRegistryException If one or more data types cannot be registered
		 */
		public void registerAll(Map<Byte, Class<? extends DataType<?>>> map) throws DatatypeRegistryException {
			DatatypeRegistryException exception = null;
			for(Map.Entry<Byte, Class<? extends DataType<?>>> entry : map.entrySet()) {
				try {
					this.register(entry.getKey(), entry.getValue());
				}catch(DatatypeRegistryException e) {
					exception = e;
				}
			}

			if(exception != null) throw exception;
		}

		/**
		 * Remove a data type from the registry.
		 * 
		 * @param id the ID of the data type to remove
		 * @return true if the data type was removed, false
		 */
		public boolean remove(byte id) {
			return this.registry.remove(id) != null;
		}

		/**
		 * Remove a data type from the registry.
		 * 
		 * @param id the ID of the data type to remove
		 * @param cls the class of the data type to remove
		 * @return true if the data type was removed, false
		 */
		public boolean remove(byte id, Class<? extends DataType<?>> cls) {
			return this.registry.remove(id, cls);
		}

		/**
		 * Build the {@link DataTypeRegistry} instance.
		 * 
		 * @return a new {@link DataTypeRegistry} instance
		 */
		public DataTypeRegistry build() {
			return new DataTypeRegistry(this);
		}
	}
}
