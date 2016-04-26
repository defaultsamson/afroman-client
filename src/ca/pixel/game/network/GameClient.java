package ca.pixel.game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import ca.pixel.game.Game;

public class GameClient extends Thread
{
	private InetAddress serverIP;
	private DatagramSocket socket;
	private Game game;
	
	public GameClient(Game game, String serverIpAddress)
	{
		this.game = game;
		try
		{
			this.socket = new DatagramSocket();
			this.serverIP = InetAddress.getByName(serverIpAddress);
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			byte[] buffer = new byte[1024];
			
			// Loads up the buffer with incoming data
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			
			try
			{
				socket.receive(packet);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			System.out.println("[CLIENT] [RECIEVE] " + new String(packet.getData()));
		}
	}
	
	public void sendData (byte[] data)
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, serverIP, GameServer.PORT);
		
		System.out.println("[CLIENT] [SEND] " + new String(data));
		
		try
		{
			socket.send(packet);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
}
