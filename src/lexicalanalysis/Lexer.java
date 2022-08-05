package lexicalanalysis;
import java.util.ArrayList;
import guipanels.CodeEditorPanel;

public class Lexer {
	private String text;
	private ErrorImpl err;
	private Position pos;
	private char currentChar;
	private String letters;
	private Position posStart;
	private boolean unmatchedComment;
	
	public static final String DIGITS = "0123456789";
	public static final String TT_INT = "INT";
	public static final String TT_DECIMAL = "DECIMAL";
	public static final String TT_KEYWORD = "KEYWORD";
	public static final String TT_IDENTIFIER = "IDENTIFIER";
	public static final String TT_PLUS = "PLUS";
	public static final String TT_MINUS = "MINUS";
	public static final String TT_MUL = "MUL";
	public static final String TT_DIV = "DIV";
	public static final String TT_LPAREN = "LPAREN";
	public static final String TT_RPAREN = "RPAREN";
	public static final String TT_EQUAL = "EQUAL";
	public static final String TT_TERMINATE = "TERMINATE";
	
	Lexer(String text) {
		letters = "";
		for (int i = 0; i < 26; i++) {
			letters += (char) ('a' + i);
			letters += (char) ('A' + i);
		}
		
		this.text = text;
		pos = new Position(-1, 0, text);
		
		currentChar = '\u0000';
		advance();
	}
	
	public void advance() {
		pos.advance(currentChar, text.length());
		
		if (pos.getIdx() < text.length()) {
			currentChar = text.charAt(pos.getIdx());
		} else {
			currentChar = '\u0000';
		}
	}
	
	public ArrayList<Token> makeTokens() {
		ArrayList<Token> tokens = new ArrayList<>();
		
		unmatchedComment = false;
		while (currentChar != '\u0000' && err == null) {
			if (isLetter(currentChar) || currentChar == '_') {
				tokens.add(makeWord());
			} else if (isDigit(currentChar)) {
				tokens.add(makeNumber());
			} else {
				switch (currentChar) {
					case ' ': case '\t': case '\n': 
						advance();
						break;
					case ';':
						tokens.add(new Token(TT_TERMINATE, ";", pos.getLn()));
						advance();
						break;
					case '+':
						tokens.add(new Token(TT_PLUS, "+", pos.getLn()));
						advance();
						break;
					case '-':
						tokens.add(new Token(TT_MINUS, "-", pos.getLn()));
						advance();
						break;
					case '*':
						tokens.add(new Token(TT_MUL, "*", pos.getLn()));
						advance();
						break;
					case '/':
						// Checks for possible comments
						int start = pos.getIdx() + 1;
						if (start < text.length() && text.charAt(start) == '*') {
							int jumpIndex = -1;
							int newLines = 0;
							
							for (int i = start + 1; i + 1 < text.length(); i++) {
								if (text.charAt(i) == '\n') {
									newLines++;
								}
								if (text.charAt(i) == '*' && text.charAt(i+1) == '/') {
									jumpIndex = i + 1;
									break;
								}
							}
							
							if (jumpIndex != -1) {
								pos.setIdx(jumpIndex);
								pos.addLn(newLines);
							} else {
								unmatchedComment = true;
								generateError("No matching comment");
								return null;
							}
						} else {
							tokens.add(new Token(TT_DIV, "/", pos.getLn()));
						}
						
						advance();
						break;
					case '(':
						tokens.add(new Token(TT_LPAREN, "(", pos.getLn()));
						advance();
						break;
					case ')':
						tokens.add(new Token(TT_RPAREN, ")", pos.getLn()));
						advance();
						break;
					case '=':
						tokens.add(new Token(TT_EQUAL, "=", pos.getLn()));
						advance();
						break;
					default:
						generateError("Illegal Character");
						return null;
				}
			}
		}
		
		return tokens;
	}
	
