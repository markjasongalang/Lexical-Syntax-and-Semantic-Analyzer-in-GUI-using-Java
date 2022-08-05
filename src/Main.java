import javax.swing.JFrame;
import guipanels.CodeEditorPanel;
import guipanels.ConsolePanel;
import guipanels.LexerPanel;

public class Main {
	private JFrame frame;
	
	Main() {
		frame = new JFrame("Lexical, Syntax, and Semantic Analyzer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(900, 600);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setLayout(null);
		
		ConsolePanel cp = new ConsolePanel(); 
		LexerPanel lp = new LexerPanel();
		CodeEditorPanel cep = new CodeEditorPanel(cp, lp);
		
		frame.add(cep);
		frame.add(lp);
		frame.add(cp);
		
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		new Main();
	}
}
