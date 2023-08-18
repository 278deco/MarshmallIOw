package marshmalliow.core.binary.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;
import java.time.ZoneOffset;

import marshmalliow.core.binary.utils.Charset;
import marshmalliow.core.objects.DateTime;

public class BinaryWriter {

	private static final int BUFFER_MIN_SIZE = 128;
	
	protected volatile OutputStream out;

	protected long written;

	/**
	 * Whether the stream is closed or not
	 */
	private volatile boolean closed;
	private final Object closeLock = new Object();

	/**
	 * Buffers
	 */
	private byte[] byteBuffer = new byte[BUFFER_MIN_SIZE];

    private final byte[] writeBuffer = new byte[8];

	public BinaryWriter(OutputStream out) {
		this.out = out;
	}

	private void increaseCounter(int value) {
		long temp = this.written + value;
		if (temp < 0)
			temp = Long.MAX_VALUE;

		this.written = temp;
	}

	public synchronized void write(int b) throws IOException {
		out.write(b);
		increaseCounter(1);
	}

	public synchronized void write(byte[] b, int offset, int length) throws IOException {
		out.write(b, offset, length);
		increaseCounter(length);
	}

	/**
	 * Flushes this data output stream. This forces any buffered output bytes to be
	 * written out to the stream.
	 * <p>
	 * The {@code flush} method of {@code DataOutputStream} calls the {@code flush}
	 * method of its underlying output stream.
	 *
	 * @throws IOException if an I/O error occurs.
	 * @see java.io.FilterOutputStream#out
	 * @see java.io.OutputStream#flush()
	 */
	public void flush() throws IOException {
		out.flush();
	}

	public void close() throws IOException {
		if(closed) return;

		synchronized (closeLock) {
			if(closed) return;

			closed = true;
		}

		Throwable flushException = null;
		try {
			flush();
		}catch(Throwable e) {
			flushException = e;

			throw e;
		}finally {
			if(flushException == null) out.close();
			else {
				try {
					out.close();
				}catch(Throwable closeException) {
					if(flushException instanceof ThreadDeath && !(closeException instanceof ThreadDeath)) {
						flushException.addSuppressed(closeException);

						throw (ThreadDeath) flushException;
					}

					if(flushException != closeException) closeException.addSuppressed(flushException);

					throw closeException;
				}

			}
		}
	}

	/**
	 * Writes a {@code boolean} to the underlying output stream as a 1-byte value.
	 * The value {@code true} is written out as the value {@code (byte)1}; the value
	 * {@code false} is written out as the value {@code (byte)0}. If no exception is
	 * thrown, the counter {@code written} is incremented by {@code 1}.

	 * @param value A {@code boolean} value to be written.
	 * @throws IOException if an I/O error occurs.
	 * @see java.io.FilterOutputStream#out
	 */
	public void writeBoolean(boolean value) throws IOException {
		out.write(value ? 1 : 0);
		increaseCounter(1);
	}

	public void writeByte(byte value) throws IOException {
		this.writeUnsignedByte(value);
	}

	public void writeUnsignedByte(int value) throws IOException {
		out.write(value);
		increaseCounter(1);
	}

	public void writeShort(short value) throws IOException {
		this.writeUnsignedShort(value);
	}

	public void writeUnsignedShort(int value) throws IOException {
		writeBuffer[0] = (byte)(value >>> 8);
		writeBuffer[1] = (byte)(value >>> 0);

		out.write(writeBuffer, 0, 2);
		increaseCounter(2);
	}

	public void writeChar(char value) throws IOException {
		this.writeUnsignedShort(value);
	}

	public void writeInt(int value) throws IOException {
		writeBuffer[0] = (byte)(value >>> 24);
        writeBuffer[1] = (byte)(value >>> 16);
        writeBuffer[2] = (byte)(value >>> 8);
        writeBuffer[3] = (byte)(value >>> 0);

        out.write(writeBuffer, 0, 4);
        increaseCounter(4);
	}

	public void writeLong(long value) throws IOException {
        writeBuffer[0] = (byte)(value >>> 56);
        writeBuffer[1] = (byte)(value >>> 48);
        writeBuffer[2] = (byte)(value >>> 40);
        writeBuffer[3] = (byte)(value >>> 32);
        writeBuffer[4] = (byte)(value >>> 24);
        writeBuffer[5] = (byte)(value >>> 16);
        writeBuffer[6] = (byte)(value >>> 8);
        writeBuffer[7] = (byte)(value >>> 0);

        out.write(writeBuffer, 0, 8);
        increaseCounter(8);
    }

	public void writeFloat(float value) throws IOException {
		this.writeInt(Float.floatToIntBits(value));
	}

	public void writeDouble(double value) throws IOException {
		this.writeLong(Double.doubleToLongBits(value));
	}

	public void writeUTF(String str, Charset charset) throws IOException {
		int bytelen = 0;
		switch(charset) {
			case UTF16:
				break;
			default:
				bytelen = writeUTF8(str);
				break;
		}

		increaseCounter(bytelen);
	}

