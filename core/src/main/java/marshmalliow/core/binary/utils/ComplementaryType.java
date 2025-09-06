package marshmalliow.core.binary.utils;

public enum ComplementaryType {

	/**
	 * Milliseconds (ms) <br/>
	 * 1 ms = 1_000_000 ns
	 */
	MILLISECONDS(0, 1000000),
	
	/**
	 * Microseconds (μs) <br/>
	 * 1 μs = 1_000 ns
	 */
	MICROSECONDS(1, 1000),
	
	/**
     * Nanoseconds (ns) <br/>
     * 1 ns = 1 ns
     */
	NANOSECONDS(2, 1),
	
	/**
	 * Null value
	 */
	NULL(-1, 0);

	private int index;
	private int fromNanos;
	private ComplementaryType(int i, int fromNanos) {
		this.index = i;
		this.fromNanos = fromNanos;
	}

	public int getValue() {
		return index;
	}

	public int getMultiplierFromNanos() {
		return fromNanos;
	}

	public static ComplementaryType of(byte value) {
		for(ComplementaryType type : ComplementaryType.values()) {
			if(type.getValue() == value) return type;
		}

		return NULL;
	}



}
