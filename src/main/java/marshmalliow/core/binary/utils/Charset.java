package marshmalliow.core.binary.utils;

import java.nio.charset.StandardCharsets;

public enum Charset {

	/**
	 * The UTF-8 charset. <br/>
	 * Uses 0x01 as id in MOBF specification.
	 */
	UTF8(0x01, StandardCharsets.UTF_8),
	
	/**
	 * The UTF-16 charset. <br/>
	 * Uses 0x02 as id in MOBF specification.
	 */
	UTF16(0x02, StandardCharsets.UTF_16),
	
	/**
	 * Null charset.
	 */
	NULL(-1, null);

	private byte id;
	private java.nio.charset.Charset stdCharset;
	private Charset(int id, java.nio.charset.Charset stdCharset) {
		this.id = (byte)id;
		this.stdCharset = stdCharset;
	}

	public byte getId() {
		return id;
	}
	
	public java.nio.charset.Charset getStandardCharset() {
		return stdCharset;
	}

	public static Charset of(byte id) {
		for(Charset charset : Charset.values()) {
			if(charset.getId() == id) return charset;
		}

		return NULL;
	}
}
