package guipanels;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import lexicalanalysis.Lexer;
import lexicalanalysis.Token;

public class LexerPanel extends JPanel {
	private String data[][];
	private String column[];
	
	public JLabel lexerLabel;
	public DefaultTableModel lexerModel;
	public JTable lexerTable;
	public JScrollPane lexerScroll;
	
	public LexerPanel() {
		this.setBounds(600, 0, 300, 600);
		this.setLayout(null);
		
		lexerLabel = new JLabel("Lexical Analyzer");
		lexerLabel.setFont(new Font("ARIAL", Font.PLAIN, 15));
		lexerLabel.setBounds(20, 15, (int) lexerLabel.getText().length() * 10, 15);
		
		lexerModel = new DefaultTableModel();
		
		lexerTable = new JTable(lexerModel);
		lexerTable.setEnabled(false);
		
		lexerModel.addColumn("Lexeme");
		lexerModel.addColumn("Token");
		
		lexerScroll = new JScrollPane(lexerTable);
		lexerScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		lexerScroll.setBounds(20, 35, 250, 515);
		
		this.add(lexerLabel);
		this.add(lexerScroll);
	}
	
	public void setLexerData(ArrayList<Token> tokens) {
		// Reset table
		lexerModel.setRowCount(0);
		
		// Fill table with new data
		for (Token tk : tokens) {
			if (Lexer.isTerminateToken(tk)) {
				continue;
			}
			lexerModel.addRow(new Object[] {tk.getLexeme(), tk.getToken()});
		}
	}
}
