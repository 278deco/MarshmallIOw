package marshmalliow.core.json.objects;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JSONObject extends ConcurrentHashMap<String, Object> implements JSONContainer {

	private static final long serialVersionUID = -497856876882492805L;
	
	private final AtomicBoolean contentModified = new AtomicBoolean(false);
	
    public JSONObject() {
    }

    /**
	 * Constructs a new, empty {@link JSONObject} with the specified initial capacity.
	 * 
	 * @param initialCapacity  The initial capacity. The implementation performs
	 *                         internal sizing to accommodate this many elements.
	 */
    public JSONObject(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a new {@link JSONObject} with the same mappings as the specified {@link Map}.
     * 
     * @param m The {@link Map} whose mappings are to be placed in this {@link JSONObject}.
     */
    public JSONObject(Map<String, Object> m) {
    	super(m.size());
        super.putAll(m);
    }

    /**
	 * Constructs a new, empty {@link JSONObject} with the specified initial capacity.
	 * 
	 * @param initialCapacity  The initial capacity. The implementation performs
	 *                         internal sizing to accommodate this many elements.
	 * @param loadFactor       The load factor threshold, used to control resizing.
	 */
    public JSONObject(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

	/**
	 * Constructs a new, empty {@link JSONObject} with the specified initial capacity, load factor,
	 * and concurrency level.
	 * 
	 * @param initialCapacity  The initial capacity. The implementation performs
	 *                         internal sizing to accommodate this many elements.
	 * @param loadFactor       The load factor threshold, used to control resizing.
	 * @param concurrencyLevel The estimated number of concurrently updating
	 *                         threads. The implementation performs internal sizing
	 *                         to try to accommodate this many threads.
	 */
    public JSONObject(int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }
	
    /**
     * Get the value of the key as a type of E.
     * @param <E> The type of the value returned.
     * @param key The key of the value in the JSONObject.
     * @param castType The type of the value returned.
     * @return The value of the key as a type of E.
     */
	public <E> E get(Object key, Class<E> castType) {
		try {
			return castType.cast(super.get(key));
		}catch(ClassCastException e) {
			return null;
		}
	}
	
	/**
	 * Get the value of the key as a type of E.
	 * 
	 * @param <E>          The type of the value returned.
	 * @param key          The key of the value in the JSONObject.
	 * @param defaultValue The default value if the key does not exist or the value
	 *                     is not the type of E.
	 * @param castType     The type of the value returned.
	 * @return The value of the key as a type of E.
	 */
	public <E> E getOrDefault(Object key, E defaultValue, Class<E> castType) {
		try {
			return castType.cast(super.get(key));
		}catch(ClassCastException e) {
			return defaultValue;
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public boolean replace(String key, Object oldValue, Object newValue) {
		final boolean result = super.replace(key, oldValue, newValue);
		if(result) this.contentModified.set(true);
		return result;
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Object replace(String key, Object value) {
		final Object replacedValue = super.replace(key, value);
		if(replacedValue != null) this.contentModified.set(true);
		return replacedValue;
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public void replaceAll(BiFunction<? super String, ? super Object, ? extends Object> function) {
		this.contentModified.set(true);
		super.replaceAll(function);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public boolean remove(Object key, Object value) {
		final boolean result = super.remove(key, value);
		if(result) this.contentModified.set(true);
		return result;
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Object remove(Object key) {
		final Object removedKey = super.remove(key);
		if(removedKey != null) this.contentModified.set(true);
		return removedKey;
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Object putIfAbsent(String key, Object value) {
		final Object previousKey = super.putIfAbsent(key, value);
		if(previousKey == null) this.contentModified.set(true);
		return previousKey;
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		this.contentModified.set(true);
		super.putAll(m);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Object put(String key, Object value) {
		final Object previousValue = super.put(key, value);
		if(previousValue == null || !previousValue.equals(value)) this.contentModified.set(true);
		return previousValue;
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
		this.contentModified.set(true);
		return super.merge(key, value, remappingFunction);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ? extends Object> remappingFunction) {
		this.contentModified.set(true);
		return super.computeIfPresent(key, remappingFunction);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Object computeIfAbsent(String key, Function<? super String, ? extends Object> mappingFunction) {
		this.contentModified.set(true);
		return super.computeIfAbsent(key, mappingFunction);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Object compute(String key, BiFunction<? super String, ? super Object, ? extends Object> remappingFunction) {
		this.contentModified.set(true);
		return super.compute(key, remappingFunction);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public void clear() {
		this.contentModified.set(true);
		super.clear();
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public String toString() {
		final Iterator<Entry<String, Object>> i = entrySet().iterator();
		if (!i.hasNext()) return "{}";

		final StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (;;) {
			final Entry<String, Object> e = i.next();
			final Object value = e.getValue();
			sb.append("\""+e.getKey()+"\"");
			sb.append(':');
			if (value == this) sb.append("this");
			else sb.append(value instanceof String ? "\"" + value + "\"" : value);
			
			if (!i.hasNext()) return sb.append('}').toString();
			sb.append(',').append(' ');
		}
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void setContentModified(boolean value) {
		this.contentModified.set(value);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public boolean isModified() {
		return this.contentModified.get();
	}
	
}
