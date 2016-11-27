package ca.afroman.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPSocket
{
	private Socket socket;
	private OutputStream output;
	private InputStream input;
	
	public TCPSocket(Socket socket)
	{
		this.socket = socket;
		
		try
		{
			output = socket.getOutputStream();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			input = socket.getInputStream();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Socket getSocket()
	{
		return socket;
	}
	
	public byte[] receive() throws IOException
	{
		try
		{
			int count = input.read();
			
			if (count < 0)
			{
				return new byte[0];
			}
			else
			{
				byte[] bytes = new byte[count];
				
				input.read(bytes);
				
				return bytes;
			}
		}
		catch (Exception e)
		{
			// TODO this is hidden
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Sends bytes using this TCP Socket's stream.
	 * 
	 * @param bytes
	 */
	public void sendData(byte[] bytes)
	{
		try
		{
			output.write(bytes.length);
			output.write(bytes);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
