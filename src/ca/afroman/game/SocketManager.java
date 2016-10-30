package ca.afroman.game;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.client.ClientGame;
import ca.afroman.entity.api.IServerClient;
import ca.afroman.gui.GuiClickNotification;
import ca.afroman.gui.GuiJoinServer;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.interfaces.IDynamicRunning;
import ca.afroman.log.ALogType;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.network.TCPSocket;
import ca.afroman.option.Options;
import ca.afroman.packet.PacketAssignClientID;
import ca.afroman.packet.PacketUpdatePlayerList;
import ca.afroman.server.ServerGame;
import ca.afroman.util.IPUtil;

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
	
	private ServerSocket welcomeSocket = null;
	private DatagramSocket socket = null;
	private PacketReceiver rSocket = null;
	private PacketSender sSocket = null;
	
	private List<TCPReceiver> tcpSockets = null;
	
	public SocketManager(Game game)
	{
		this.game = game;
		
		playerList = new ArrayList<ConnectedPlayer>();
		
		serverConnection = new IPConnection(null, -1, null);
		
		tcpSockets = new ArrayList<TCPReceiver>();
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
			
			short id = (short) ConnectedPlayer.getIDCounter().getNext();
			
			IPConnectedPlayer newConnection = new IPConnectedPlayer(connection, id, role, username);
			playerList.add(newConnection);
			
			// Tells the newly added connection their ID
			sender().sendPacket(new PacketAssignClientID(newConnection.getID(), newConnection.getConnection()));
			
			updateClientsPlayerList();
			
			if (isServerSide())
			{
				try
				{
					Socket clientTCP = welcomeSocket().accept();
					TCPSocket tcp = new TCPSocket(clientTCP);
					newConnection.getConnection().setTCPSocket(tcp);
					
					synchronized (tcpSockets)
					{
						TCPReceiver rec = new TCPReceiver(this, tcp);
						tcpSockets.add(rec);
						rec.startThis();
					}
				}
				catch (IOException e)
				{
					ServerGame.instance().logger().log(ALogType.WARNING, "Failed to accept connection from the welcome socket", e);
				}
			}
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
	
	public IPConnectedPlayer getPlayerConnection(InetAddress address, int port)
	{
		if (isServerSide())
		{
			for (ConnectedPlayer player : playerList)
			{
				if (player instanceof IPConnectedPlayer)
				{
					IPConnectedPlayer ipPlayer = (IPConnectedPlayer) player;
					
					if (ipPlayer.getConnection() != null)
					{
						// If the IP and port equal those that were specified, return the player
						if (IPUtil.equals(address, port, ipPlayer.getConnection())) return ipPlayer;
					}
				}
			}
		}
		return null;
	}
	
	public ConnectedPlayer getPlayerConnection(int id)
	{
		for (ConnectedPlayer player : getConnectedPlayers())
		{
			if (player.getID() == id) return player;
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
	
	public boolean hasActiveServerConnection()
	{
		return sSocket != null;
	}
	
	public void initServerTCPConnection()
	{
		if (!isServerSide())
		{
			try
			{
				Socket clientSocket = new Socket(getServerConnection().getIPAddress(), getServerConnection().getPort());
				TCPSocket sock = new TCPSocket(clientSocket);
				getServerConnection().setTCPSocket(sock);
				
				TCPReceiver thread = new TCPReceiver(this, sock);
				synchronized (tcpSockets)
				{
					tcpSockets.add(thread);
				}
				thread.startThis();
			}
			catch (UnknownHostException e)
			{
				game.logger().log(ALogType.WARNING, "Unkonwn host while setting up client TCP connection", e);
			}
			catch (IOException e)
			{
				game.logger().log(ALogType.WARNING, "IOException while setting up client TCP connection", e);
			}
		}
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
			synchronized (tcpSockets)
			{
				int index = -1;
				
				for (int i = 0; i < tcpSockets.size(); i++)
				{
					TCPReceiver rec = tcpSockets.get(i);
					if (rec.getTCPSocket() == connection.getConnection().getTCPSocket())
					{
						index = i;
						break;
					}
				}
				
				if (index != -1)
				{
					tcpSockets.get(index).stopThis();
					tcpSockets.remove(index);
				}
			}
			
			if (connection.getConnection().getTCPSocket() != null)
			{
				try
				{
					connection.getConnection().getTCPSocket().getSocket().close();
				}
				catch (IOException e)
				{
					ServerGame.instance().logger().log(ALogType.WARNING, "Error while closing TCP socket", e);
				}
			}
			
			playerList.remove(connection);
			
			ConnectedPlayer.getIDCounter().reset();
			// shifts everyone's ID
			for (ConnectedPlayer player : getConnectedPlayers())
			{
				if (player instanceof IPConnectedPlayer)
				{
					IPConnectedPlayer cPlayer = (IPConnectedPlayer) player;
					
					cPlayer.setID((short) ConnectedPlayer.getIDCounter().getNext());
					
					sender().sendPacket(new PacketAssignClientID(cPlayer.getID(), cPlayer.getConnection()));
				}
				else
				{
					ServerGame.instance().logger().log(ALogType.CRITICAL, "There shouldn't be a non-IPConnectedPlayer in the server's SocketManager");
				}
			}
			
			updateClientsPlayerList();
		}
	}
	
	public PacketSender sender()
	{
		return sSocket;
	}
	
	public boolean setServerConnection(String serverIpAddress, int port)
	{
		port = validatedPort(port);
		
		serverConnection.setPort(port);
		
		if (serverIpAddress == null)
		{
			serverConnection.setIPAddress(null);
			return false;
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
				new GuiClickNotification(ClientGame.instance().getCurrentScreen(), -1, "UNKNOWN", "HOST");
			}
			return false;
		}
		
		serverConnection.setIPAddress(ip);
		
		if (isServerSide())
		{
			Options.instance().serverPort = "" + port;
			
			try
			{
				welcomeSocket = new ServerSocket(serverConnection.getPort());
				welcomeSocket.setSoTimeout(15000);// TODO make gui to display that it's waiting?
			}
			catch (IOException e)
			{
				game.logger().log(ALogType.CRITICAL, "Failed to create server ServerSocket", e);
			}
			
			try
			{
				socket = new DatagramSocket(serverConnection.getPort());
			}
			catch (SocketException e)
			{
				game.logger().log(ALogType.CRITICAL, "Failed to create server DatagramSocket", e);
				
				return false;
			}
		}
		else
		{
			Options.instance().clientPort = "" + port;
			
			try
			{
				socket = new DatagramSocket();
				socket.connect(serverConnection.getIPAddress(), serverConnection.getPort());
			}
			catch (SocketException e)
			{
				game.logger().log(ALogType.CRITICAL, "Failed to create client DatagramSocket", e);
				return false;
			}
		}
		
		rSocket = new PacketReceiver(this);
		sSocket = new PacketSender(this);
		
		rSocket.startThis();
		sSocket.startThis();
		
		return true;
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
		try
		{
			if (welcomeSocket != null) welcomeSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		synchronized (tcpSockets)
		{
			for (TCPReceiver tcp : tcpSockets)
			{
				tcp.stopThis();
			}
			tcpSockets.clear();
		}
		
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
	
	public ServerSocket welcomeSocket()
	{
		return welcomeSocket;
	}
}
