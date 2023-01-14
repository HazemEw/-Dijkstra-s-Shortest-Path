package Hazem_Huffman;

public class HuffmanNode implements Comparable<HuffmanNode> {

	private int freq;
	private byte val;
	private boolean leaf;
	private HuffmanNode left, right;

	public HuffmanNode(byte ch, int freq, HuffmanNode left, HuffmanNode right, boolean n) {
		this.val = ch;
		this.freq = freq;
		this.left = left;
		this.right = right;
		this.leaf = n;
	}

	public HuffmanNode(int freq, byte val) {
		super();
		this.freq = freq;
		this.val = (byte) (val);
		this.leaf = false;
	}

	public HuffmanNode(byte val, boolean leaf) {
		super();
		this.val = (byte) (val);
		this.leaf = leaf;
	}

	public HuffmanNode(int freq, byte val, boolean leaf) {
		super();
		this.freq = freq;
		this.val = (byte) (val - 128);
		this.leaf = leaf;
	}

	public HuffmanNode(HuffmanNode left, HuffmanNode right) {
		this.left = left;
		this.right = right;
		this.leaf = false;
	}

	public void addLift(HuffmanNode left) {
		this.freq += left.freq;
		this.left = left;
	}

	public void addRight(HuffmanNode right) {
		this.freq += right.freq;
		this.right = right;
	}

	public HuffmanNode(int freq) {

		this.freq = freq;

	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public byte getVal() {
		return val;
	}

	public void setVal(byte val) {
		this.val = val;
	}

	public HuffmanNode getLeft() {
		return left;
	}

	public void setLeft(HuffmanNode left) {
		this.left = left;
	}

	public HuffmanNode getRight() {
		return right;
	}

	public void setRight(HuffmanNode right) {
		this.right = right;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	boolean isLeaf() {
		assert ((left == null) && (right == null)) || ((left != null) && (right != null));
		return (left == null) && (right == null);
	}

	@Override
	public int compareTo(HuffmanNode a) {
		if (a.freq > this.freq)
			return -1;
		else if (a.freq < this.freq)
			return 1;
		else
			return 0;
	}

}
