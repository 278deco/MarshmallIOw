package marshmalliow.core.binary.data;

import java.io.IOException;
import java.util.Arrays;

import marshmalliow.core.binary.exceptions.MalformedHeaderException;
import marshmalliow.core.binary.io.BinaryReader;
import marshmalliow.core.binary.io.BinaryWriter;
import marshmalliow.core.binary.utils.Charset;

public class MOBFFileHeader {

	public static final MOBFFileHeader DEFAULT_HEADER = new MOBFFileHeader(Charset.UTF8, 1);
	
	/**
	 * Official signature for the MOBF File Format<br/>
	 * Cannot recognize a file if the signature isn't present or corruped
	 */
	private static final byte[] MOBF_FILE_SIGNATURE = {0x53, 0x54, 0x50, 0x43};

	private byte[] signature = new byte[4];
	private Charset encodingCharset;
	private int version; //unsigned byte

	public MOBFFileHeader(BinaryReader reader) throws IOException {
		reader.read(signature, 0, 4);
		if(!Arrays.equals(signature, MOBF_FILE_SIGNATURE))
			throw new MalformedHeaderException("Incorrect header signature", MOBF_FILE_SIGNATURE, this.signature);
		
		final byte encoding = reader.readByte();
		this.encodingCharset = Charset.of(encoding);

		if(this.encodingCharset.equals(Charset.NULL))
			throw new MalformedHeaderException("Incorrect encoding charset byte", 4, new byte[]{0x00, 0x01}, encoding);

		this.version = reader.readUnsignedByte();

		if((reader.readByte() | reader.readByte()) != 0)
			throw new MalformedHeaderException("Incorrect header ending");
	}

	public MOBFFileHeader(Charset encoding, int version) {
		this.signature = MOBF_FILE_SIGNATURE;
		this.encodingCharset = encoding;
		this.version = version;
	}

	public void write(BinaryWriter writer) throws IOException {
		writer.write(this.signature, 0, 4);

		writer.writeByte(this.encodingCharset.getId());

		writer.writeUnsignedByte(this.version);

		writer.write(new byte[] {0x00, 0x00}, 0, 2);
	}

	public byte[] getSignature() {
		return signature;
	}

	public Charset getEncodingCharset() {
		return encodingCharset;
	}

	public int getVersion() {
		return version;
	}
}
