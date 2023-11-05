package marshmalliow.core.security;

import javax.crypto.SecretKey;

public class FileCredentials {

	public static final FileCredentials EMPTY = FileCredentials.builder().empty();
	
	private EncryptionType type;
	private KeySize keySize;
	private SecretKey key;
	private SaltSize initVectorSize;
	
	private FileCredentials(FileCredentials.Builder builder) {
		this.type = builder.type;
		this.keySize = builder.keySize;
		this.key = builder.key;
		this.initVectorSize = builder.initVectorSize;
	}
	
	public EncryptionType getType() {
		return type;
	}
	
	public SaltSize getInitVectorSize() {
		return initVectorSize;
	}
	
	public KeySize getKeySize() {
		return keySize;
	}
	
	public SecretKey getKey() {
		return key;
	}
	
	public static final FileCredentials.Builder builder() {
		return new FileCredentials.Builder();
	}
	
	@Override
	public int hashCode() {
		int result = this.type.ordinal();
		result = 31 * result + this.initVectorSize.ordinal();
		result = 31 * result + this.keySize.hashCode();
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		return false;
	}
	
	public static final class Builder {

		private EncryptionType type;
		private KeySize keySize;
		private SecretKey key;
		private SaltSize initVectorSize;

		private Builder() {}

		public Builder encryptionType(EncryptionType type) {
			this.type = type;
			return this;
		}

		public Builder keySize(KeySize keySize) {
			this.keySize = keySize;
			return this;
		}

		public Builder key(SecretKey key) {
			this.key = key;
			return this;
		}


		public Builder vectorSize(SaltSize size) {
			this.initVectorSize = size;
			return this;
		}

		public FileCredentials build() {
			if(this.type == null) throw new IllegalArgumentException("type must not be null");

			return new FileCredentials(this);
		}
		
		private FileCredentials empty() {
			this.type = EncryptionType.NONE;
			return new FileCredentials(this);
		}

	}
}
