package lexicalanalysis;

public class ErrorImpl {
	private Position posStart;
	private Position posEnd;
	private String errorName;
	private String details;
	
	ErrorImpl(Position posStart, Position posEnd, String errorName, String details) {
		this.posStart = posStart;
		this.posEnd = posEnd;
		this.errorName = errorName;
		this.details = details;
	}
	
	public String toString() {
		String result = errorName + ": " + details + " at ";
		result += "line " + (posStart.getLn() + 1) + "\n";
		return result;
	}	
}
