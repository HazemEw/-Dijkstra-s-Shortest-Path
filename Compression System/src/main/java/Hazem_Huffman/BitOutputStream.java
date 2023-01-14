package Hazem_Huffman;

import java.io.FileOutputStream;
import java.io.IOException;

class BitOutputStream {

    private FileOutputStream out;
    public BitOutputStream(FileOutputStream is) {

        this.out = is;
    }

    public void writeH(StringBuilder header) throws IOException {
        for (int i = 0; i < header.length(); i++) {
            out.write((byte) header.charAt(i));
        }
    }
}