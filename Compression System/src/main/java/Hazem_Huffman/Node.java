package Hazem_Huffman;

public class Node implements Comparable<Node> {
	private String hCode;
	private HuffmanNode val;

	public Node(String hCode, HuffmanNode val) {
		super();
		this.hCode = hCode;
		this.val = val;
	}

	public String gethCode() {
		return hCode;
	}

	public void sethCode(String hCode) {
		this.hCode = hCode;
	}

	public HuffmanNode getVal() {
		return val;
	}

	public void setVal(HuffmanNode val) {
		this.val = val;
	}

	@Override
	public int compareTo(Node a) {
		// TODO Auto-generated method stub
		if (this.val.getVal() > a.val.getVal())
			return 1;
		if (this.val.getVal() < a.val.getVal())
			return -1;
		else
			return 0;
	}

}
