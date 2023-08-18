package marshmalliow.core.binary.utils;

public enum CompressionType {

	GZIP(new byte[] {0x1F, (byte)0x8B}),
	NONE(new byte[] {0x00});
	
	private byte[] signature;
	private CompressionType(byte[] signature) {
		this.signature = signature;
	}
	
	public byte[] getSignature() {
		return signature;
	}
	
	public int getSignatureLength() {
		return signature.length;
	}
	
}
