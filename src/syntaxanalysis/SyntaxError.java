package syntaxanalysis;

public class SyntaxError {
	private String errorName;
	private int ln;
	
	public SyntaxError(String errorName, int ln) {
		this.errorName = errorName;
		this.ln = ln + 1;
	}
	
	public String toString() {
		return "Error: " + errorName + " at line " + ln + "\n";
	}
}
