package ca.afroman.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.Game;
import ca.afroman.entity.Role;
import ca.afroman.gui.GuiClickNotification;
import ca.afroman.gui.GuiConnectToServer;
import ca.afroman.gui.GuiJoinServer;
import ca.afroman.gui.GuiLobby;
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
	private List<ConnectedPlayer> playerList;
	
	public GameClient()
	{
		playerList = new ArrayList<ConnectedPlayer>();
		
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
			
			this.parsePacket(packet.getData(), new IPConnection(packet.getAddress(), packet.getPort()));
		}
	}
	
	public void parsePacket(byte[] data, IPConnection connection)
	{
		PacketType type = Packet.readType(data);
		
		System.out.println("[CLIENT] [RECIEVE] [" + connection.getIPAddress().getHostAddress() + ":" + connection.getPort() + "] " + type.toString());
		
		switch (type)
		{
			default:
			case INVALID:
				System.out.println("INVALID PACKET");
				break;
			case REQUEST_PASSWORD:
				// Game.instance().setPassword("INVALID PASSWORD");
				Game.instance().setCurrentScreen(new GuiJoinServer(new GuiMainMenu()));
				new GuiClickNotification(Game.instance().getCurrentScreen(), "INVALID", "PASSWORD");
				break;
			case ASSIDN_CLIENTID:
				id = Integer.parseInt(Packet.readContent(data));
				break;
			case UPDATE_PLAYERLIST:
			{
				String[] split = Packet.readContent(data).split(",");
				
				List<ConnectedPlayer> players = new ArrayList<ConnectedPlayer>();
				for (int i = 0; i < split.length; i += 3)
				{
					players.add(new ConnectedPlayer(Integer.parseInt(split[i]), Role.fromOrdinal(Integer.parseInt(split[i + 1])), split[i + 2]));
				}
				
				this.playerList = players;
				
				if (Game.instance().getCurrentScreen() instanceof GuiConnectToServer)
				{
					Game.instance().setCurrentScreen(new GuiLobby(null));
				}
			}
				break;
		}
	}
	
	public int getID()
	{
		return id;
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
		
		System.out.println("[CLIENT] [SEND] [" + this.serverIP + ":" + GameServer.PORT + "] " + new String(data));
		
		try
		{
			socket.send(packet);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public ConnectedPlayer thisPlayer()
	{
		return playerByID(id);
	}
	
	public ConnectedPlayer playerByID(int id)
	{
		System.out.println("Looking For ID: " + id);
		
		for (ConnectedPlayer player : playerList)
		{
			System.out.println("ID Found: " + player.getID());
			
			if (player.getID() == id) return player;
		}
		
		return null;
	}
	
	/**
	 * @return a list of all the ConnectedPlayers, exclusing this current player.
	 */
	public List<ConnectedPlayer> otherPlayers()
	{
		List<ConnectedPlayer> toReturn = new ArrayList<ConnectedPlayer>();
		
		for (ConnectedPlayer player : playerList)
		{
			if (player.getID() != id) toReturn.add(player);
		}
		
		return toReturn;
	}
	
	public List<ConnectedPlayer> getPlayers()
	{
		return playerList;
	}
}
