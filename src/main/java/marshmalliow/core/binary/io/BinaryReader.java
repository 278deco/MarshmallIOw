package marshmalliow.core.binary.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;

import marshmalliow.core.binary.MOBFFile;
import marshmalliow.core.binary.utils.Charset;
import marshmalliow.core.exceptions.DatetimeDataFormatException;
import marshmalliow.core.objects.DateTime;

/**
 * A class for reading binary data from a stream, designed for {@link MOBFFile} files.
 * @version 1.0.0
 * @author 278deco
 */
public class BinaryReader {

	private static final int BUFFER_MIN_SIZE = 64;

	protected volatile InputStream in;
	
	/**
	 * Whether the stream is closed or not
	 */
	private volatile boolean closed;
	private final Object closeLock = new Object();
	
	private byte[] byteBuffer = new byte[BUFFER_MIN_SIZE];
	private char[] charBuffer = new char[BUFFER_MIN_SIZE];

	private byte readBuffer[] = new byte[8];


	public BinaryReader(InputStream in) {
		this.in = in;
	}

	/**
	 * Closes the stream and releases any system resources associated with it.<br/>
	 * Once the stream has been closed, further read() invocations will throw an IOException.<br/>
	 * Multiple calls to close are allowed and will have no effect.
	 * @throws IOException
	 */
	public void close() throws IOException {
		if(closed) return;

		synchronized (closeLock) {
			if(closed) return;

			closed = true;
		}

		try {
			in.close();
		}catch(Throwable closeException) {
			throw closeException;
		}
	}
	
	/**
	 * Read up to <em>len</em> bytes from the stream. 
	 * @param b the buffer into which the data is read.
	 * @return the total number of bytes read into the buffer, or -1 if there is no more data because the end of the stream has been reached.
	 * @see InputStream#read(byte[], int, int)
	 * @throws IOException
	 */
	public int read(byte[] b) throws IOException {
		return in.read(b, 0, b.length);
	}

	/**
     * Read up to <em>length</em> bytes from the stream. 
     * @param b the buffer into which the data is read.
     * @param offset the start offset in the buffer at which the data is written.
     * @param length the maximum number of bytes to read.
     * @return the total number of bytes read into the buffer, or -1 if there is no more data because the end of the stream has been reached.
     * @see InputStream#read(byte[], int, int)
     * @throws IOException
     */
	public int read(byte[] b, int offset, int length) throws IOException {
		return in.read(b, offset, length);
	}


	/**
	 * Read up to <em>len</em> bytes from the stream. This method read until the array is full or the end of the stream is reached.
	 * @param b the buffer into which the data is read.
	 * @throws IOException
	 */
	public void readFully(byte[] b) throws IOException {
		this.readFully(b, 0, b.length);
	}

	/**
	 * Read up to <em>len</em> bytes from the stream. This method read until the array is full or the end of the stream is reached.
	 * @param b the buffer into which the data is read.
	 * @param off the start offset in the buffer at which the data is written
	 * @param len the maximum number of bytes to read
	 * @throws IOException
	 */
	public void readFully(byte[] b, int off, int len) throws IOException {
		Objects.checkFromIndexSize(off, len, b.length);
        int n = 0;
        while (n < len) {
            int count = in.read(b, off + n, len - n);

            if (count < 0) throw new EOFException();
            n += count;
        }
	}


	/**
	 * Skips over and discards <em>n</em> bytes of data from the input stream.
	 * @param n the number of bytes to be skipped.
	 * @return the actual number of bytes skipped.
	 * @throws IOException
	 */
	public int skipBytes(int n) throws IOException {
		int skipped = 0;
		int curr = 0;

		do {
			curr = (int) in.skip(n-skipped);
		}while(skipped<n && curr > 0);

		return skipped;
	}


	/**
	 * Reads a boolean from the input
	 * 
	 * @return the byte value converted to a boolean
	 * @throws EOFException if the end of the stream is reached
	 * @throws IOException 
	 */
	public boolean readBoolean() throws IOException {
		int bl = in.read();
		if(bl < 0) throw new EOFException();

		return (bl != 0);
	}

	/**
	 * Reads a byte from the input
	 * 
	 * @return the byte value
	 * @throws EOFException if the end of the stream is reached
	 * @throws IOException
	 */
	public byte readByte() throws IOException {
		int b = in.read();
		if(b < 0) throw new EOFException();

		return (byte)b;
	}

