package marshmalliow.core.json.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class JSONArray extends ArrayList<Object> implements JSONContainer {

	private static final long serialVersionUID = 3593877469226039660L;
	
	private final AtomicBoolean contentModified = new AtomicBoolean(false);
	protected final Object mutex;
	
	/**
	 * Create a new {@link JSONArray} with a mutex object to synchronize access
	 * @param mutex The mutex object to synchronize access
	 */
	public JSONArray(Object mutex) {
		super();
		this.mutex = mutex;
	}
	
	/**
	 * Create a new {@link JSONArray} with a mutex object to synchronize.<br/>
	 * A given initial capacity is used to optimize the performance.
	 * @param initialCapacity The initial capacity of the list
	 * @param mutex The mutex object to synchronize access
	 */
	public JSONArray(int initialCapacity, Object mutex) {
		super(initialCapacity);
		this.mutex = mutex;
	}
	
	/**
	 * Create a new {@link JSONArray} with a mutex object to synchronize.<br/>
	 * The given collection is used to initialize the new list.
	 * @param c The collection to initialize the list
	 * @param mutex The mutex object to synchronize access
	 */
	public JSONArray(Collection<? extends Object> c, Object mutex) {
		super(c);
		this.mutex = mutex;
	}
	
	/**
	 * Create a new {@link JSONArray}.<br/>
	 * A new mutex {@link Object} is created to synchronize access.
	 */
	public JSONArray() {
		this(new Object());
	}
	
	/**
     * Create a new {@link JSONArray} with a given initial capacity.<br/>
     * A new mutex {@link Object} is created to synchronize access.
     * @param initialCapacity The initial capacity of the list
     */
	public JSONArray(int initialCapacity) {
		this(initialCapacity, new Object());
	}
	
	/**
	 * Create a new {@link JSONArray} with a given collection.<br/>
	 * A new mutex {@link Object} is created to synchronize access.
	 * 
	 * @param c The collection to initialize the list
	 */
	public JSONArray(Collection<? extends Object> c) {
		this(c, new Object());
	}
	
	
	/**
	 * Add an element at the given index to the list. The <code>contentModified</code> flag is set to true.<br/>
	 * The list is synchronized using the mutex object.
	 * @param index The index to add the element
	 * @param element The element to add
	 */
	@Override
	public void add(int index, Object element) {
		synchronized (mutex) {
			this.contentModified.set(true);
			super.add(index, element);
		}
	}
	
	/**
	 * Add an element to the list. The <code>contentModified</code> flag is set to
	 * true.<br/>
	 * The list is synchronized using the mutex object.
	 * 
	 * @param e The element to add
	 */
	@Override
	public boolean add(Object e) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.add(e);
		}
	}
	
	/**
     * Add all elements of the given collection to the list. The <code>contentModified</code> flag is set to true.<br/>
     * The list is synchronized using the mutex object.
     * @param c The collection
     */
	@Override
	public boolean addAll(Collection<? extends Object> c) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.addAll(c);
		}
	}
	
	/**
     * Add all elements of the given collection to the list at the specific index.
     *  The <code>contentModified</code> flag is set to true.<br/>
     * The list is synchronized using the mutex object.
     * @param index The index where to add the elements
     * @param c The collection
     */
	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.addAll(index, c);
		}
	}
	
	/**
	 * Get the element at the given index. The list is synchronized using the mutex object.
	 * @param index The index of the element
	 * @return The element at the given index
	 */
	@Override
	public Object get(int index) {
		synchronized (mutex) {
			return super.get(index);
		}
	}
	
	/**
	 * Increases the capacity of this ArrayList instance, if necessary, 
	 * to ensure that it can hold at least the number of elements 
	 * specified by the minimum capacity argument.
	 * 
	 * @param minCapacity the desired minimum capacity
     */
	@Override
	public void ensureCapacity(int minCapacity) {
		synchronized (mutex) {
			this.contentModified.set(true);
			super.ensureCapacity(minCapacity);
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		synchronized (mutex) {
			this.contentModified.set(true);
			super.removeRange(fromIndex, toIndex);
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		synchronized (mutex) {
			return new JSONArray(super.subList(fromIndex, toIndex), mutex);
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public void replaceAll(UnaryOperator<Object> operator) {
		synchronized (mutex) {
			this.contentModified.set(true);
			super.replaceAll(operator);
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public void sort(Comparator<? super Object> c) {
		synchronized (mutex) {
			this.contentModified.set(true);
			super.sort(c);
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Object set(int index, Object element) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.set(index, element);
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public void trimToSize() {
		synchronized (mutex) {
			this.contentModified.set(true);
			super.trimToSize();
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Object remove(int index) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.remove(index);
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public boolean remove(Object o) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.remove(o);
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public boolean retainAll(Collection<?> c) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.retainAll(c); 
			}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public boolean removeAll(Collection<?> c) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.removeAll(c);
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public boolean removeIf(Predicate<? super Object> filter) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.removeIf(filter);
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public void forEach(Consumer<? super Object> action) {
		synchronized (mutex) {
			super.forEach(action);
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public void clear() {
		synchronized (mutex) {
			this.contentModified.set(true);
			super.clear();
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public int indexOf(Object o) {
		synchronized (mutex) { return super.indexOf(o); }
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public int lastIndexOf(Object o) {
		synchronized (mutex) { return super.lastIndexOf(o); }
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public int size() {
		synchronized (mutex) {
			return super.size();
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public boolean isEmpty() {
        synchronized (mutex) {return super.isEmpty();}
    }
	
	/**
     * {@inheritDoc}
     */
	@Override
    public boolean contains(Object o) {
        synchronized (mutex) {return super.contains(o);}
    }
	
	/**
     * {@inheritDoc}
     */
	@Override
	public boolean containsAll(Collection<?> c) {
		synchronized (mutex) { return super.containsAll(c); }
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
    public Object[] toArray() {
        synchronized (mutex) {return super.toArray();}
    }
	
	/**
     * {@inheritDoc}
     */
	@Override
    public <T> T[] toArray(T[] a) {
        synchronized (mutex) {return super.toArray(a);}
    }
	
	/**
     * {@inheritDoc}
     */
	@Override
    public <T> T[] toArray(IntFunction<T[]> f) {
        synchronized (mutex) {return super.toArray(f);}
    }
	
	/**
     * {@inheritDoc}
     */
	@Override
	public String toString() {
		final Iterator<Object> it = iterator();
		if (!it.hasNext()) return "[]";

		final StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (;;) {
			final Object e = it.next();
			sb.append(e instanceof String ? "\"" + e + "\"" : e);
			if (!it.hasNext()) return sb.append(']').toString();
			sb.append(',').append(' ');
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		synchronized (mutex) {
			return super.equals(o);
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public int hashCode() {
		synchronized (mutex) {
			return super.hashCode();
		}
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void setContentModified(boolean value) {
		synchronized (mutex) {
			this.contentModified.set(value);
		}
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public boolean isModified() {
		return this.contentModified.get();
	}

}
