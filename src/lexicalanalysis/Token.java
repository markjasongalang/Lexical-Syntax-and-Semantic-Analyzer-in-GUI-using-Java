package lexicalanalysis;

public class Token {
	private String token;
	private String lexeme;
	private int ln;
	
	Token(String token, String lexeme, int ln) {
		this.token = token;
		this.lexeme = lexeme;
		this.ln = ln;
	}
	
	Token(String token, int lexeme) {
		this.token = token;
		this.lexeme = String.valueOf(lexeme);
	}
	
	Token(String token, double lexeme) {
		this.token = token;
		this.lexeme = String.valueOf(lexeme);
	}
	
	public String getToken() {
		return token;
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	public int getLn() {
		return ln;
	}
	
	public String toString() {
		return token + ": " + lexeme;
	}
}
