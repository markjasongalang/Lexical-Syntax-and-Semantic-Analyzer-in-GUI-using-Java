package lexicalanalysis;

public class IllegalCharError extends ErrorImpl {
	IllegalCharError(Position posStart, Position posEnd, String errorName, String details) {
		super(posStart, posEnd, errorName, details);
	}
}
