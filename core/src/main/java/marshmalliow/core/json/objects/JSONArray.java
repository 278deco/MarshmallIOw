package marshmalliow.core.json.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class JSONArray implements JSONContainer {

	private final ArrayList<Object> list;
	private final AtomicBoolean contentModified = new AtomicBoolean(false);

	protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	/**
	 * Create a new {@link JSONArray}.<br/>
	 * A new mutex {@link ReentrantReadWriteLock} is created to synchronize access.
	 */
	public JSONArray() {
		this.list = new ArrayList<>();
	}

	/**
	 * Create a new {@link JSONArray} with a given initial capacity.<br/>
	 * A new mutex {@link ReentrantReadWriteLock} is created to synchronize access.
	 * 
	 * @param initialCapacity The initial capacity of the list
	 */
	public JSONArray(int initialCapacity) {
		this.list = new ArrayList<>(initialCapacity);
	}

	/**
	 * Create a new {@link JSONArray} with a given collection.<br/>
	 * A new mutex {@link ReentrantReadWriteLock} is created to synchronize access.
	 *
	 * @param c The collection to initialize the list
	 */
	public JSONArray(Collection<? extends Object> c) {
		this.list = new ArrayList<>(c);
	}

	/**
	 * Add an element at the given index to the list. The
	 * <code>contentModified</code> flag is set to true.<br/>
	 * The list is synchronized using the mutex object.
	 * 
	 * @param index   The index to add the element
	 * @param element The element to add
	 */
	public void add(int index, Object element) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			this.list.add(index, element);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Add an element to the list. The <code>contentModified</code> flag is set to
	 * true.<br/>
	 * The list is synchronized using the mutex object.
	 *
	 * @param e The element to add
	 */
	public boolean add(Object e) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			return list.add(e);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Add all elements of the given collection to the list. The
	 * <code>contentModified</code> flag is set to true.<br/>
	 * The list is synchronized using the mutex object.
	 * 
	 * @param c The collection
	 */
	public boolean addAll(Collection<? extends Object> c) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			return list.addAll(c);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Add all elements of the given collection to the list at the specific index.
	 * The <code>contentModified</code> flag is set to true.<br/>
	 * The list is synchronized using the mutex object.
	 * 
	 * @param index The index where to add the elements
	 * @param c     The collection
	 */
	public boolean addAll(int index, Collection<? extends Object> c) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			return list.addAll(index, c);
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * Get the element at the given index. The list is synchronized using the mutex
	 * object.
	 * 
	 * @param index The index of the element
	 * @return The element at the given index
	 */
	public Object get(int index) {
		try {
			lock.readLock().lock();
			return list.get(index);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Get the element at the given index. The list is synchronized using the mutex
	 * object.
	 * 
	 * @param <E>   The expected type of the element
	 * @param index The index of the element
	 * @param clazz The class of the expected type
	 * @return The element at the given index
	 */
	public <E> E  get(int index, Class<E> clazz) {
		try {
			lock.readLock().lock();
			return clazz.cast(list.get(index));
		} catch (ClassCastException ex) {
			return null;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public <E> E getOrDefault(int index, Class<E> clazz, E defaultValue) {
		try {
			lock.readLock().lock();
			return clazz.cast(list.get(index));
		} catch (ClassCastException | IndexOutOfBoundsException ex) {
			return defaultValue;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public JSONObject getJSONObject(int index, Object key) {
		final Object value = list.get(index);
		if(value == null) return null;
		if(value instanceof JSONObject) return (JSONObject) value;
		return null;
	}
	
	public JSONArray getJSONArray(int index, Object key) {
		final Object value = list.get(index);
		if(value == null) return null;
		if(value instanceof JSONArray) return (JSONArray) value;
		return null;
	}
	
	public String getString(int index, Object key) {
		final Object value = list.get(index);
		if(value == null) return null;
		if(value instanceof String) return (String) value;
		return null;
	}
	
	public String getString(int index, Object key, String defaultValue) {
		final Object value = list.get(index);
		if(value == null) return defaultValue;
		if(value instanceof String) return (String) value;
		return defaultValue;
	}
	
	public Integer getInt(int index, Object key) {
		final Object value = list.get(index);
		if(value == null) return null;
		if(value instanceof Integer) return (Integer) value;
		if(value instanceof Number) return ((Number) value).intValue();
		return null;
	}
	
	public int getInt(int index, Object key, int defaultValue) {
		final Object value = list.get(index);
		if(value == null) return defaultValue;
		if(value instanceof Integer) return (Integer) value;
		if(value instanceof Number) return ((Number) value).intValue();
		return defaultValue;
	}
	
	public Long getLong(int index, Object key) {
		final Object value = list.get(index);
		if(value == null) return null;
		if(value instanceof Long) return (Long) value;
		if(value instanceof Number) return ((Number) value).longValue();
		return null;
	}
	
	public Long getLong(int index, Object key, long defaultValue) {
		final Object value = list.get(index);
		if(value == null) return defaultValue;
		if(value instanceof Long) return (Long) value;
		if(value instanceof Number) return ((Number) value).longValue();
		return defaultValue;
	}
	
	public Float getFloat(int index, Object key) {
		final Object value = list.get(index);
		if(value == null) return null;
		if(value instanceof Float) return (Float) value;
		if(value instanceof Number) return ((Number) value).floatValue();
		return null;
	}
	
	public Float getFloat(int index, Object key, float defaultValue) {
		final Object value = list.get(index);
		if(value == null) return defaultValue;
		if(value instanceof Float) return (Float) value;
		if(value instanceof Number) return ((Number) value).floatValue();
		return defaultValue;
	}
	
	public Double getDouble(int index, Object key) {
		final Object value = list.get(index);
		if(value == null) return null;
		if(value instanceof Double) return (Double) value;
		if(value instanceof Number) return ((Number) value).doubleValue();
		return null;
	}
	
	public Double getDouble(int index, Object key, double defaultValue) {
		final Object value = list.get(index);
		if(value == null) return defaultValue;
		if(value instanceof Double) return (Double) value;
		if(value instanceof Number) return ((Number) value).doubleValue();
		return defaultValue;
	}
	
	public Boolean getBoolean(int index, Object key) {
		final Object value = list.get(index);
		if(value == null) return null;
		if(value instanceof Boolean) return (Boolean) value;
		if(value instanceof Number) return ((Number) value).intValue() != 0;
		return null;
	}
	
	public Boolean getBoolean(int index, Object key, boolean defaultValue) {
		final Object value = list.get(index);
		if(value == null) return defaultValue;
		if(value instanceof Boolean) return (Boolean) value;
		if(value instanceof Number) return ((Number) value).intValue() != 0;
		return defaultValue;
	}

	/**
	 * Increases the capacity of this ArrayList instance, if necessary, to ensure
	 * that it can hold at least the number of elements specified by the minimum
	 * capacity argument.
	 *
	 * @param minCapacity the desired minimum capacity
	 */
	public void ensureCapacity(int minCapacity) {
		try {
			lock.writeLock().lock();
			list.ensureCapacity(minCapacity);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public JSONArray subList(int fromIndex, int toIndex) {
		try {
			lock.readLock().lock();
			return new JSONArray(new ArrayList<>(list.subList(fromIndex, toIndex)));
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void replaceAll(UnaryOperator<Object> operator) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			list.replaceAll(operator);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void sort(Comparator<? super Object> c) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			list.sort(c);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object set(int index, Object element) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			return list.set(index, element);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void trimToSize() {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			list.trimToSize();
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object remove(int index) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			return list.remove(index);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean remove(Object o) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			return list.remove(o);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean retainAll(Collection<?> c) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			return list.retainAll(c);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeAll(Collection<?> c) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			return list.removeAll(c);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeIf(Predicate<? super Object> filter) {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			return list.removeIf(filter);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void forEach(Consumer<? super Object> action) {
		try {
			lock.readLock().lock();
			list.forEach(action);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		try {
			lock.writeLock().lock();
			this.contentModified.set(true);
			list.clear();
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int indexOf(Object o) {
		try {
			lock.readLock().lock();
			return list.indexOf(o);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int lastIndexOf(Object o) {
		try {
			lock.readLock().lock();
			return list.lastIndexOf(o);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		try {
			lock.readLock().lock();
			return list.size();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		try {
			lock.readLock().lock();
			return list.isEmpty();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(Object o) {
		try {
			lock.readLock().lock();
			return list.contains(o);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */

	public boolean containsAll(Collection<?> c) {
		try {
			lock.readLock().lock();
			return list.containsAll(c);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] toArray() {
		try {
			lock.readLock().lock();
			return list.toArray();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T[] toArray(T[] a) {
		try {
			lock.readLock().lock();
			return list.toArray(a);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T[] toArray(IntFunction<T[]> f) {
		try {
			lock.readLock().lock();
			return list.toArray(f);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Create a new {@link Iterator} for the list. The list is copied to avoid
	 * concurrent modification exceptions.<br/>
	 * The list is synchronized using the mutex object.
	 * 
	 * @return A new {@link Iterator} for the list
	 */
	public Iterator<Object> iterator() {
		try {
			lock.readLock().lock();
			return new ArrayList<>(list).iterator();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public Iterator<Object> liveInterator() {
		try {
			lock.readLock().lock();
			return list.iterator();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public ListIterator<Object> listIterator() {
		try {
			lock.readLock().lock();
			return new ArrayList<>(list).listIterator();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public ListIterator<Object> liveListIterator() {
		try {
			lock.readLock().lock();
			return list.listIterator();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public ListIterator<Object> listIterator(int index) {
		try {
			lock.readLock().lock();
			return new ArrayList<>(list).listIterator(index);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public ListIterator<Object> liveListIterator(int index) {
		try {
			lock.readLock().lock();
			return list.listIterator(index);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
        return list.toString();
    }
	
	@Override
	public int hashCode() {
		return list.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		return (obj instanceof JSONArray) && this.list.equals(((JSONArray) obj).list);
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
