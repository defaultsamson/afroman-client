package ca.pixel.game.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import ca.pixel.game.Game;
import ca.pixel.game.entity.PlayerMPEntity;
import ca.pixel.game.network.packet.Packet;
import ca.pixel.game.network.packet.PacketLogin;
import ca.pixel.game.network.packet.PacketType;

public class GameServer extends Thread
{
	public static final String IPv4_LOCALHOST = "127.0.0.1";
	public static final int PORT = 2413;
	
	private Game game;
	private DatagramSocket socket;
	private List<PlayerMPEntity> connectedPlayers;
	
	public GameServer(Game game)
	{
		this.game = game;
		try
		{
			this.socket = new DatagramSocket(PORT);
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		
		connectedPlayers = new ArrayList<PlayerMPEntity>();
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
			
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
			
			// String message = new String(packet.getData());
			//
			// System.out.println("[SERVER] [RECIEVE] [" + packet.getAddress().getHostAddress() + ":" + packet.getPort() + "] " + message);
			//
			// if (message.trim().equals("ping"))
			// {
			// sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
			// }
		}
	}
	
	public void parsePacket(byte[] data, InetAddress address, int port)
	{
		PacketType type = Packet.readType(data);
		// String message = Packet.readContent(data);
		
		System.out.println("[SERVER] [RECIEVE] [" + address.getHostAddress() + ":" + port + "] " + type.toString());
		
		switch (type)
		{
			default:
			case INVALID:
				break;
			case LOGIN:
				PacketLogin packet = new PacketLogin(data);
				
				if (packet.getPassword().trim().equals(Game.instance().getPassword()))
				{
					System.out.println("[SERVER] Password accepted: " + packet.getPassword());
					
					PlayerMPEntity player = new PlayerMPEntity(game.blankLevel, 100, 120, 1, address, port);

					this.addConnection(player, packet);
				}
				else
				{
					System.out.println("[SERVER] Password denied: " + packet.getPassword());
					// TODO tell the client that they failed
				}
				break;
			case DISCONNECT:
				break;
		}
	}
	
	private void addConnection(PlayerMPEntity player, PacketLogin packet)
	{
		boolean alreadyConnected = false;
		
		// TODO check if they're already connected
		
		
		if (!alreadyConnected) this.connectedPlayers.add(player);
	}

	/**
	 * Sends data to a Client
	 * 
	 * @param data the data to send
	 * @param ipAddress the Client's IP address
	 * @param port the Client's port
	 */
	public void sendData(byte[] data, InetAddress ipAddress, int port)
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		
		System.out.println("[SERVER] [SEND] " + new String(data));
		
		try
		{
			socket.send(packet);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void sendDataToAllClients(byte[] data)
	{
		for (PlayerMPEntity player : connectedPlayers)
		{
			sendData(data, player.ipAddress, player.port);
		}
	}
}
