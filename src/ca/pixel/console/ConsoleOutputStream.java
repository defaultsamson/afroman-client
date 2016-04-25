package ca.pixel.console;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ConsoleOutputStream extends OutputStream
{
	private final JTextArea textArea;
	private final StringBuilder sb = new StringBuilder();
	
	public ConsoleOutputStream(final JTextArea textArea)
	{
		this.textArea = textArea;
	}
	
	@Override
	public void flush()
	{}
	
	@Override
	public void close()
	{}
	
	@Override
	public void write(int b) throws IOException
	{
		
		if (b == '\r') return;
		
		if (b == '\n')
		{
			final String text = sb.toString() + "\n";
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					textArea.append(text);
				}
			});
			sb.setLength(0);
			return;
		}
		
		sb.append((char) b);
	}
}
