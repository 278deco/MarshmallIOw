package marshmalliow.core.binary.registry;

import java.util.HashMap;
import java.util.Map;

import marshmalliow.core.binary.data.types.DataType;
import marshmalliow.core.binary.data.types.RegisteredDataType;
import marshmalliow.core.binary.exceptions.DatatypeRegistryException;

public class DataTypeRegistry {

	private final Map<Byte, Class<? extends RegisteredDataType>> registry;

	private DataTypeRegistry(DataTypeRegistry.Builder builder) {
		this.registry = builder.registry;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends DataType<?>> getDataTypeByID(byte id) {
		final Class<? extends RegisteredDataType> result = this.registry.get(id);
		
		return result != null && result.isInstance(DataType.class) ? (Class<? extends DataType<?>>) result : null;
	}

	public static DataTypeRegistry.Builder builder() {
		return new DataTypeRegistry.Builder();
	}

	public static final class Builder {

		private final Map<Byte, Class<? extends RegisteredDataType>> registry = new HashMap<>();

		private Builder() { }

		public void register(byte id, Class<? extends RegisteredDataType> cls) throws DatatypeRegistryException {
			if(!DataType.class.isAssignableFrom(cls) || (id == 0)) throw new DatatypeRegistryException("");
			if(this.registry.containsKey(id)) throw new DatatypeRegistryException();

			if(this.registry.containsValue(cls)) {
				Byte currentId = this.registry.entrySet().stream().filter(entry -> entry.getValue().equals(cls)).map(Map.Entry::getKey).findFirst().orElse((byte)0);

				throw new DatatypeRegistryException("currID "+currentId);
			}

			this.registry.put(id, cls);
		}

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


		public boolean remove(byte id) {
			return this.registry.remove(id) != null;
		}

		public boolean remove(byte id, Class<? extends DataType<?>> cls) {
			return this.registry.remove(id, cls);
		}

		public DataTypeRegistry build() {
			return new DataTypeRegistry(this);
		}
	}
}
