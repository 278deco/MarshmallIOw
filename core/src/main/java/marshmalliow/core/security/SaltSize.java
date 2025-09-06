package marshmalliow.core.security;

public enum SaltSize {
	
	/**
	 * 12 bytes salt size
	 */
	BYTE_12(12),
	
	/**
	 * 16 bytes salt size
	 */
	BYTE_16(16),
	
	/**
	 * 32 bytes salt size
	 */
	BYTE_32(32),
	
	/**
	 * 64 bytes salt size
	 */
	BYTE_64(64),
	
	/**
	 * 128 bytes salt size
	 */
	BYTE_128(128);

	private byte size;
	SaltSize(int i) {
		size = (byte)i;
	}
	
	public byte getSize() {
		return size;
	}
}