package marshmalliow.core.security;

public enum AESKeySize implements KeySize {
	SIZE_128(128),
	SIZE_192(192),
	SIZE_256(256);

	private int size;
	AESKeySize(int i) {
		this.size = i;
	}
	
	public int getSize() {
		return size;
	}
}
