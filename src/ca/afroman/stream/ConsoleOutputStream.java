package ca.afroman.stream;

import java.io.IOException;
import java.io.OutputStream;

import ca.afroman.client.ConsoleOutput;

public class ConsoleOutputStream extends OutputStream
{
	private final StringBuilder sb = new StringBuilder();
	
	public ConsoleOutputStream()
	{}
	
	@Override
	public void write(int b) throws IOException
	{
		
		if (b == '\r') return;
		
		if (b == '\n')
		{
			final String text = sb.toString() + "\n";
			ConsoleOutput.instance().getTextArea().append(text);
			sb.setLength(0);
			return;
		}
		
		sb.append((char) b);
	}
}