	private Token makeWord() {
		String wordStr = "";
		
		while (currentChar != '\u0000' && (isLetter(currentChar) || currentChar == '_' || isDigit(currentChar))) {
			wordStr += currentChar;
			advance();
		}
		
		if (CodeEditorPanel.isKeyword(wordStr)) {
			return new Token(TT_KEYWORD, wordStr, pos.getLn());
		}
		
		return new Token(TT_IDENTIFIER, wordStr, pos.getLn());
	}
	
	private Token makeNumber() {
		String numStr = "";
		int dotCount = 0;
		
		while (currentChar != '\u0000' && (isDigit(currentChar) || currentChar == '.')) {
			if (currentChar == '.') {
				if (dotCount == 1) {
					break;
				}
				dotCount++;
				numStr += ".";
			} else {
				numStr += currentChar;
			}
			
			advance();
		}
		
		if (dotCount == 1) {
			return new Token(TT_DECIMAL, numStr, pos.getLn());	
		}
		
		return new Token(TT_INT, numStr, pos.getLn());
	}
	
	private void generateError(String errorName) {
		posStart = pos.copy();
		char ch = currentChar;
		advance();
		if (unmatchedComment) {
			err = new IllegalCharError(posStart, pos, errorName, "'" + ch + '*' + "'");
		} else {
			err = new IllegalCharError(posStart, pos, errorName, "'" + ch + "'");
		}
	}
	
	private boolean isLetter(char ch) {
		return letters.contains(String.valueOf(ch));
	}
	
	public static boolean isDigit(char ch) {
		return DIGITS.contains(String.valueOf(ch));
	}
	
	public static boolean isArithmeticOpToken(Token tk) {
		if (tk == null) {
			return false;
		}
		
		if (tk.getToken().equals(TT_PLUS)) {
			return true;
		}
		
		if (tk.getToken().equals(TT_MINUS)) {
			return true;
		}
		
		if (tk.getToken().equals(TT_MUL)) {
			return true;
		}
		
		if (tk.getToken().equals(TT_DIV)) {
			return true;
		}

		return false;
	}
	
	public static boolean isTerminateToken(Token tk) {
		if (tk == null) {
			return false;
		}
		
		return tk.getToken().equals(TT_TERMINATE);
	}
	
	public static boolean isEqualToken(Token tk) {
		if (tk == null) {
			return false;
		}
		
		return tk.getToken().equals(TT_EQUAL);
	}
	
	public static boolean isIdentifierToken(Token tk) {
		if (tk == null) {
			return false;
		}
		
		return tk.getToken().equals(TT_IDENTIFIER);
	}
	
	public static boolean isLeftParenToken(Token tk) {
		if (tk == null) {
			return false;
		}
		
		return tk.getToken().equals(TT_LPAREN);
	}
	
	public static boolean isRightParenToken(Token tk) {
		if (tk == null) {
			return false;
		}
		
		return tk.getToken().equals(TT_RPAREN);
	}
	
	public static boolean isKeywordToken(Token tk) {
		if (tk == null) {
			return false;
		}
		
		return tk.getToken().equals(TT_KEYWORD);
	}
	
	public static boolean isIntegerToken(Token tk) {
		if (tk == null) {
			return false;
		}
		
		return tk.getToken().equals(TT_INT);
	}
	
	public static boolean isDecimalToken(Token tk) {
		if (tk == null) {
			return false;
		}
		
		return tk.getToken().equals(TT_DECIMAL);
	}
	
	public static boolean isOutputKeyword(Token tk) {
		if (tk == null) {
			return false;
		}
		
		return tk.getLexeme().equals("output");
	}
	
	public static boolean isInputKeyword(Token tk) {
		if (tk == null) {
			return false;
		}
		
		return tk.getLexeme().equals("input");
	}
	
	public static boolean isVarKeyword(Token tk) {
		if (tk == null) {
			return false;
		}
		
		return tk.getLexeme().equals("var");
	}
	
	public ErrorImpl getError() {
		return err;
	}
}
