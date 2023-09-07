package marshmalliow.core.json.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class JSONArray extends ArrayList<Object> implements JSONContainer {

	private static final long serialVersionUID = 3593877469226039660L;
	
	private final AtomicBoolean contentModified = new AtomicBoolean(false);
	
	@Override
	public void add(int index, Object element) {
		this.contentModified.set(true);
		super.add(index, element);
	}
	
	@Override
	public boolean add(Object e) {
		this.contentModified.set(true);
		return super.add(e);
	}
	
	@Override
	public boolean addAll(Collection<? extends Object> c) {
		this.contentModified.set(true);
		return super.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		this.contentModified.set(true);
		return super.addAll(index, c);
	}
	
	@Override
	public void ensureCapacity(int minCapacity) {
		this.contentModified.set(true);
		super.ensureCapacity(minCapacity);
	}
	
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		this.contentModified.set(true);
		super.removeRange(fromIndex, toIndex);
	}
	
	@Override
	public void replaceAll(UnaryOperator<Object> operator) {
		this.contentModified.set(true);
		super.replaceAll(operator);
	}
	
	@Override
	public void sort(Comparator<? super Object> c) {
		this.contentModified.set(true);
		super.sort(c);
	}
	
	@Override
	public Object set(int index, Object element) {
		this.contentModified.set(true);
		return super.set(index, element);
	}
	
	@Override
	public void trimToSize() {
		this.contentModified.set(true);
		super.trimToSize();
	}
	
	@Override
	public Object remove(int index) {
		this.contentModified.set(true);
		return super.remove(index);
	}
	
	@Override
	public boolean remove(Object o) {
		this.contentModified.set(true);
		return super.remove(o);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		this.contentModified.set(true);
		return super.removeAll(c);
	}
	
	@Override
	public boolean removeIf(Predicate<? super Object> filter) {
		this.contentModified.set(true);
		return super.removeIf(filter);
	}
	
	@Override
	public void clear() {
		this.contentModified.set(true);
		super.clear();
	}

	public void setContentModified(boolean value) {
		this.contentModified.set(value);
	}
	
	public boolean isContentModified() {
		return this.contentModified.get();
	}

}
