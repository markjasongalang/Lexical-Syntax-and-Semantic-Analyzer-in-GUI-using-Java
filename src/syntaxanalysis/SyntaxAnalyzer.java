package syntaxanalysis;

import java.util.ArrayList;

import lexicalanalysis.Lexer;
import lexicalanalysis.Token;

public class SyntaxAnalyzer {
	private ArrayList<Token> tokens;
	private ArrayList<SyntaxError> errList;
	private int sz;
	private Token previousToken;
	private Token nextToken;
	private Token currentToken;
	private boolean valid;
	
	public SyntaxAnalyzer(ArrayList<Token> tokens) {
		this.tokens = tokens;
		sz = tokens.size();
	}
	
	public void checkSyntax() {
		errList = new ArrayList<>();
		
		checkIdentifier();
		checkKeyword();
		checkEqualSign();
		checkNumber();
		checkArithmeticOp();
		checkLeftParen();
		checkRightParen();
		checkTerminate();
		checkBalancedParen();
	}
	
	private void setProperTokens(int i) {
		currentToken = tokens.get(i);
		
		if (i - 1 >= 0) {
			previousToken = tokens.get(i-1);
		} else {
			previousToken = null;
		}
		
		if (i + 1 < sz) {
			nextToken = tokens.get(i+1);
		} else {
			nextToken = null;
		}
	}
	
	private void addErrorToList(String errorName, int ln) {
		errList.add(new SyntaxError(errorName, ln));
	}
	
	private boolean isUninitIdentifier(ArrayList<String> initIdentifiers, int i) {
		if (!initIdentifiers.contains(tokens.get(i).getLexeme())) {
			addErrorToList("Uninitialized variable", tokens.get(i).getLn());
			return true;
		}
		
		return false;
	}
	
	private void checkIdentifier() {
		ArrayList<String> declaredIdentifiers = new ArrayList<>();
		ArrayList<String> initIdentifiers = new ArrayList<>();
		
		for (int i = 0; i < sz; i++) {
			setProperTokens(i);
			
			if (Lexer.isIdentifierToken(currentToken)) {
				if (!declaredIdentifiers.contains(currentToken.getLexeme())) {
					valid = false;
					if (previousToken != null && Lexer.isDigit(previousToken.getLexeme().charAt(0))) {
						errList.add(new SyntaxError("Invalid identifier name", currentToken.getLn()));
						continue;
					}
					
					if (previousToken != null && Lexer.isVarKeyword(previousToken)) {
						valid = true;
						declaredIdentifiers.add(tokens.get(i).getLexeme());
					}
					
					if (!valid) {
						addErrorToList("Variable not yet declared", currentToken.getLn());
					}
					
					valid = false;
						
					if (Lexer.isTerminateToken(nextToken)) {
						valid = true;
					}
					
					if (Lexer.isEqualToken(nextToken)) {
						initIdentifiers.add(currentToken.getLexeme());
						valid = true;
					}
					
					if (!valid) {
						addErrorToList("Missing ; or =", currentToken.getLn());
					}
				} else {
					valid = false;
						
					if (Lexer.isVarKeyword(previousToken)) {
						addErrorToList("Variable already declared", currentToken.getLn());
						continue;
					}
					
					if (Lexer.isTerminateToken(previousToken)) {
						if (Lexer.isEqualToken(nextToken)) {
							initIdentifiers.add(currentToken.getLexeme());
							valid = true;
						}
					
					} else if (Lexer.isInputKeyword(previousToken)) {
						if (Lexer.isTerminateToken(nextToken)) {
							initIdentifiers.add(currentToken.getLexeme());
							valid = true;
						}
					} else if (Lexer.isOutputKeyword(previousToken)) {
						if (isUninitIdentifier(initIdentifiers, i)) {
							continue;
						}
				
						if (Lexer.isTerminateToken(nextToken)) {
							valid = true;
						}
						
						if (Lexer.isArithmeticOpToken(nextToken)) {
							valid = true;
						}
					} else if (Lexer.isEqualToken(previousToken)) {
						if (isUninitIdentifier(initIdentifiers, i)) {
							continue;
						}
						
						if (Lexer.isArithmeticOpToken(nextToken)) {
							valid = true;
						}
						
						if (Lexer.isTerminateToken(nextToken)) {
							valid = true;
						}
					} else if (Lexer.isArithmeticOpToken(previousToken)) {
						if (isUninitIdentifier(initIdentifiers, i)) {
							continue;
						}
						
						if (Lexer.isArithmeticOpToken(nextToken)) {
							valid = true;
						}
						
						if (Lexer.isTerminateToken(nextToken)) {
							valid = true;
						}
						
						if (Lexer.isRightParenToken(nextToken)) {
							valid = true;
						}
					} else if (Lexer.isLeftParenToken(previousToken)) {
						if (isUninitIdentifier(initIdentifiers, i)) {
							continue;
						}
						
						if (Lexer.isRightParenToken(nextToken)) {
							valid = true;
						}
						
						if (Lexer.isArithmeticOpToken(nextToken)) {
							valid = true;
						}
					}
					
					if (!valid) {
						addErrorToList("Invalid syntax", currentToken.getLn());
					}
				}
			}
		}
	}
	
