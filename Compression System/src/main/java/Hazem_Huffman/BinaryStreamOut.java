package Hazem_Huffman;

import java.io.*;

public final class BinaryStreamOut {
	private static BufferedOutputStream out;// output stream (standard output)
	private static int buffer; // 8-bit buffer of bits to write
	static OutputStream outPutStream;
	private static int n; // number of bits remaining in buffer
	private static boolean isInitialized; // has BinaryStdOut been called for first time?

	public BinaryStreamOut(FileOutputStream fileOutputStream) throws FileNotFoundException {
		outPutStream = fileOutputStream;
		initialize();
	}

	void initialize() {
		out = new BufferedOutputStream(outPutStream);

		buffer = 0;
		n = 0;
		isInitialized = true;
	}

	private void writeBit(boolean bit) {
		if (!isInitialized)
			initialize();

		// add bit to buffer
		buffer <<= 1;

		if (bit)
			buffer |= 1; // like increment the buffer

		// if buffer is full (8 bits), write out as a single byte
		n++;
		if (n == 8)   // the buffer fill
			clearBuffer();
	}

	private void writeByte(int x) {
		if (!isInitialized)
			initialize();

		assert x >= 0 && x < 256;

		if (n == 0) {
			try {
				out.write(x);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		for (int i = 0; i < 8; i++) {
			boolean bit = ((x >>> (8 - i - 1)) & 1) == 1;
			writeBit(bit);
		}
	}

	void clearBuffer() {
		if (!isInitialized)
			initialize();

		if (n == 0)
			return;
		if (n > 0)
			buffer <<= (8 - n);  // eof
		try {
			out.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		n = 0;
		buffer = 0;
	}

	public void flush() {
		clearBuffer();
		try {
			out.flush(); //// flush buffered data to the output device
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		flush();
		try {
			out.close();
			isInitialized = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(boolean x) {
		writeBit(x);
	}

	public void write(byte x) {
		writeByte(x + 128 & 0xff);
	}

	public void writeH(StringBuilder header) throws IOException {
		for (int i = 0; i < header.length(); i++) {
			write((byte) header.charAt(i));
		}
	}

}