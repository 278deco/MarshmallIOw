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
	
	public UnmodifiableJSONArray(JSONArray list) {
		this.list = list;
	}
	
	@Override
	public void add(int index, Object element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean add(Object e) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(Collection<? extends Object> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object get(int index) {
		return list.get(index);
	}
	
	public void ensureCapacity(int minCapacity) {
		throw new UnsupportedOperationException();
	}
	
	protected void removeRange(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void replaceAll(UnaryOperator<Object> operator) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void sort(Comparator<? super Object> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object set(int index, Object element) {
		throw new UnsupportedOperationException();
	}
	
	public void trimToSize() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object remove(int index) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeIf(Predicate<? super Object> filter) {
		throw new UnsupportedOperationException();
	}
	
	public void forEach(Consumer<? super Object> action) {
		list.forEach(action);
	}
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public int indexOf(Object o) {
		 return list.indexOf(o);
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}
	
	@Override
	public int size() {
		return list.size();
	}
	
	@Override
	public boolean isEmpty() {
        return list.isEmpty();
    }
	
	@Override
    public boolean contains(Object o) {
        return list.contains(o);
    }
	
	@Override
	public boolean containsAll(Collection<?> c) {
		 return list.containsAll(c); 
	}
	
	@Override
    public Object[] toArray() {
        return list.toArray();
    }
	
	@Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }
	
	@Override
    public <T> T[] toArray(IntFunction<T[]> f) {
        return list.toArray(f);
    }
	
	@Override
	public Iterator<Object> iterator() {
		return list.iterator();
	}

	@Override
	public ListIterator<Object> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<Object> listIterator(int index) {
		return list.listIterator(index);
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		return super.equals(o);
	}
	
	@Override
	public int hashCode() {
		return list.hashCode();
	}

	@Override
	public void setContentModified(boolean value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean isModified() {
		return list.isModified();
	}
}