	/**
	 * Reads an unsigned byte from the input. Because Java doesn't have unsigned byte, it is returned as an integer.
	 * 
	 * @return the unsigned byte value
	 * @throws EOFException if the end of the stream is reached
	 * @throws IOException
	 */
	public int readUnsignedByte() throws IOException {
		int ubyte = in.read();
		if(ubyte < 0) throw new EOFException();

		return ubyte;
	}

	/**
	 * Reads a short from the input. The short is read as two bytes, with the first byte being the most significant.
	 * 
	 * @return the short value
	 * @throws EOFException if the end of the stream is reached
	 * @throws IOException
	 */
	public short readShort() throws IOException {
		int short1 = in.read();
		int short2 = in.read();
		if(short1 < 0 || short2 < 0) throw new EOFException();

		return (short)((short1 << 8) + short2);
	}

	/**
	 * Reads an unsigned short from the input. The short is read as two bytes, with
	 * the first byte being the most significant. Because Java doesn't have unsigned short, it is returned as an integer.
	 * 
	 * @return the unsigned short value
	 * @throws EOFException if the end of the stream is reached
	 * @throws IOException
	 */
	public int readUnsignedShort() throws IOException {
		int short1 = in.read();
		int short2 = in.read();
		if((short1 |short2) < 0) throw new EOFException();

		return ((short1 << 8) + short2);
	}

	/**
	 * Reads a char from the input. The char is read as two bytes, with the first
	 * byte being the most significant.
	 * 
	 * @return the char value
	 * @throws EOFException if the end of the stream is reached
	 * @throws IOException
	 */
	public char readChar() throws IOException {
		return (char)readUnsignedShort();
	}

	/**
	 * Reads an integer from the input. The integer is read as four bytes, with the
	 * first byte being the most significant.
	 * 
	 * @return the integer value
	 * @throws EOFException if the end of the stream is reached
	 * @throws IOException
	 */
	public int readInt() throws IOException {
		readFully(readBuffer, 0, 4);
		
		return (int) (((readBuffer[0] & 0xFF) << 24) + ((readBuffer[1] & 0xFF) << 16) + ((readBuffer[2] & 0xFF) << 8) + (readBuffer[3] & 0xFF));
	}

	/**
	 * Reads a long from the input. The long is read as eight bytes, with the first
	 * byte being the most significant.
	 * 
	 * @return the long value
	 * @throws EOFException if the end of the stream is reached
	 * @throws IOException
	 */
	public long readLong() throws IOException {
		readFully(readBuffer, 0, 8);

		return ((long)readBuffer[0] << 56) + ((long)(readBuffer[1] & 255) << 48)
				+ ((long)(readBuffer[2] & 255) << 40) + ((long)(readBuffer[3] & 255) << 32)
				+ ((readBuffer[4] & 255) << 24) + ((readBuffer[5] & 255) << 16)
				+ ((readBuffer[6] & 255) << 8) + (readBuffer[7] & 255);
	}

