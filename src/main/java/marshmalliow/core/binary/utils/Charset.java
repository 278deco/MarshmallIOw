package marshmalliow.core.binary.utils;

import java.nio.charset.StandardCharsets;

public enum Charset {

	UTF8(0x01, StandardCharsets.UTF_8),
	UTF16(0x02, StandardCharsets.UTF_16),
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
