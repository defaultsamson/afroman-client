package ca.afroman.client;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.gui.GuiClickNotification;
import ca.afroman.gui.GuiJoinServer;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.interfaces.IDynamicRunning;
import ca.afroman.log.ALogType;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;

public class ClientSocketManager implements IDynamicRunning
{
	private static ThreadGroup threadGroup = null;
	
	public static ThreadGroup threadGroupInstance()
	{
		if (threadGroup == null)
		{
			threadGroup = new ThreadGroup(ClientGame.instance().getThreadGroup(), "Socket");
		}
		return threadGroup;
	}
	
	private List<ConnectedPlayer> playerList;
	
	private IPConnectedPlayer serverConnection;
	private DatagramSocket socket;
	
	private ClientSocketReceive rSocket;
	private ClientSocketSend sSocket;
	
	public ClientSocketManager()
	{
		serverConnection = new IPConnectedPlayer(null, -1, (short) -1, null, "");
		
		playerList = new ArrayList<ConnectedPlayer>();
		
		try
		{
			socket = new DatagramSocket();
		}
		catch (SocketException e)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "", e);
		}
		
		rSocket = new ClientSocketReceive(this);
		sSocket = new ClientSocketSend(this);
	}
	
	public List<ConnectedPlayer> getConnectedPlayers()
	{
		return playerList;
	}
	
	public IPConnectedPlayer getServerConnection()
	{
		return serverConnection;
	}
	
	/**
	 * @return a list of all the ConnectedPlayers, excluding this current player.
	 */
	public List<ConnectedPlayer> otherPlayers()
	{
		List<ConnectedPlayer> toReturn = new ArrayList<ConnectedPlayer>();
		
		for (ConnectedPlayer player : getConnectedPlayers())
		{
			if (player.getID() != getServerConnection().getID()) toReturn.add(player);
		}
		
		return toReturn;
	}
	
	@Override
	public void pauseThis()
	{
		rSocket.pauseThis();
		sSocket.pauseThis();
	}
	
	public ConnectedPlayer playerByID(int id)
	{
		for (ConnectedPlayer player : getConnectedPlayers())
		{
			if (player.getID() == id) return player;
		}
		
		return null;
	}
	
	public ConnectedPlayer playerByRole(Role role)
	{
		for (ConnectedPlayer player : getConnectedPlayers())
		{
			if (player.getRole() == role) return player;
		}
		
		return null;
	}
	
	public ClientSocketReceive receiver()
	{
		return rSocket;
	}
	
	public ClientSocketSend sender()
	{
		return sSocket;
	}
	
	public void setServerIP(String serverIpAddress, int port)
	{
		serverConnection.getConnection().setPort(port);
		
		if (serverIpAddress == null)
		{
			serverConnection.getConnection().setIPAddress(null);
			return;
		}
		
		InetAddress ip = null;
		
		try
		{
			ip = InetAddress.getByName(serverIpAddress);
		}
		catch (UnknownHostException e)
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "Couldn't resolve hostname", e);
			
			ClientGame.instance().setCurrentScreen(new GuiJoinServer(new GuiMainMenu()));
			new GuiClickNotification(ClientGame.instance().getCurrentScreen(), "UNKNOWN", "HOST");
			return;
		}
		
		IPConnection connection = serverConnection.getConnection();
		
		connection.setIPAddress(ip);
		socket().connect(connection.getIPAddress(), connection.getPort());
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
	
	public void updateConnectedPlayer(List<ConnectedPlayer> players)
	{
		playerList = players;
		getServerConnection().setRole(playerByID(serverConnection.getID()).getRole());
	}
}
