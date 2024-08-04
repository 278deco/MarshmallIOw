package marshmalliow.core.json.objects;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class UnmodifiableJSONArray implements List<Object>, JSONContainer {
		
	private final JSONArray list;
	
	/**
	 * Constructor for {@link UnmodifiableJSONArray}. Wraps the given {@link JSONArray} in an unmodifiable wrapper.
	 * @param list the {@link JSONArray} to be wrapped.
	 */
	public UnmodifiableJSONArray(JSONArray list) {
		this.list = list;
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not supported by this class.
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void add(int index, Object element) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public boolean add(Object e) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public boolean addAll(Collection<? extends Object> c) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object get(int index) {
		return list.get(index);
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	public void ensureCapacity(int minCapacity) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	protected void removeRange(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void replaceAll(UnaryOperator<Object> operator) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void sort(Comparator<? super Object> c) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public Object set(int index, Object element) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	public void trimToSize() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public Object remove(int index) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public boolean removeIf(Predicate<? super Object> filter) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void forEach(Consumer<? super Object> action) {
		list.forEach(action);
	}
	
	/**
	 * Throws an {@link UnsupportedOperationException} as this method is not
	 * supported by this class.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int indexOf(Object o) {
		 return list.indexOf(o);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return list.size();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
        return list.isEmpty();
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public boolean contains(Object o) {
        return list.contains(o);
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		 return list.containsAll(c); 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public Object[] toArray() {
        return list.toArray();
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public <T> T[] toArray(IntFunction<T[]> f) {
        return list.toArray(f);
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Object> iterator() {
		return list.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListIterator<Object> listIterator() {
		return list.listIterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListIterator<Object> listIterator(int index) {
		return list.listIterator(index);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		return super.equals(o);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return list.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setContentModified(boolean value) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isModified() {
		return list.isModified();
	}
}