	private void checkKeyword() {
		for (int i = 0; i < sz; i++) {
			setProperTokens(i);
			
			if (Lexer.isKeywordToken(currentToken)) {
				valid = false;
				
				if (Lexer.isOutputKeyword(currentToken)) {
					if (Lexer.isTerminateToken(previousToken) || i == 0) {
						if (Lexer.isIdentifierToken(nextToken)) {
							valid = true;
						}
						
						if (Lexer.isLeftParenToken(nextToken)) {
							valid = true;
						}
						
						if (Lexer.isIntegerToken(nextToken) || Lexer.isDecimalToken(nextToken)) {
							valid = true;
						}
					}
				} else if (Lexer.isInputKeyword(currentToken) || Lexer.isVarKeyword(currentToken)) {
					if (Lexer.isTerminateToken(previousToken) || i == 0) {
						if (Lexer.isIdentifierToken(nextToken)) {
							valid = true;
						}
					}
				}
				
				if (!valid) {
					addErrorToList("Invalid use of keyword", currentToken.getLn());
				}
			}
		}
	}
	
	private void checkEqualSign() {
		for (int i = 0; i < sz; i++) {
			setProperTokens(i);
			
			if (Lexer.isEqualToken(currentToken)) {
				valid = false;
				
				if (Lexer.isIdentifierToken(previousToken)) {
					if (nextToken.getToken().equals(Lexer.TT_MINUS)) {
						valid = true;
					}
					
					if (Lexer.isIntegerToken(nextToken) || Lexer.isDecimalToken(nextToken)) {
						valid = true;
					}
					
					if (Lexer.isIdentifierToken(nextToken)) {
						valid = true;
					}
					
					if (Lexer.isLeftParenToken(nextToken)) {
						valid = true;
					}
				}
				
				if (!valid) {
					addErrorToList("Invalid assignment", currentToken.getLn());
				}
			}
		}
	}
	
	private void checkNumber() {
		for (int i = 0; i < sz; i++) {
			setProperTokens(i);
			
			if (Lexer.isIntegerToken(currentToken) || Lexer.isDecimalToken(currentToken)) {
				valid = false;
				
				if (Lexer.isEqualToken(previousToken) || Lexer.isOutputKeyword(previousToken)) {
					if (Lexer.isArithmeticOpToken(nextToken)) {
						valid = true;
					}
					
					if (Lexer.isTerminateToken(nextToken)) {
						valid = true;
					}
				}
				
				if (Lexer.isLeftParenToken(previousToken) || Lexer.isArithmeticOpToken(previousToken)) {
					if (Lexer.isArithmeticOpToken(nextToken)) {
						valid = true;
					}
					
					if (Lexer.isRightParenToken(nextToken)) {
						valid = true;
					}
				}
				
				if (Lexer.isArithmeticOpToken(previousToken)) {
					if (Lexer.isTerminateToken(nextToken)) {
						valid = true;
					}
				}
				
				if (!valid) {
					addErrorToList("Invalid expression", currentToken.getLn());
				}
			}
		}
	}
	
