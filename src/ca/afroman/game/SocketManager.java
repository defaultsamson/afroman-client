package ca.afroman.game;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.client.ClientGame;
import ca.afroman.client.Role;
import ca.afroman.entity.api.IServerClient;
import ca.afroman.gui.GuiClickNotification;
import ca.afroman.gui.GuiJoinServer;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.interfaces.IDynamicRunning;
import ca.afroman.log.ALogType;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.PacketAssignClientID;
import ca.afroman.packet.PacketUpdatePlayerList;
import ca.afroman.server.ServerGame;

public class SocketManager implements IDynamicRunning, IServerClient
{
	/**
	 * Returns a usable port. If the provided one is eligible then it will return it, otherwise it will return the default port.
	 * 
	 * @param port
	 * @return
	 */
	public static int validatedPort(int port)
	{
		return (!(port < 0 || port > 0xFFFF)) ? port : Game.DEFAULT_PORT;
	}
	
	/**
	 * Returns a usable port. If the provided one is eligible then it will return it, otherwise it will return the default port.
	 * 
	 * @param port
	 * @return
	 */
	public static int validatedPort(String port)
	{
		if (port.length() > 0)
		{
			try
			{
				return validatedPort(Integer.parseInt(port));
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		
		return Game.DEFAULT_PORT;
	}
	
	private ThreadGroup threadGroup = null;
	
	private Game game;
	
	private List<ConnectedPlayer> playerList;
	private IPConnection serverConnection;
	
	private DatagramSocket socket = null;
	private PacketReceiver rSocket = null;
	
	private PacketSender sSocket = null;
	
	public SocketManager(Game game)
	{
		this.game = game;
		
		playerList = new ArrayList<ConnectedPlayer>();
		
		serverConnection = new IPConnection(null, -1);
		
		// try
		// {
		// socket = new DatagramSocket();
		// }
		// catch (SocketException e)
		// {
		// game.logger().log(ALogType.CRITICAL, "", e);
		// }
		//
		// rSocket = new NetworkReceiver(this);
		// sSocket = new NetworkSender(this);
	}
	
	/**
	 * Sets up a IPConnectedPlayer for a new connection. Makes the player join the server.
	 * 
	 * @param connection the connection to set up for
	 * @param username the desired username
	 */
	public void addConnection(IPConnection connection, String username)
	{
		if (isServerSide())
		{
			// Gives player a default role based on what critical roles are still required
			Role role = (getPlayerConnection(Role.PLAYER1) == null ? Role.PLAYER1 : (getPlayerConnection(Role.PLAYER2) == null ? Role.PLAYER2 : Role.SPECTATOR));
			
			IPConnectedPlayer newConnection = new IPConnectedPlayer(connection.getIPAddress(), connection.getPort(), (short) ConnectedPlayer.getIDCounter().getNext(), role, username);
			playerList.add(newConnection);
			
			ServerGame.instance().addConnection(newConnection.getConnection());
			
			// Tells the newly added connection their ID
			sender().sendPacket(new PacketAssignClientID(newConnection.getID(), newConnection.getConnection()));
			
			updateClientsPlayerList();
		}
	}
	
	public List<ConnectedPlayer> getConnectedPlayers()
	{
		return playerList;
	}
	
	public Game getGame()
	{
		return game;
	}
	
	public ConnectedPlayer getPlayerConnection(int id)
	{
		for (ConnectedPlayer player : getConnectedPlayers())
		{
			if (player.getID() == id) return player;
		}
		
		return null;
	}
	
	public IPConnectedPlayer getPlayerConnection(IPConnection connection)
	{
		if (isServerSide())
		{
			for (ConnectedPlayer player : playerList)
			{
				if (player instanceof IPConnectedPlayer)
				{
					IPConnectedPlayer ipPlayer = (IPConnectedPlayer) player;
					
					// If the IP and port equal those that were specified, return the player
					if (ipPlayer.getConnection().equals(connection)) return ipPlayer;
				}
			}
		}
		return null;
	}
	
	public ConnectedPlayer getPlayerConnection(Role role)
	{
		for (ConnectedPlayer player : getConnectedPlayers())
		{
			if (player.getRole() == role) return player;
		}
		
		return null;
	}
	
	public ConnectedPlayer getPlayerConnection(String name)
	{
		for (ConnectedPlayer player : getConnectedPlayers())
		{
			if (player.getUsername().equals(name)) return player;
		}
		
		return null;
	}
	
	public IPConnection getServerConnection()
	{
		return serverConnection;
	}
	
	@Override
	public boolean isServerSide()
	{
		return game.isServerSide();
	}
	
	@Override
	public void pauseThis()
	{
		rSocket.pauseThis();
		sSocket.pauseThis();
	}
	
	public PacketReceiver receiver()
	{
		return rSocket;
	}
	
	public void removeConnection(IPConnectedPlayer connection)
	{
		if (isServerSide())
		{
			playerList.remove(connection);
			ServerGame.instance().removeConnection(connection.getConnection());
			
			updateClientsPlayerList();
		}
	}
	
	public PacketSender sender()
	{
		return sSocket;
	}
	
	public void setServerConnection(String serverIpAddress, int port)
	{
		port = validatedPort(port);
		
		serverConnection.setPort(port);
		
		if (serverIpAddress == null)
		{
			serverConnection.setIPAddress(null);
			return;
		}
		
		InetAddress ip = null;
		
		try
		{
			ip = InetAddress.getByName(serverIpAddress);
		}
		catch (UnknownHostException e)
		{
			game.logger().log(ALogType.CRITICAL, "Couldn't resolve hostname", e);
			
			if (!isServerSide())
			{
				ClientGame.instance().setCurrentScreen(new GuiJoinServer(new GuiMainMenu()));
				new GuiClickNotification(ClientGame.instance().getCurrentScreen(), "UNKNOWN", "HOST");
			}
			return;
		}
		
		serverConnection.setIPAddress(ip);
		
		if (isServerSide())
		{
			try
			{
				socket = new DatagramSocket(serverConnection.getPort());
			}
			catch (SocketException e)
			{
				game.logger().log(ALogType.CRITICAL, "Failed to create server DatagramSocket", e);
			}
		}
		else
		{
			try
			{
				socket = new DatagramSocket();
				socket.connect(serverConnection.getIPAddress(), serverConnection.getPort());
			}
			catch (SocketException e)
			{
				game.logger().log(ALogType.CRITICAL, "Failed to create client DatagramSocket", e);
			}
		}
		
		rSocket = new PacketReceiver(this);
		sSocket = new PacketSender(this);
		
		rSocket.startThis();
		sSocket.startThis();
	}
	
	public DatagramSocket socket()
	{
		return socket;
	}
	
	@Override
	public void startThis()
	{
		rSocket.startThis();
		sSocket.startThis();
	}
	
	@Override
	public void stopThis()
	{
		socket.close();
		playerList.clear();
		rSocket.stopThis();
		sSocket.stopThis();
	}
	
	public ThreadGroup threadGroupInstance()
	{
		if (threadGroup == null)
		{
			if (isServerSide())
			{
				threadGroup = new ThreadGroup(ServerGame.instance().getThreadGroup(), "Socket");
			}
			else
			{
				threadGroup = new ThreadGroup(ClientGame.instance().getThreadGroup(), "Socket");
			}
		}
		return threadGroup;
	}
	
	/**
	 * Updates the player list for all the connected clients.
	 * <p>
	 * For server use.
	 */
	public void updateClientsPlayerList()
	{
		if (isServerSide())
		{
			sender().sendPacketToAllClients(new PacketUpdatePlayerList(playerList));
		}
	}
	
	/**
	 * For client use.
	 * 
	 * @param players
	 */
	public void updateConnectedPlayers(List<ConnectedPlayer> players)
	{
		if (!isServerSide())
		{
			playerList = players;
			ClientGame.instance().setRole(getPlayerConnection(ClientGame.instance().getID()).getRole());
		}
	}
}
