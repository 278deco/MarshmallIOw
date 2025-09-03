package marshmalliow.core.binary.utils;

public enum CompressionType {

	/**
	 * GZIP compression
	 * @see <a href="https://tools.ietf.org/html/rfc1952">RFC 1952</a>
	 */
	GZIP(new byte[] {0x1F, (byte)0x8B}, true),
	
	/**
	 * Automatic detection of compression type based on the signature bytes
	 * @see CompressionType#getSignature()
	 */
	AUTOMATIC(new byte[] {}, false),
	
	/**
	 * No compression
	 */
	NONE(new byte[] {0x00}, false);
	
	private byte[] signature;
	private boolean realAlgorithm;
	private CompressionType(byte[] signature, boolean realAlgorithm) {
		this.signature = signature;
		this.realAlgorithm = realAlgorithm;
	}
	
	public byte[] getSignature() {
		return signature;
	}
	
	public int getSignatureLength() {
		return signature.length;
	}
	
	public boolean isRealAlgorithm() {
		return realAlgorithm;
	}
	
}
