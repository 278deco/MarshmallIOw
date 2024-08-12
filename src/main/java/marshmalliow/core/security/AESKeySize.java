package marshmalliow.core.security;

public enum AESKeySize implements KeySize {
	
	/**
	 * AES 128 bits key size
	 */
	SIZE_128(128),
	
	/**
	 * AES 192 bits key size
	 */
	SIZE_192(192),
	
	/**
	 * AES 256 bits key size
	 */
	SIZE_256(256);

	private int size;
	AESKeySize(int i) {
		this.size = i;
	}
	
	public int getSize() {
		return size;
	}
}
