package lexicalanalysis;
import java.util.ArrayList;

public class LexerUtil {
	private ArrayList<Token> tokens;
	private ErrorImpl err;
	
	public LexerUtil(String text) {	
		Lexer l = new Lexer(text);
		tokens = l.makeTokens();
		err = l.getError();
	}
	
	public ArrayList<Token> getResult() {
		return tokens;
	}
	
	public ErrorImpl getError() {
		return err;
	}
}