	/**
	 * Reads a float from the input. The float is read as four bytes, with the first
	 * byte being the most significant. We use {@link Float#intBitsToFloat(int)} to convert the integer to a float.
	 * 
	 * @return the float value
	 * @throws EOFException if the end of the stream is reached
	 * @throws IOException
	 */
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	/**
	 * Reads a double from the input. The double is read as eight bytes, with the
	 * first byte being the most significant. We use
	 * {@link Double#longBitsToDouble(long)} to convert the long to a double.
	 * 
	 * @return the double value
	 * @throws EOFException if the end of the stream is reached
	 * @throws IOException
	 */
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}


	/**
	 * Reads a string from the input. The string is read depending on the {@link Charset}.<br/>
	 * <strong>For now only UTF-8 is supported</strong>
	 * 
	 * @return the string value
	 * @throws EOFException if the end of the stream is reached
	 * @throws IOException
	 */
	public String readUTF(Charset charset) throws IOException {
		switch(charset) {
			case UTF16:
				return "";
			default:
				return readUTF8();
		}
	}

	private String readUTF8() throws IOException {
		byte[] byteBuffer = null;
		char[] charBuffer = null;

		final int strlen = readInt();
		if(strlen < 0) throw new UTFDataFormatException("");

		if(strlen >= BUFFER_MIN_SIZE) {
			this.byteBuffer = new byte[strlen*2];
			this.charBuffer = new char[strlen*2];
		}
		byteBuffer = this.byteBuffer;
		charBuffer = this.charBuffer;

		int char1, char2, char3, char4;
		int count = 0;
		int charBufferIndex = 0;

		readFully(byteBuffer, 0, strlen);
		//Parse in a first time the char if it is in the ASCII table
		//Skip to the next method if a non ASCII char is found
		while(count < strlen) {
			char1 = byteBuffer[count] & 0xFF;
			if(char1 > 127) break;
			count++;
			charBuffer[charBufferIndex++] = (char)char1;
		}

		while(count < strlen) {
			char1 = byteBuffer[count] & 255;

			if(char1 >> 7 == 0) {
				count++;
				charBuffer[charBufferIndex++] = (char)char1;
			}else {
				switch(char1 >> 4) {
					case 12, 13 -> {
						count+=2;
						if(count > strlen) throw new UTFDataFormatException();

						char2 = byteBuffer[count-1];
						if((char2 & 0xC0) != 0x80) throw new UTFDataFormatException();

						charBuffer[charBufferIndex++] = (char)(((char1 & 0x1F) << 6) | (char2 & 0x3F));
					}
					case 14 -> {
						count+=3;
						if(count > strlen) throw new UTFDataFormatException();

						char2 = byteBuffer[count-2];
						char3 = byteBuffer[count-1];
						if((char2 & 0xC0) != 0x80 || (char3 & 0xC0) != 0x80) throw new UTFDataFormatException();

						charBuffer[charBufferIndex++] = (char)(((char1 & 0x0F) << 12) | ((char2 & 0x3F) << 6) | (char3 & 0x3F));
					}
					case 15 -> {
						count+=4;
						if(count < strlen) throw new UTFDataFormatException();

						char2 = byteBuffer[count-3];
						char3 = byteBuffer[count-2];
						char4 = byteBuffer[count-1];
						if((char2 & 0xC0) != 0x80 || (char3 & 0xC0) != 0x80 || (char4 & 0xC0) != 0x80) throw new UTFDataFormatException();

						charBuffer[charBufferIndex++] = (char)(((char1 & 0x0F) << 18) | ((char2 & 0x3F) << 12) | ((char3 & 0x3F) << 6) | (char4 & 0x3F));
					}

				}
			}

		}
		return new String(charBuffer, 0, charBufferIndex);
	}

	/**
     * Reads a {@link DateTime} from the input. The DateTime is read depending on the control byte.
     * 
     * @return the DateTime value
     * @throws EOFException if the end of the stream is reached
     * @throws IOException
     * @throws DatetimeDataFormatException if the control byte is 0x00
     */
	public DateTime readDatetime() throws IOException {
		final DateTime.Builder builder = DateTime.builder();
		final int controlByte = readByte(); //Read the control byte
		
		if(controlByte == 0x00) throw new DatetimeDataFormatException();

		if(((controlByte >> 7) & 0x01) == 1) builder.year(readUnsignedShort());
		if(((controlByte >> 6) & 0x01) == 1) builder.month(readByte());
		if(((controlByte >> 5) & 0x01) == 1) builder.dayOfMonth(readByte());
		if(((controlByte >> 4) & 0x01) == 1) builder.hour(readByte());
		if(((controlByte >> 3) & 0x01) == 1) builder.minute(readByte());
		if(((controlByte >> 2) & 0x01) == 1) builder.second(readByte());
		if(((controlByte >> 1) & 0x01) == 1) {
			final int data = readUnsignedShort();
			builder.complementary((byte)((data >> 8) & 0xC0), (short)(data & 0x3FF));
		}

		if((controlByte & 0x01) == 1) {
			final byte data = readByte();
			final ZoneOffset offset = calculateOffset((byte)(data & 0x3F), (((data >> 7) & 0x01) == 1));
			final String timezone = "UTC"+offset;

			builder.timezone(ZoneId.of(timezone), offset);
		}

		return builder.build();
	}

	private static ZoneOffset calculateOffset(byte data, boolean negative) {
		int hour = (negative ? -1 : 1) * ((data * 15) / 60);
		int minute = (negative ? -1 : 1) * ((data-(Math.abs(hour)*4)) * 15);
		
		return ZoneOffset.ofHoursMinutes(hour, minute);
	}

}
