package guipanels;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.Border;

public class ConsolePanel extends JPanel {
	
	public JLabel consoleLabel;
	public JTextArea consoleArea;
	public JScrollPane consoleScroll;

	public ConsolePanel() {
		
		this.setBounds(0, 350, 600, 350);
		this.setLayout(null);
		
		consoleLabel = new JLabel("Console");
		consoleLabel.setFont(new Font("ARIAL", Font.PLAIN, 15));
		consoleLabel.setBounds(20, 15, (int) consoleLabel.getText().length() * 10, 15);
		
		consoleArea = new JTextArea();
		consoleArea.setFont(new Font("ARIAL", Font.PLAIN, 14));
		consoleArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		consoleScroll = new JScrollPane();
		consoleScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		consoleScroll.setBounds(20, 35, 560, 165);
		consoleScroll.getViewport().add(consoleArea);
		
		this.add(consoleLabel);
		this.add(consoleScroll);
		
	}
	
}
