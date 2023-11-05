package marshmalliow.core.security;

public enum EncryptionType {
	
	AES_GCM_TAG_128("AES/GCM/NoPadding"),
	AES_GCM_TAG_96("AES/GCM/NoPadding"),
	AES_GCM_TAG_112("AES/GCM/NoPadding"),
	NONE("none");

	private String encryption;
	private EncryptionType(String encryption) {
		this.encryption = encryption;
	}
	
	public String getEncryption() {
		return encryption;
	}
}
