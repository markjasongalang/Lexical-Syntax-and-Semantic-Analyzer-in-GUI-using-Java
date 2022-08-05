package guipanels;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import lexicalanalysis.LexerUtil;
import semanticanalysis.SemanticAnalyzer;
import syntaxanalysis.SyntaxAnalyzer;
import syntaxanalysis.SyntaxError;

public class CodeEditorPanel extends JPanel implements ActionListener {
	public JLabel codeEditorLabel;
	public JTextPane codeEditorArea;
	public JTextArea codeEditorLines;
	public JScrollPane codeEditorScroll;
	public JButton compileRunButton;
	public JButton saveButton;
	public JButton loadButton;
	public JButton undoButton;
	public JButton redoButton;
	
	private ConsolePanel cp;
	private LexerPanel lp;
	private TreeSet<String> distinctErrors;
	private UndoManager undoManager;
	
	public CodeEditorPanel(ConsolePanel cp, LexerPanel lp) {
		this.cp = cp;
		this.lp = lp;
		
		this.setBounds(0, 0, 600, 350);
		this.setLayout(null);
		
		codeEditorLabel = new JLabel("Code Editor");
		codeEditorLabel.setFont(new Font("ARIAL", Font.PLAIN, 15));
		codeEditorLabel.setBounds(20, 15, (int) codeEditorLabel.getText().length() * 10, 15);
		
		codeEditorArea = new JTextPane();
		codeEditorArea.setFont(new Font("ARIAL", Font.PLAIN, 14));
		
		codeEditorLines = new JTextArea("1");
		codeEditorLines.setBackground(new Color(204, 204, 204));
		codeEditorLines.setEditable(false);
		codeEditorLines.setFont(new Font("ARIAL", Font.BOLD, 14));
		codeEditorLines.setForeground(new Color(128, 128, 128));
		
		// Generates line numbers beside the code editor
		codeEditorArea.getDocument().addDocumentListener(new DocumentListener() {
			
			public String getText() {
				int caretPosition = codeEditorArea.getDocument().getLength();
				Element root = codeEditorArea.getDocument().getDefaultRootElement();
				String text = "1" + System.getProperty("line.separator");
				for(int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
					text += i + System.getProperty("line.separator");
				}
				return text;
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				codeEditorLines.setText(getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				codeEditorLines.setText(getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				codeEditorLines.setText(getText());
			}
			
		});
		
		// Syntax highlighting in code editor
		codeEditorArea.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				setSyntaxColor();
			}
			
		});
		
		// Undo button
	    undoButton = new JButton("Undo");
	    undoButton.setOpaque(true);
	    undoButton.setBorder(BorderFactory.createLineBorder(new Color(108, 142, 191), 1));
	    undoButton.setBounds(330, 322, 85, 20);
	    undoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    undoButton.addActionListener(this);
	    
	    // Redo button
	    redoButton = new JButton("Redo");
	    redoButton.setOpaque(true);
	    redoButton.setBorder(BorderFactory.createLineBorder(new Color(108, 142, 191), 1));
	    redoButton.setBounds(425, 322, 85, 20);
	    redoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    redoButton.addActionListener(this);
	    
	    undoButton.setEnabled(false);
	    redoButton.setEnabled(false);
	    
	    undoManager = new UndoManager();
	    
	    codeEditorArea.getDocument().addUndoableEditListener(new UndoableEditListener() {

			@Override
			public void undoableEditHappened(UndoableEditEvent e) {
				undoManager.addEdit(e.getEdit());
				updateButtons();
			}
	    });
	    
