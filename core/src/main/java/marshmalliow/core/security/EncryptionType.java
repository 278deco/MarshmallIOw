package marshmalliow.core.security;

public enum EncryptionType {
	/**
	 * AES encryption with GCM mode and a 128-bit tag. No padding is used.
	 * @see <a href="https://nvlpubs.nist.gov/nistpubs/Legacy/SP/nistspecialpublication800-38d.pdf">NIST Special Publication 800-38D</a>
	 */
	AES_GCM_TAG_128("AES/GCM/NoPadding"),
	
	/**
	 * AES encryption with GCM mode and a 96-bit tag. No padding is used.
	 * @see <a href="https://nvlpubs.nist.gov/nistpubs/Legacy/SP/nistspecialpublication800-38d.pdf">NIST Special Publication 800-38D</a>
	 */
	AES_GCM_TAG_96("AES/GCM/NoPadding"),
	
	/**
	 * AES encryption with GCM mode and a 112-bit tag. No padding is used.
	 * @see <a href="https://nvlpubs.nist.gov/nistpubs/Legacy/SP/nistspecialpublication800-38d.pdf">NIST Special Publication 800-38D</a>
	 */
	AES_GCM_TAG_112("AES/GCM/NoPadding"),
	
	/**
	 * No encryption.
	 */
	NONE("none");

	private String encryption;
	private EncryptionType(String encryption) {
		this.encryption = encryption;
	}
	
	public String getEncryption() {
		return encryption;
	}
}
