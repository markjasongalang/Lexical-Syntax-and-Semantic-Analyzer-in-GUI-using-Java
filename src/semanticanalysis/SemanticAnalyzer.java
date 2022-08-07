package semanticanalysis;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import lexicalanalysis.Lexer;
import lexicalanalysis.Token;
import guipanels.ConsolePanel;

public class SemanticAnalyzer {
	private ArrayList<Token> tokens;
	private Token currentToken;
	private Token previousToken;
	private Token nextToken;
	private Map<String, String> mp;
	private ConsolePanel cp;
	private int sz, i;
	private boolean invalid;
	private ArrayList<SemanticError> semErrList;
	
	public SemanticAnalyzer(ArrayList<Token> tokens, ConsolePanel cp) {
		this.tokens = tokens;
		this.sz = tokens.size();
		this.cp = cp;
		this.i = 0;
		
		cp.consoleArea.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					processInputKeyword();
					processSemantics(false);
				}
			}
		});
	}
	
	private void setTokens(int i) {
		currentToken = tokens.get(i);
		
		if (i - 1 >= 0) {
			previousToken = tokens.get(i-1);
		}
		
		if (i + 1 < sz) {
			nextToken = tokens.get(i+1);
		}
	}
	
	private boolean isDigit(char ch) {
		return ('0' <= ch && ch <= '9');
	}
	
	private double applyOp(char op, double b, double a) {
		switch (op) {
			case '+':
				return a + b;
			case '-':
				return a - b;
			case '*':
				return a * b;
			case '/':
				if (b == 0) {
					semErrList.add(new SemanticError("Cannot divie by zero", currentToken.getLn()));
					return 0;
				}
				return a / b;
		}
		return 0;
	}
	
	private boolean isOperator(char ch) {
		return "+-*/".contains(String.valueOf(ch));
	}
	
	private boolean hasPrecedence(char op1, char op2) {
		if (op2 == '(' || op2 == ')') {
			return false;
		}
		
		if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
			return false;
		}
		
		return true;
	}
	
	private String evalExp(String expression) {
		char[] exp = expression.toCharArray();
		Stack<Double> values = new Stack<>();
		Stack<Character> ops = new Stack<>();
		
		boolean negative = false;
		invalid = false;
		for (int i = 0; i < exp.length; i++) {
			if (exp[i] == ' ') {
				continue;
			}
			
			if ((i == 0 && exp[i] == '-') || (i - 1 >= 0 && exp[i] == '-' && isOperator(exp[i-1]) ||
				(i - 1 >= 0 && exp[i] == '-' && exp[i-1] == '('))) {
				if (negative) {
					semErrList.add(new SemanticError("Invalid expression", currentToken.getLn()));
					invalid = true;
				}
				negative = true;
				continue;
			}
			
			if (isDigit(exp[i])) {
				StringBuffer sbuf = new StringBuffer();
				while (i < exp.length && (isDigit(exp[i]) || exp[i] == '.')) {
					sbuf.append(exp[i++]);
				}
				
				double digitExtracted = Double.parseDouble(sbuf.toString());
				if (negative) {
					digitExtracted *= -1;
					negative = false;
				}
				values.push(digitExtracted);
				i--;
			} else if (exp[i] == '(') {
				ops.push(exp[i]);
			} else if (exp[i] == ')' && values.size() > 1) {
				while (ops.peek() != '(') {
					values.push(applyOp(ops.pop(), values.pop(), values.pop()));
				}
				
				ops.pop();
			} else if (isOperator(exp[i])) {
				while (!ops.empty() && hasPrecedence(exp[i], ops.peek())) {
					values.push(applyOp(ops.pop(), values.pop(), values.pop()));
				}
				
				ops.push(exp[i]);
			}
		}
		
		while (!ops.empty() && values.size() > 1) {
			values.push(applyOp(ops.pop(), values.pop(), values.pop()));
		}
		
		return String.valueOf(values.pop());
	}
	
	private void processInputKeyword() {
		char[] console = cp.consoleArea.getText().toCharArray();
		int index = console.length - 2;
		String value = "";
		while (index >= 0 && console[index] != '\n') {
			value += console[index];
			index--;
		}
		
		String reversedValue = "";
		for (int id = value.length() - 1; id >= 0; id--) {
			reversedValue += value.charAt(id);
		}
		
		mp.put(nextToken.getLexeme(), reversedValue);
		i++;
	}
	
	public void processSemantics(boolean reset) {
		if (reset) {
			semErrList = new ArrayList<>();
			mp = new HashMap<>();
			cp.consoleArea.setText("");
		}
		
		while (i < sz) {
			if (invalid) {
				displaySemanticErrors();
				break;
			}
			
			setTokens(i);
			
			if (Lexer.isEqualToken(currentToken)) {
				int id = i + 1;
				
				String exp = "";
				while (id < sz && !Lexer.isTerminateToken(tokens.get(id))) {
					String token = tokens.get(id).getLexeme();
					if (Lexer.isIdentifierToken(tokens.get(id))) {
						if (!mp.containsKey(tokens.get(id).getLexeme())) {
							semErrList.add(new SemanticError("Uninitialized variable", tokens.get(id).getLn()));
							invalid = true;
						} else {
							exp += mp.get(token);
						}
					} else {
						exp += token;
					}
					id++;
				}
				
				if (invalid) {
					continue;
				}
				
				mp.put(previousToken.getLexeme(), evalExp(exp));
			} else if (Lexer.isOutputKeyword(currentToken)) {
				int id = i + 1;
				
				String exp = "";
				while (id < sz && !Lexer.isTerminateToken(tokens.get(id))) {
					if (Lexer.isIdentifierToken(tokens.get(id))) {
						exp += mp.get(tokens.get(id).getLexeme());
					} else {
						exp += tokens.get(id).getLexeme();
					}
					id++;
				}
				
				cp.consoleArea.append(evalExp(exp) + "\n");
			} else if (Lexer.isInputKeyword(currentToken)) {
				break;
			}
			
			if (semErrList.size() > 0) {
				displaySemanticErrors();
				break;
			}
			
			i++;
		}
	}
	
	private void displaySemanticErrors() {
		cp.consoleArea.setText("");
		for (SemanticError err : semErrList) {
			cp.consoleArea.append(err.toString());
		}
	}
}