	private int writeUTF8(String str) throws IOException {
		final byte[] byteBuffer;
		final int strlen = str.length();
		int strByteLen = strlen;

		for(int i = 0; i < strlen; i++) {
			int c = str.charAt(i);

			if(c >= 0x10000) strByteLen += 3;
			else if(c >= 0x800) strByteLen += 2;
			else if(c >= 0x80) strByteLen += 1;
		}

		if(strByteLen > 2147483647 || strByteLen < strlen) {
			throw new UTFDataFormatException("");
		}

		if(this.byteBuffer.length <= strByteLen+4 /*Add length bytes*/) {
			this.byteBuffer = new byte[(strByteLen*2) + 4];
			byteBuffer = this.byteBuffer;
		}else {
			byteBuffer = new byte[strByteLen*2 + 4];
		}

		//Write the size of the string
		byteBuffer[0] = (byte)((strByteLen >>> 24) & 0xFF);
		byteBuffer[1] = (byte)((strByteLen >>> 16) & 0xFF);
		byteBuffer[2] = (byte)((strByteLen >>> 8) & 0xFF);
		byteBuffer[3] = (byte)((strByteLen >>> 0) & 0xFF);

		int counter = 4;
		int index = 0;
		
		//Write in a first time the char if it is in the ASCII table
		//Skip to the next method if a non ASCII char is found
		for(; index < strlen; index++) {
			final int c = str.charAt(index);
			if(c >= 0x80) break;
			
			byteBuffer[counter++] = (byte) c;
		}

		for(; index < strlen; index++) {
			final int c = str.charAt(index);

			if(c < 0x80) {
				byteBuffer[counter++] = (byte) c;
			}else if(c >= 0x10000) {
				byteBuffer[counter++] = (byte) (0xE0 | ((c >> 18) & 0x0F));
				byteBuffer[counter++] = (byte) (0x80 | ((c >> 12) & 0x3F));
				byteBuffer[counter++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                byteBuffer[counter++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			}else if(c >= 0x800) {
				byteBuffer[counter++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
				byteBuffer[counter++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                byteBuffer[counter++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			}else {
				byteBuffer[counter++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
				byteBuffer[counter++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			}
		}

		out.write(byteBuffer, 0, strByteLen+4);

		return strByteLen+4;
	}

	public void writeDatetime(DateTime dt) throws IOException {
		final byte[] byteBuffer;
		final int dtlen = 1 /*control byte*/ + DateTime.FIELDS_NUMBER /*number of fields*/;

		if(this.byteBuffer.length < dtlen*2) {
			this.byteBuffer = new byte[(dtlen*2)];
			byteBuffer = this.byteBuffer;
		}else {
			byteBuffer = new byte[dtlen*2];
		}

		byteBuffer[0] = 0x00;
		int counter = 1;
		if(dt.isYearPresent()) {
			byteBuffer[0] += 0x80; /* control byte*/

			final int year = dt.getYear().get();
			byteBuffer[counter++] = (byte)(year >>> 8);
			byteBuffer[counter++] = (byte)(year >>> 0);
		}
		if(dt.isMonthPresent()) {
			byteBuffer[0] += 0x40; /* control byte*/

			byteBuffer[counter++] = (dt.getMonth().get());
		}
		if(dt.isDayOfMonthPresent()) {
			byteBuffer[0] += 0x20; /* control byte*/

			byteBuffer[counter++] = (dt.getDayOfMonth().get());
		}
		if(dt.isHourPresent()) {
			byteBuffer[0] += 0x10; /* control byte*/

			byteBuffer[counter++] = (dt.getHour().get());
		}
		if(dt.isMinutePresent()) {
			byteBuffer[0] += 0x08; /* control byte*/

			byteBuffer[counter++] = (dt.getMinute().get());
		}
		if(dt.isSecondPresent()) {
			byteBuffer[0] += 0x04; /* control byte*/

			byteBuffer[counter++] = (dt.getSecond().get());
		}
		if(dt.isComplementaryPresent()) {
			byteBuffer[0] += 0x02; /* control byte*/

			final short value = dt.getComplementaryValue().get();
			byteBuffer[counter++] = (byte)((dt.getComplementaryType().get().getValue() << 6) + (value >>> 8));
			byteBuffer[counter++] = (byte)(value >>> 0);
		}
		if(dt.isTimezonePresent()) {
			byteBuffer[0] += 0x01; /* control byte*/

			final ZoneOffset offset = dt.getZoneOffset().get();
			byteBuffer[counter++] = (byte)((offset.getTotalSeconds() > 0 ? 0x00 : 0x80) + writeOffset(dt.getZoneOffset().get()));
		}

		out.write(byteBuffer, 0, 1+counter);
		increaseCounter(1+counter);
	}

	private static byte writeOffset(ZoneOffset offset) {
		return (byte)Math.abs((offset.getTotalSeconds() /60 ) /15);
	}

	public long size() {
		return this.written;
	}
}
