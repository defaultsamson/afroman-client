package ca.afroman.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MultiOutputStream extends OutputStream
{
	private List<OutputStream> streams;
	
	public MultiOutputStream(OutputStream... streams)
	{
		this.streams = new ArrayList<OutputStream>();
		
		for (OutputStream out : streams)
		{
			if (out != null) this.streams.add(out);
		}
	}
	
	public List<OutputStream> getStreams()
	{
		return streams;
	}
	
	@Override
	public void write(int b) throws IOException
	{
		for (OutputStream stream : streams)
		{
			stream.write(b);
		}
	}
	
	@Override
	public void write(byte[] b) throws IOException
	{
		for (OutputStream stream : streams)
		{
			stream.write(b);
		}
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		for (OutputStream stream : streams)
		{
			stream.write(b, off, len);
		}
	}
	
	@Override
	public void flush() throws IOException
	{
		for (OutputStream stream : streams)
		{
			stream.flush();
		}
	}
	
	@Override
	public void close() throws IOException
	{
		for (OutputStream stream : streams)
		{
			stream.close();
		}
	}
}
