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
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.player.Role;

public class ClientSocketManager implements IDynamicRunning
{
	private static ThreadGroup newDefaultThreadGroupInstance()
	{
		return new ThreadGroup(ClientGame.instance().getThreadGroup(), "Socket");
	}
	
	public static final boolean TRACE_PACKETS = true;
	
	private List<ConnectedPlayer> playerList;
	
	private IPConnectedPlayer serverConnection;
	private DatagramSocket socket;
	
	private ClientSocketReceive rSocket;
	private ClientSocketSend sSocket;
	
	private ThreadGroup threadGroup;
	
	public ClientSocketManager()
	{
		threadGroup = newDefaultThreadGroupInstance();
		
		serverConnection = new IPConnectedPlayer(null, -1, -1, null, "");
		
		playerList = new ArrayList<ConnectedPlayer>();
		
		try
		{
			socket = new DatagramSocket();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		
		rSocket = new ClientSocketReceive(this);
		sSocket = new ClientSocketSend(this);
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
			e.printStackTrace();
			
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
	
	public IPConnectedPlayer getConnectedPlayer()
	{
		return serverConnection;
	}
	
	public ConnectedPlayer playerByRole(Role role)
	{
		for (ConnectedPlayer player : getConnectedPlayers())
		{
			if (player.getRole() == role) return player;
		}
		
		return null;
	}
	
	public ConnectedPlayer playerByID(int id)
	{
		for (ConnectedPlayer player : getConnectedPlayers())
		{
			if (player.getID() == id) return player;
		}
		
		return null;
	}
	
	/**
	 * @return a list of all the ConnectedPlayers, excluding this current player.
	 */
	public List<ConnectedPlayer> otherPlayers()
	{
		List<ConnectedPlayer> toReturn = new ArrayList<ConnectedPlayer>();
		
		for (ConnectedPlayer player : getConnectedPlayers())
		{
			if (player.getID() != getConnectedPlayer().getID()) toReturn.add(player);
		}
		
		return toReturn;
	}
	
	public List<ConnectedPlayer> getConnectedPlayers()
	{
		return playerList;
	}
	
	@Override
	public void startThis()
	{
		rSocket.startThis();
		sSocket.startThis();
	}
	
	public ClientSocketReceive receiver()
	{
		return rSocket;
	}
	
	public ClientSocketSend sender()
	{
		return sSocket;
	}
	
	@Override
	public void pauseThis()
	{
		rSocket.pauseThis();
		sSocket.pauseThis();
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
		getConnectedPlayer().setRole(playerByID(serverConnection.getID()).getRole());
	}
	
	public ThreadGroup getThreadGroup()
	{
		return threadGroup;
	}
}
