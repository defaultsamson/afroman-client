package ca.afroman.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import ca.afroman.game.SocketManager;
import ca.afroman.resource.ServerClientObject;

public class TCPSocket extends ServerClientObject
{
	private SocketManager manager;
	
	private Socket socket;
	private OutputStream output;
	private InputStream input;
	
	public TCPSocket(Socket socket, SocketManager manager)
	{
		super(manager.isServerSide());
		this.socket = socket;
		this.manager = manager;
		
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
		catch (SocketException e)
		{
			// // The connection was lost
			// if (isServerSide())
			// {
			// manager.removeConnection(manager.getPlayerConnection(socket.getInetAddress(), socket.getPort()));
			//
			// // TODO If it was a required player that left, pause/stop the game
			// }
			// else
			// {
			// ClientGame.instance().exitFromGame(ExitGameReason.CONNECTION_LOST);
			// }
			if (!manager.isStopping()) e.printStackTrace();
		}
		catch (Exception e)
		{
			if (!manager.isStopping()) e.printStackTrace();
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
			if (!manager.isStopping()) e.printStackTrace();
		}
		catch (Exception e)
		{
			if (!manager.isStopping()) e.printStackTrace();
		}
	}
}
