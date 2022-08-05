package lexicalanalysis;

public class Position {
	private int idx;
	private int ln;
	private String ftxt;
	
	Position(int idx, int ln, String ftxt) {
		this.idx = idx;
		this.ln = ln;
		this.ftxt = ftxt;
	}
	
	public void setIdx(int newIndex) {
		idx = newIndex;
	}
	
	public int getIdx() {
		return idx;
	}
	
	public void addLn(int newLn) {
		ln += newLn;
	}
	
	public int getLn() {
		return ln;
	}
	
	public void advance(char currentChar, int len) {
		idx++;
		if (currentChar == '\n') {
			ln++;
		}
	}
	
	public Position copy() {
		return new Position(idx, ln, ftxt);
	}
}
