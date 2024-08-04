package marshmalliow.core.binary.data;

import java.io.IOException;
import java.util.Arrays;

import marshmalliow.core.binary.exceptions.MalformedHeaderException;
import marshmalliow.core.binary.utils.Charset;
import marshmalliow.core.io.BinaryReader;
import marshmalliow.core.io.BinaryWriter;

public class MOBFFileHeader {

	/**
	 * Official signature for the MOBF File Format<br/>
	 * Cannot recognize a file if the signature isn't present or corrupted
	 */
	private static final byte[] MOBF_FILE_SIGNATURE = {0x53, 0x54, 0x50, 0x43};
	
	public static final MOBFFileHeader DEFAULT_HEADER = new MOBFFileHeader(Charset.UTF8, 1);
	
	private byte[] signature = new byte[4];
	private Charset encodingCharset;
	private int version; //unsigned byte

	/**
	 * Given a {@link BinaryReader}, reads and ensure the validity of the header
	 * @param reader The binary reader instance
	 * @throws IOException
	 */
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

	/**
	 * Creates a new MOBF File Header with the given encoding charset and version
	 * @param encoding The encoding charset used
	 * @param version The version of the MOBF File Format
	 */
	public MOBFFileHeader(Charset encoding, int version) {
		System.arraycopy(MOBF_FILE_SIGNATURE, 0, this.signature, 0, 4);
		this.encodingCharset = encoding;
		this.version = version;
	}

	/**
	 * Given a {@link BinaryWriter}, writes the header to the output
	 * @param writer The binary writer instance
	 * @throws IOException
	 */
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
