package marshmalliow.core.json.objects;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JSONObject implements JSONContainer {

	private final ConcurrentHashMap<String, Object> map;
	private final AtomicBoolean contentModified = new AtomicBoolean(false);

	
    public JSONObject() {
    	this.map = new ConcurrentHashMap<>();
    }

    /**
	 * Constructs a new, empty {@link JSONObject} with the specified initial capacity.
	 * 
	 * @param initialCapacity  The initial capacity. The implementation performs
	 *                         internal sizing to accommodate this many elements.
	 */
    public JSONObject(int initialCapacity) {
        this.map = new ConcurrentHashMap<>(initialCapacity);
    }

    /**
     * Constructs a new {@link JSONObject} with the same mappings as the specified {@link Map}.
     * 
     * @param m The {@link Map} whose mappings are to be placed in this {@link JSONObject}.
     */
    public JSONObject(Map<String, Object> m) {
    	this.map = new ConcurrentHashMap<>(m);
    }

    /**
	 * Constructs a new, empty {@link JSONObject} with the specified initial capacity.
	 * 
	 * @param initialCapacity  The initial capacity. The implementation performs
	 *                         internal sizing to accommodate this many elements.
	 * @param loadFactor       The load factor threshold, used to control resizing.
	 */
    public JSONObject(int initialCapacity, float loadFactor) {
        this.map = new ConcurrentHashMap<>(initialCapacity, loadFactor);
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
        this.map = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
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
			return castType.cast(map.get(key));
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
			return castType.cast(map.get(key));
		}catch(ClassCastException e) {
			return defaultValue;
		}
	}
	
	public JSONObject getJSONObject(Object key) {
		final Object value = map.get(key);
		if(value == null) return null;
		if(value instanceof JSONObject) return (JSONObject) value;
		return null;
	}
	
	public JSONArray getJSONArray(Object key) {
		final Object value = map.get(key);
		if(value == null) return null;
		if(value instanceof JSONArray) return (JSONArray) value;
		return null;
	}
	
	public String getString(Object key) {
		final Object value = map.get(key);
		if(value == null) return null;
		if(value instanceof String) return (String) value;
		return null;
	}
	
	public String getString(Object key, String defaultValue) {
		final Object value = map.get(key);
		if(value == null) return defaultValue;
		if(value instanceof String) return (String) value;
		return defaultValue;
	}
	
	public Integer getInt(Object key) {
		final Object value = map.get(key);
		if(value == null) return null;
		if(value instanceof Integer) return (Integer) value;
		if(value instanceof Number) return ((Number) value).intValue();
		return null;
	}
	
	public int getInt(Object key, int defaultValue) {
		final Object value = map.get(key);
		if(value == null) return defaultValue;
		if(value instanceof Integer) return (Integer) value;
		if(value instanceof Number) return ((Number) value).intValue();
		return defaultValue;
	}
	
	public Long getLong(Object key) {
		final Object value = map.get(key);
		if(value == null) return null;
		if(value instanceof Long) return (Long) value;
		if(value instanceof Number) return ((Number) value).longValue();
		return null;
	}
	
	public Long getLong(Object key, long defaultValue) {
		final Object value = map.get(key);
		if(value == null) return defaultValue;
		if(value instanceof Long) return (Long) value;
		if(value instanceof Number) return ((Number) value).longValue();
		return defaultValue;
	}
	
	public Float getFloat(Object key) {
		final Object value = map.get(key);
		if(value == null) return null;
		if(value instanceof Float) return (Float) value;
		if(value instanceof Number) return ((Number) value).floatValue();
		return null;
	}
	
	public Float getFloat(Object key, float defaultValue) {
		final Object value = map.get(key);
		if(value == null) return defaultValue;
		if(value instanceof Float) return (Float) value;
		if(value instanceof Number) return ((Number) value).floatValue();
		return defaultValue;
	}
	
	public Double getDouble(Object key) {
		final Object value = map.get(key);
		if(value == null) return null;
		if(value instanceof Double) return (Double) value;
		if(value instanceof Number) return ((Number) value).doubleValue();
		return null;
	}
	
	public Double getDouble(Object key, double defaultValue) {
		final Object value = map.get(key);
		if(value == null) return defaultValue;
		if(value instanceof Double) return (Double) value;
		if(value instanceof Number) return ((Number) value).doubleValue();
		return defaultValue;
	}
	
	public Boolean getBoolean(Object key) {
		final Object value = map.get(key);
		if(value == null) return null;
		if(value instanceof Boolean) return (Boolean) value;
		if(value instanceof Number) return ((Number) value).intValue() != 0;
		return null;
	}
	
	public Boolean getBoolean(Object key, boolean defaultValue) {
		final Object value = map.get(key);
		if(value == null) return defaultValue;
		if(value instanceof Boolean) return (Boolean) value;
		if(value instanceof Number) return ((Number) value).intValue() != 0;
		return defaultValue;
	}
	
	public int size() {
		return map.size();
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}
	
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	/**
	 * Returns a set view of the keys contained in this map.<br/>
	 * This set is immutable an not backed by the map, so changes to the map are not reflected in the set,
	 * and vice versa.
	 * 
	 * @return a immutable view of the pairs of keys and values in this map.
	 */
	public Map<String, Object> snapshot() {
		return Map.copyOf(map);
	}
		
	/**
     * {@inheritDoc}
     */
	public boolean replace(String key, Object oldValue, Object newValue) {
		final boolean result = map.replace(key, oldValue, newValue);
		if(result) this.contentModified.set(true);
		return result;
	}
	
	/**
     * {@inheritDoc}
     */
	public Object replace(String key, Object value) {
		final Object replacedValue = map.replace(key, value);
		if(replacedValue != null) this.contentModified.set(true);
		return replacedValue;
	}
	
	/**
     * {@inheritDoc}
     */
	public void replaceAll(BiFunction<? super String, ? super Object, ? extends Object> function) {
		this.contentModified.set(true);
		map.replaceAll(function);
	}
	
	/**
     * {@inheritDoc}
     */
	public boolean remove(Object key, Object value) {
		final boolean result = map.remove(key, value);
		if(result) this.contentModified.set(true);
		return result;
	}
	
	/**
     * {@inheritDoc}
     */
	public Object remove(Object key) {
		final Object removedKey = map.remove(key);
		if(removedKey != null) this.contentModified.set(true);
		return removedKey;
	}
	
	/**
     * {@inheritDoc}
     */
	public Object putIfAbsent(String key, Object value) {
		final Object previousKey = map.putIfAbsent(key, value);
		if(previousKey == null) this.contentModified.set(true);
		return previousKey;
	}
	
	/**
     * {@inheritDoc}
     */
	public void putAll(Map<? extends String, ? extends Object> m) {
		if(m == null || m.isEmpty()) return; // No need to modify if the map is empty
		this.contentModified.set(true);
		map.putAll(m);
	}
	
	/**
     * {@inheritDoc}
     */
	public Object put(String key, Object value) {
		final Object previousValue = map.put(key, value);
		if(previousValue == null || !previousValue.equals(value)) this.contentModified.set(true);
		return previousValue;
	}
	
	/**
     * {@inheritDoc}
     */
	public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
		this.contentModified.set(true);
		return map.merge(key, value, remappingFunction);
	}
	
	/**
     * {@inheritDoc}
     */
	public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ? extends Object> remappingFunction) {
		this.contentModified.set(true);
		return map.computeIfPresent(key, remappingFunction);
	}
	
	/**
     * {@inheritDoc}
     */
	public Object computeIfAbsent(String key, Function<? super String, ? extends Object> mappingFunction) {
		this.contentModified.set(true);
		return map.computeIfAbsent(key, mappingFunction);
	}
	
	/**
     * {@inheritDoc}
     */
	public Object compute(String key, BiFunction<? super String, ? super Object, ? extends Object> remappingFunction) {
		this.contentModified.set(true);
		return map.compute(key, remappingFunction);
	}
	
	/**
     * {@inheritDoc}
     */
	public void clear() {
		this.contentModified.set(true);
		map.clear();
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public String toString() {
		final Iterator<Entry<String, Object>> i = map.entrySet().iterator();
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
	public void resetModified() {
		this.contentModified.set(false);		
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public boolean isModified() {
		return this.contentModified.get();
	}
	
}
