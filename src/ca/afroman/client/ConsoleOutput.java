package ca.afroman.client;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

@SuppressWarnings("serial")
public class ConsoleOutput extends JPanel
{
	public static ConsoleOutput console = null;
	
	private JFrame frame;
	private JTextArea textArea;
	
	public static ConsoleOutput instance()
	{
		if (console == null)
		{
			console = new ConsoleOutput();
		}
		return console;
	}
	
	public JTextArea getTextArea()
	{
		return textArea;
	}
	
	public ConsoleOutput()
	{
		frame = new JFrame("CONSOLE");
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		textArea = new JTextArea(20, 80);
		setToAutoScroll();
		
		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		setLayout(new BorderLayout());
		add(scrollPane);
		
		frame.getContentPane().add(this);
		frame.pack();
	}
	
	public void setToAutoScroll()
	{
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}
	
	public void setGuiVisible(boolean show)
	{
		frame.setVisible(show);
		
		if (show)
		{
			setToAutoScroll();
		}
	}
	
	public void showGui()
	{
		setGuiVisible(true);
	}
	
	public void hideGui()
	{
		setGuiVisible(false);
	}
}
