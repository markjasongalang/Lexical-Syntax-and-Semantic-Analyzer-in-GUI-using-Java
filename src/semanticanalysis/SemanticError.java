package semanticanalysis;

public class SemanticError {
	private String errorName;
	private int ln;
	
	public SemanticError(String errorName, int ln) {
		this.errorName = errorName;
		this.ln = ln + 1;
	}
	
	public String toString() {
		return "Error: " + errorName + " at line " + ln + "\n";
	}
}
