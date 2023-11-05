package marshmalliow.core.security;

public enum SaltSize {
	BYTE_12(12),
	BYTE_16(16),
	BYTE_32(32),
	BYTE_64(64),
	BYTE_128(128);

	private byte size;
	SaltSize(int i) {
		size = (byte)i;
	}
	
	public byte getSize() {
		return size;
	}
}