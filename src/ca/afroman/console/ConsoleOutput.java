package ca.afroman.console;

import java.awt.BorderLayout;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ConsoleOutput extends JPanel
{
	private static final long serialVersionUID = -4401360933188428855L;
	public static final JFrame frame = new JFrame("CONSOLE");
	private JTextArea textArea = new JTextArea(30, 40);
	private ConsoleOutputStream taOutputStream = new ConsoleOutputStream(textArea);
	
	public ConsoleOutput()
	{
		JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setAutoscrolls(true);
		
		setLayout(new BorderLayout());
		add(scrollPane);
		System.setOut(new PrintStream(taOutputStream));
		
	}
	
	public static void setGuiVisible(boolean show)
	{
		frame.setVisible(show);
	}
	
	public static void showGui()
	{
		frame.setVisible(true);
	}
	
	public static void hideGui()
	{
		frame.setVisible(false);
	}
	
	public static void createGui()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				frame.getContentPane().add(new ConsoleOutput());
				frame.pack();
				// frame.setLocationRelativeTo(null);
				frame.setFocusable(false);
			}
		});
	}
}
