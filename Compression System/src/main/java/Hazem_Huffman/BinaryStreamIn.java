package Hazem_Huffman;

import java.io.*;
import java.util.NoSuchElementException;

public class BinaryStreamIn {
	private static final int EOF = -1; // end of file

	private static BufferedInputStream in; // input stream
	private static FileInputStream inu;
	private static int buffer; // one character buffer
	private static int n; // number of bits left in buffer
	private static boolean isInitialized; // has BinaryStdIn been called for first time?

	public BinaryStreamIn(File f) throws FileNotFoundException {
		BinaryStreamIn.inu = new FileInputStream(f);
		initialize();

	}

	 private void initialize() {
		in = new BufferedInputStream(inu);
		buffer = 0;
		n = 0;
		fillBuffer();
		isInitialized = true;
	}

private	void fillBuffer() {
		try {
			buffer = in.read();
			n = 8;
		} catch (IOException e) {
			System.out.println("EOF");
			buffer = EOF;
			n = -1;
		}
	}

	 void close() {
		if (!isInitialized)
			initialize();
		try {
			in.close();
			isInitialized = false;
		} catch (IOException ioe) {
			throw new IllegalStateException("Could not close BinaryStdIn", ioe);
		}
	}

	private boolean isEmpty() {
		if (!isInitialized)
			initialize();

		return buffer == -1;
	}

	public boolean readBoolean() {
		if (isEmpty())
			throw new NoSuchElementException("Reading from empty input stream");
		n--;
		boolean bit = ((buffer >> n) & 1) == 1;
		if (n == 0)
			fillBuffer();
		return bit;
	}

	public char readChar() {
		if (isEmpty())
			throw new NoSuchElementException("Reading from empty input stream");

		if (n == 8) {
			int x = buffer;
			fillBuffer();
			return (char) (x & 0xff);
		}

		int x = buffer;
		x <<= (8 - n);
		int oldN = n;
		fillBuffer();
		if (isEmpty())
			throw new NoSuchElementException("Reading from empty input stream");
		n = oldN;
		x |= (buffer >>> n);
		return (char) (x & 0xff);
	}

	public byte readByte() {
		char c = readChar();
		return (byte) (c & 0xff);
	}

}
