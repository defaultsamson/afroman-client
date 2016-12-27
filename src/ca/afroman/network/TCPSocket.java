package ca.afroman.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import ca.afroman.interfaces.IDynamicRunning;
import ca.afroman.resource.ServerClientObject;

public class TCPSocket extends ServerClientObject implements IDynamicRunning
{
	private Socket socket;
	private OutputStream output;
	private InputStream input;
	
	private boolean isStopped = false;
	
	public TCPSocket(Socket socket, boolean isServerSide)
	{
		super(isServerSide);
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
	
	@Override
	@Deprecated
	public void pauseThis()
	{
		
	}
	
	public byte[] receive() throws IOException
	{
		if (!isStopped)
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
			catch (SocketException e)
			{
				if (!isStopped) e.printStackTrace();
			}
			catch (Exception e)
			{
				if (!isStopped) e.printStackTrace();
			}
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
		if (!isStopped)
		{
			try
			{
				output.write(bytes.length);
				output.write(bytes);
			}
			catch (IOException e)
			{
				if (!isStopped) e.printStackTrace();
			}
			catch (Exception e)
			{
				if (!isStopped) e.printStackTrace();
			}
		}
	}
	
	@Override
	@Deprecated
	public void startThis()
	{
		
	}
	
	@Override
	public void stopThis()
	{
		isStopped = true;
		
		try
		{
			socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
