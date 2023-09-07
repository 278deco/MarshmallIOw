package marshmalliow.core.json.objects;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JSONObject extends ConcurrentHashMap<String, Object> implements JSONContainer {

	private static final long serialVersionUID = -497856876882492805L;
	
	private final AtomicBoolean contentModified = new AtomicBoolean(false);
	
	@Override
	public boolean replace(String key, Object oldValue, Object newValue) {
		final boolean result = super.replace(key, oldValue, newValue);
		if(result) this.contentModified.set(true);
		return result;
	}
	
	@Override
	public Object replace(String key, Object value) {
		final Object replacedValue = super.replace(key, value);
		if(replacedValue != null) this.contentModified.set(true);
		return replacedValue;
	}
	
	@Override
	public void replaceAll(BiFunction<? super String, ? super Object, ? extends Object> function) {
		this.contentModified.set(true);
		super.replaceAll(function);
	}
	
	@Override
	public boolean remove(Object key, Object value) {
		final boolean result = super.remove(key, value);
		if(result) this.contentModified.set(true);
		return result;
	}
	
	@Override
	public Object remove(Object key) {
		final Object removedKey = super.remove(key);
		if(removedKey != null) this.contentModified.set(true);
		return removedKey;
	}
	
	@Override
	public Object putIfAbsent(String key, Object value) {
		final Object previousKey = super.putIfAbsent(key, value);
		if(previousKey == null) this.contentModified.set(true);
		return previousKey;
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		this.contentModified.set(true);
		super.putAll(m);
	}
	
	@Override
	public Object put(String key, Object value) {
		final Object previousValue = super.put(key, value);
		if(previousValue == null || !previousValue.equals(value)) this.contentModified.set(true);
		return previousValue;
	}
	
	@Override
	public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
		this.contentModified.set(true);
		return super.merge(key, value, remappingFunction);
	}
	
	@Override
	public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ? extends Object> remappingFunction) {
		this.contentModified.set(true);
		return super.computeIfPresent(key, remappingFunction);
	}
	
	@Override
	public Object computeIfAbsent(String key, Function<? super String, ? extends Object> mappingFunction) {
		this.contentModified.set(true);
		return super.computeIfAbsent(key, mappingFunction);
	}
	
	@Override
	public Object compute(String key, BiFunction<? super String, ? super Object, ? extends Object> remappingFunction) {
		this.contentModified.set(true);
		return super.compute(key, remappingFunction);
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
