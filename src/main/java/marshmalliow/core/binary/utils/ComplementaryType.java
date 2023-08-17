package marshmalliow.core.binary.utils;

public enum ComplementaryType {

	MILLISECONDS(0, 1000000),
	MICROSECONDS(1, 1000),
	NANOSECONDS(2, 1),
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