	    undoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					undoManager.undo();
				} catch (CannotRedoException cre) {
					cre.printStackTrace();
				}
				updateButtons();
			}
	    });
	    
	    redoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					undoManager.redo();
				} catch (CannotRedoException cre) {
					cre.printStackTrace();
				}
				updateButtons();
			}
	    });
		
		codeEditorScroll = new JScrollPane();
		
		// Places padding inside code editor
		codeEditorLines.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		codeEditorArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
		
		// Places black border around code editor
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		codeEditorScroll.setBorder(border);
		/*codeEditorScroll.setBorder(BorderFactory.createCompoundBorder(border,
								BorderFactory.createEmptyBorder(10, 10, 10, 10)));*/
		
		codeEditorScroll.setBounds(20, 35, 560, 280);
		codeEditorScroll.getViewport().add(codeEditorArea);
		codeEditorScroll.setRowHeaderView(codeEditorLines);
		
		// Compile and Run button
		compileRunButton = new JButton("Compile & Run");
		compileRunButton.setBackground(new Color(213, 232, 212));
		compileRunButton.setOpaque(true);
		compileRunButton.setBorder(BorderFactory.createLineBorder(new Color(130, 179, 102), 1));
		compileRunButton.setBounds(20, 322, 110, 20);
		compileRunButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		compileRunButton.addActionListener(this);
		
		// Save button
		saveButton = new JButton("Save");
		saveButton.setBackground(new Color(225, 213, 231));
		saveButton.setOpaque(true);
		saveButton.setBorder(BorderFactory.createLineBorder(new Color(150, 115, 166), 1));
		saveButton.setBounds(140, 322, 85, 20);
		saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		saveButton.addActionListener(this);
		
		// Load button
		loadButton = new JButton("Load");
		loadButton.setBackground(new Color(218, 232, 252));
		loadButton.setOpaque(true);
		loadButton.setBorder(BorderFactory.createLineBorder(new Color(108, 142, 191), 1));
		loadButton.setBounds(235, 322, 85, 20);
		loadButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		loadButton.addActionListener(this);
		
		this.add(codeEditorLabel);
		this.add(codeEditorScroll);
		this.add(compileRunButton);
		this.add(saveButton);
		this.add(loadButton);
		this.add(undoButton);
		this.add(redoButton);
	}
	
	public static boolean isKeyword(String word) {
		String[] keywords = new String[] {"var", "input", "output"};
		for (int i = 0; i < keywords.length; i++) {
			if (word.equals(keywords[i])) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		LexerUtil lexer = new LexerUtil(codeEditorArea.getText());
		
		if (e.getSource() == compileRunButton) {
			if (lexer.getError() != null) {
				cp.consoleArea.setText(String.valueOf(lexer.getError()));
			} else {
				SyntaxAnalyzer syn = new SyntaxAnalyzer(lexer.getResult());
				syn.checkSyntax();
				
				if (syn.getErrorList().size() > 0) {
					filterSyntaxErrors(syn.getErrorList());
					displaySyntaxErrors();
				} else {
					lp.setLexerData(lexer.getResult());
					
					SemanticAnalyzer sem = new SemanticAnalyzer(lexer.getResult(), cp);
					sem.processSemantics(true);
				}
			}
		} else if (e.getSource() == saveButton) {
			String fileName = JOptionPane.showInputDialog("What is the name of the file?");
			
			if (fileName != null) {
				try {
					FileWriter writer = new FileWriter(fileName + ".txt");
					writer.write(codeEditorArea.getText());
					writer.close();
					JOptionPane.showMessageDialog(this, "Saved successfully!", "Notification", JOptionPane.WARNING_MESSAGE);
				} catch (IOException ex) {
					cp.consoleArea.setText("Unable to save the file.");
				}
			}
		} else if (e.getSource() == loadButton) {
			JFileChooser fileChooser = new JFileChooser();
			int response = fileChooser.showOpenDialog(null);
			
			if (response == JFileChooser.APPROVE_OPTION) {
				try {
					File file = new File(fileChooser.getSelectedFile().getName());
					Scanner sc = new Scanner(file);
					
					String data = "";
					while (sc.hasNextLine()) {
						data += sc.nextLine() + "\n";
					}
					sc.close();
					
					codeEditorArea.setText(data);
					setSyntaxColor();
				} catch (FileNotFoundException ex) {
					cp.consoleArea.setText("File not found.");
				}
			}
		}
	}
	
	private void changeColor(int start, int end, Color c) {
		int selectedLength = end - start;
		StyledDocument style = codeEditorArea.getStyledDocument();
		
		AttributeSet oldSet = style.getCharacterElement(end - 1).getAttributes();
		StyleContext sc = StyleContext.getDefaultStyleContext();
		
		AttributeSet s = sc.addAttribute(oldSet, StyleConstants.Foreground, c);
		style.setCharacterAttributes(start, selectedLength, s, true);
	}
	
	private void setSyntaxColor() {
		String allCodes = codeEditorArea.getText();
		
		int len = allCodes.length();
		
		// Resets the whole text to black
		changeColor(0, len, Color.BLACK);
		
		int start = -1;
		int end = -1;
		String word = "";
		
		// Changes the color of the keywords
		for (int i = 0; i < len; i++) {
			if (isLowerCaseLetter(allCodes.charAt(i))) {
				if (start == -1) {
					start = i;
				}
				word += allCodes.charAt(i);
				
				if (i == len - 1) {
					if (i > 0) {
						end = i + 1;
					}
					
					if (isKeyword(word)) {
						changeColor(start, end, Color.BLUE);
					}
					
					start = end + 1;
					word = "";
				}
			} else if (!isLowerCaseLetter(allCodes.charAt(i))) {
				if (i > 0) {
					end = i;
				}
				
				if (isKeyword(word)) {
					changeColor(start, end, Color.BLUE);
				}
				
				start = end + 1;
				word = "";
			}
		}
		
		// Changes the color of the comments
		for (int i = 0; i < len - 1; i++) {
			if (allCodes.charAt(i) == '/' && allCodes.charAt(i+1) == '*') {
				int id = i + 2;
				while (id + 1 < len && allCodes.charAt(id) != '*' && allCodes.charAt(id+1) != '/') {
					id++;
				}
				
				int endCommentPos = -1;
				
				if (id + 1 < len && allCodes.charAt(id) == '*' && allCodes.charAt(id+1) == '/') {
					endCommentPos = id + 2;
				} else {
					endCommentPos = len;
				}
				
				changeColor(i, endCommentPos, Color.GREEN);
			}
		}
	}
	
	private void updateButtons() {
	    undoButton.setEnabled(undoManager.canUndo());
	    redoButton.setEnabled(undoManager.canRedo());
	}
	
	private void filterSyntaxErrors(ArrayList<SyntaxError> errList) {
		distinctErrors = new TreeSet<>();
		for (int i = 0; i < errList.size(); i++) {
			distinctErrors.add(errList.get(i).toString());
		}
	}
	
	private void displaySyntaxErrors() {
		cp.consoleArea.setText("");
		for (String error : distinctErrors) {
			cp.consoleArea.append(error);
		}
	}
	
	private boolean isLowerCaseLetter(char ch) {
		return ('a' <= ch && ch <= 'z');
	}
}