	private void checkArithmeticOp() {
		for (int i = 0; i < sz; i++) {
			setProperTokens(i);
			
			if (Lexer.isArithmeticOpToken(currentToken)) {
				valid = false;
				
				if (tokens.get(i).getToken().equals(Lexer.TT_MINUS) && (Lexer.isEqualToken(previousToken) || 
					Lexer.isArithmeticOpToken(previousToken))) {
					valid = true;
					continue;
				}
				
				if (Lexer.isRightParenToken(previousToken) || Lexer.isIntegerToken(previousToken) || 
					Lexer.isIdentifierToken(previousToken) || Lexer.isDecimalToken(previousToken) ||
					Lexer.isLeftParenToken(previousToken)) {
					
					if (Lexer.isLeftParenToken(nextToken) || Lexer.isIntegerToken(nextToken) || 
						Lexer.isIdentifierToken(nextToken) || nextToken.getToken().equals(Lexer.TT_MINUS) ||
						Lexer.isDecimalToken(nextToken)) {
						
						valid = true;
					}
				}
				
				if (!valid) {
					addErrorToList("Invalid expression", currentToken.getLn());
				}
			}
		}
	}
	
	private void checkLeftParen() {
		for (int i = 0; i < sz; i++) {
			setProperTokens(i);
			
			if (Lexer.isLeftParenToken(currentToken)) {
				valid = false;
				
				if (Lexer.isEqualToken(previousToken) || Lexer.isArithmeticOpToken(previousToken) ||
					Lexer.isOutputKeyword(previousToken) || Lexer.isLeftParenToken(previousToken)) {
					
					if (Lexer.isIdentifierToken(nextToken) || Lexer.isIntegerToken(nextToken) || 
						Lexer.isDecimalToken(nextToken) || Lexer.isLeftParenToken(nextToken) ||
						nextToken.getToken().equals(Lexer.TT_MINUS)) {
						
						valid = true;
					}
				}
				
				if (!valid) {
					addErrorToList("Invalid expression", currentToken.getLn());
				}
			}
		}
	}
	
	private void checkRightParen() {
		for (int i = 0; i < sz; i++) {
			setProperTokens(i);
			
			if (Lexer.isRightParenToken(currentToken)) {
				valid = false;
				
				if (Lexer.isIdentifierToken(previousToken) || Lexer.isIntegerToken(previousToken) ||
					Lexer.isRightParenToken(previousToken) || Lexer.isDecimalToken(previousToken)) {
					
					if (Lexer.isTerminateToken(nextToken) || Lexer.isArithmeticOpToken(nextToken) ||
						Lexer.isRightParenToken(nextToken)) {
						
						valid = true;
					}
				}
				
				if (!valid) {
					addErrorToList("Invalid expression", tokens.get(i).getLn());
				}
			}
		}
	}
	
	private void checkTerminate() {
		for (int i = 0; i < sz; i++) {
			setProperTokens(i);
			
			if (Lexer.isTerminateToken(currentToken)) {
				valid = false;
				
				if (Lexer.isIdentifierToken(previousToken) || Lexer.isRightParenToken(previousToken) ||
					Lexer.isIntegerToken(previousToken) || Lexer.isTerminateToken(previousToken) || 
					Lexer.isDecimalToken(previousToken)) {
					
					if (Lexer.isKeywordToken(nextToken) || Lexer.isIdentifierToken(nextToken) ||
						Lexer.isTerminateToken(nextToken) ||
						i == sz - 1) {
						
						valid = true;
					}
				}
				
				if (!valid) {
					addErrorToList("Invalid syntax", currentToken.getLn());
				}
			}
		}
	}
	
	private void checkBalancedParen() {
		int counter = 0;
		int leftParenPos = -1;
		
		for (int i = 0; i < sz; i++) {
			setProperTokens(i);
			
			if (Lexer.isLeftParenToken(currentToken)) {
				leftParenPos = currentToken.getLn();
				counter++;
			}
			
			if (Lexer.isRightParenToken(currentToken)) {
				counter--;
			}
			
			if (counter < 0) {
				addErrorToList("Parenthesis not balanced", currentToken.getLn());
			}
		}
		
		if (counter > 0) {
			addErrorToList("Parenthesis not balanced", leftParenPos);
		}
	}
	
	public ArrayList<SyntaxError> getErrorList() {
		return errList;
	}
}
