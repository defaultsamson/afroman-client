package ca.afroman.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import ca.afroman.Game;
import ca.afroman.gui.GuiJoinServer;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.packet.Packet;
import ca.afroman.packet.PacketType;
import ca.afroman.server.GameServer;

public class GameClient extends Thread
{
	public static int id = -1;
	private InetAddress serverIP;
	private DatagramSocket socket;
	// private Game game;
	
	public GameClient()
	{
		try
		{
			this.socket = new DatagramSocket();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setServerIP(String serverIpAddress)
	{
		try
		{
			this.serverIP = InetAddress.getByName(serverIpAddress);
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
			
			this.parsePacket(packet.getData(), new Connection(packet.getAddress(), packet.getPort()));
		}
	}
	
	public void parsePacket(byte[] data, Connection connection)
	{
		PacketType type = Packet.readType(data);
		// String message = Packet.readContent(data);
		
		System.out.println("[CLIENT] [RECIEVE] [" + connection.toString() + "] " + type.toString());
		
		switch (type)
		{
			default:
			case INVALID:
				System.out.println("INVALID PACKET");
				break;
			case REQUEST_PASSWORD:
			{
				GuiJoinServer.passwordText = "INVALID PASSWORD";
				Game.instance().setCurrentScreen(new GuiJoinServer(Game.instance(), new GuiMainMenu(Game.instance())));
			}
				break;
			case ASSIDN_CLIENTID:
				id = Integer.parseInt(Packet.readContent(data));
				break;
			// case THIS_PLAYER_JOIN:
			// {
			// PacketThisPlayerJoin packet = new PacketThisPlayerJoin(data);
			// PlayerMPEntity joined = new PlayerMPEntity(packet.getX(), packet.getY(), 1, Game.instance().input, address, port);
			// }
			// break;
			// case PLAYER_JOIN:
			// PacketPlayerJoin packet = new PacketPlayerJoin(data);
			//
			// PlayerMPEntity joined = new PlayerMPEntity(packet.getX(), packet.getY(), 1, address, port);
			//
			// // PacketLogin packet = new PacketLogin(data);
			// //
			// // if (packet.getPassword().trim().equals(Game.instance().getPassword()))
			// // {
			// // System.out.println("[SERVER] Password accepted: " + packet.getPassword());
			// //
			// // PlayerMPEntity player = new PlayerMPEntity(game.blankLevel, 100, 120, 1, address, port);
			// // }
			// // else
			// // {
			// // System.out.println("[SERVER] Password denied: " + packet.getPassword());
			// // // TODO tell the client that they failed
			// // }
			// break;
			// case DISCONNECT:
			// break;
		}
	}
	
	/**
	 * Sends a packet to the server.
	 * 
	 * @param packet the packet
	 */
	public void sendPacket(Packet packet)
	{
		sendData(packet.getData());
	}
	
	/**
	 * Sends a byte array of data to the server.
	 * 
	 * @param data the data
	 * 
	 * @deprecated Still works to send raw data, but sendPacket() is preferred.
	 */
	@Deprecated
	public void sendData(byte[] data)
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
