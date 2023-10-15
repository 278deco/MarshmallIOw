package marshmalliow.core.json.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class JSONArray extends ArrayList<Object> implements JSONContainer {

	private static final long serialVersionUID = 3593877469226039660L;
	
	private final AtomicBoolean contentModified = new AtomicBoolean(false);
	private final Object mutex;
	
	public JSONArray(Object mutex) {
		super();
		this.mutex = mutex;
	}
	
	public JSONArray(int initialCapacity, Object mutex) {
		super(initialCapacity);
		this.mutex = mutex;
	}
	
	public JSONArray(Collection<? extends Object> c, Object mutex) {
		super(c);
		this.mutex = mutex;
	}
	
	public JSONArray() {
		this(new Object());
	}
	
	public JSONArray(int initialCapacity) {
		this(initialCapacity, new Object());
	}
	
	public JSONArray(Collection<? extends Object> c) {
		this(c, new Object());
	}
	
	@Override
	public void add(int index, Object element) {
		synchronized (mutex) {
			this.contentModified.set(true);
			super.add(index, element);
		}
	}
	
	@Override
	public boolean add(Object e) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.add(e);
		}
	}
	
	@Override
	public boolean addAll(Collection<? extends Object> c) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.addAll(c);
		}
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.addAll(index, c);
		}
	}
	
	@Override
	public void ensureCapacity(int minCapacity) {
		synchronized (mutex) {
			this.contentModified.set(true);
			super.ensureCapacity(minCapacity);
		}
	}
	
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		synchronized (mutex) {
			this.contentModified.set(true);
			super.removeRange(fromIndex, toIndex);
		}
	}
	
	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		synchronized (mutex) {
			return new JSONArray(super.subList(fromIndex, toIndex), mutex);
		}
	}
	
	@Override
	public void replaceAll(UnaryOperator<Object> operator) {
		synchronized (mutex) {
			this.contentModified.set(true);
			super.replaceAll(operator);
		}
	}
	
	@Override
	public void sort(Comparator<? super Object> c) {
		synchronized (mutex) {
			this.contentModified.set(true);
			super.sort(c);
		}
	}
	
	@Override
	public Object set(int index, Object element) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.set(index, element);
		}
	}
	
	@Override
	public void trimToSize() {
		synchronized (mutex) {
			this.contentModified.set(true);
			super.trimToSize();
		}
	}
	
	@Override
	public Object remove(int index) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.remove(index);
		}
	}
	
	@Override
	public boolean remove(Object o) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.remove(o);
		}
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.retainAll(c); 
			}
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.removeAll(c);
		}
	}
	
	@Override
	public boolean removeIf(Predicate<? super Object> filter) {
		synchronized (mutex) {
			this.contentModified.set(true);
			return super.removeIf(filter);
		}
	}
	
	@Override
	public void forEach(Consumer<? super Object> action) {
		synchronized (mutex) {
			super.forEach(action);
		}
	}
	
	@Override
	public void clear() {
		synchronized (mutex) {
			this.contentModified.set(true);
			super.clear();
		}
	}
	
	@Override
	public int indexOf(Object o) {
		synchronized (mutex) { return super.indexOf(o); }
	}
	
	@Override
	public int lastIndexOf(Object o) {
		synchronized (mutex) { return super.lastIndexOf(o); }
	}
	
	@Override
	public int size() {
		synchronized (mutex) {
			return super.size();
		}
	}
	
	@Override
	public boolean isEmpty() {
        synchronized (mutex) {return super.isEmpty();}
    }
	
	@Override
    public boolean contains(Object o) {
        synchronized (mutex) {return super.contains(o);}
    }
	
	@Override
	public boolean containsAll(Collection<?> c) {
		synchronized (mutex) { return super.containsAll(c); }
	}
	
	@Override
    public Object[] toArray() {
        synchronized (mutex) {return super.toArray();}
    }
	
	@Override
    public <T> T[] toArray(T[] a) {
        synchronized (mutex) {return super.toArray(a);}
    }
	
	@Override
    public <T> T[] toArray(IntFunction<T[]> f) {
        synchronized (mutex) {return super.toArray(f);}
    }
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		synchronized (mutex) {
			return super.equals(o);
		}
	}
	
	@Override
	public int hashCode() {
		synchronized (mutex) {
			return super.hashCode();
		}
	}

	@Override
	public void setContentModified(boolean value) {
		synchronized (mutex) {
			this.contentModified.set(value);
		}
	}
	
	@Override
	public boolean isModified() {
		return this.contentModified.get();
	}

}
