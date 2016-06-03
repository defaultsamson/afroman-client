package ca.afroman.server;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.client.Role;
import ca.afroman.interfaces.IDynamicRunning;
import ca.afroman.log.ALogType;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.PacketAssignClientID;
import ca.afroman.packet.PacketStopServer;
import ca.afroman.packet.PacketUpdatePlayerList;

public class ServerSocketManager implements IDynamicRunning
{
	public static final String IPv4_LOCALHOST = "127.0.0.1";
	public static final int PORT = 2413;
	public static final int MAX_PLAYERS = 8;
	
	private static ThreadGroup thread = null;
	
	public static ThreadGroup threadGroupInstance()
	{
		if (thread == null)
		{
			thread = new ThreadGroup(ServerGame.instance().getThreadGroup(), "Socket");
		}
		return thread;
	}
	
	private List<IPConnectedPlayer> playerList;
	private DatagramSocket socket;
	
	private ServerSocketReceive rSocket;
	private ServerSocketSend sSocket;
	
	public ServerSocketManager(String password)
	{
		playerList = new ArrayList<IPConnectedPlayer>();
		
		try
		{
			this.socket = new DatagramSocket(PORT);
		}
		catch (SocketException e)
		{
			ServerGame.instance().logger().log(ALogType.CRITICAL, "Server already running on this IP and PORT.", e);
		}
		
		rSocket = new ServerSocketReceive(this, password);
		sSocket = new ServerSocketSend(this);
	}
	
	public DatagramSocket socket()
	{
		return socket;
	}
	
	/**
	 * @return all the client connections.
	 */
	public List<IPConnectedPlayer> clientConnections()
	{
		return playerList;
	}
	
	public List<IPConnectedPlayer> getConnectedPlayers()
	{
		return playerList;
	}
	
	/**
	 * Gets a player by their connection (which contains their IP address and port).
	 * 
	 * @param connection the connection
	 * @return the connected player.
	 */
	public IPConnectedPlayer getPlayerByConnection(IPConnection connection)
	{
		for (IPConnectedPlayer player : playerList)
		{
			// If the IP and port equal those that were specified, return the player
			if (player.getConnection().equals(connection)) return player;
		}
		return null;
	}
	
	/**
	 * Gets the first player with the given role.
	 * 
	 * @param role the role
	 * @return the player.
	 */
	public IPConnectedPlayer getPlayerByRole(Role role)
	{
		for (IPConnectedPlayer player : playerList)
		{
			if (player.getRole() == role) return player;
		}
		return null;
	}
	
	/**
	 * Gets a player by their username.
	 * 
	 * @param username the username
	 * @return the player.
	 */
	public IPConnectedPlayer getPlayerByUsername(String username)
	{
		for (IPConnectedPlayer player : playerList)
		{
			if (player.getUsername().equals(username)) return player;
		}
		return null;
	}
	
	/**
	 * Gets a player by their ID number.
	 * 
	 * @param id the ID number
	 * @return the player.
	 */
	public IPConnectedPlayer getPlayerByID(int id)
	{
		for (IPConnectedPlayer player : playerList)
		{
			if (player.getID() == id) return player;
		}
		return null;
	}
	
	/**
	 * Removes a player's IPConnectedPlayer for a leaving connection. Makes the player disconnect from the server.
	 * 
	 * @param connection the connection to remove.
	 */
	public void removeConnection(IPConnectedPlayer connection)
	{
		playerList.remove(connection);
		receiver().removeConnection(connection.getConnection());
		sender().removeConnection(connection.getConnection());
		
		updateClientsPlayerList();
	}
	
	/**
	 * Sets up a IPConnectedPlayer for a new connection. Makes the player join the server.
	 * 
	 * @param connection the connection to set up for
	 * @param username the desired username
	 */
	public void addConnection(IPConnection connection, String username)
	{
		// Gives player a default role based on what critical roles are still required
		Role role = (this.getPlayerByRole(Role.PLAYER1) == null ? Role.PLAYER1 : (this.getPlayerByRole(Role.PLAYER2) == null ? Role.PLAYER2 : Role.SPECTATOR));
		
		IPConnectedPlayer newConnection = new IPConnectedPlayer(connection.getIPAddress(), connection.getPort(), ConnectedPlayer.getNextAvailableID(), role, username);
		playerList.add(newConnection);
		
		receiver().addConnection(newConnection.getConnection());
		sender().addConnection(newConnection.getConnection());
		
		// Tells the newly added connection their ID
		sender().sendPacket(new PacketAssignClientID(newConnection.getID()), newConnection.getConnection());
		
		updateClientsPlayerList();
	}
	
	/**
	 * Updates the player list for all the connected clients.
	 */
	public void updateClientsPlayerList()
	{
		// Sends all the connections the updated list
		PacketUpdatePlayerList updateList = new PacketUpdatePlayerList(playerList);
		for (IPConnectedPlayer con : playerList)
		{
			sender().sendPacket(updateList, con.getConnection());
		}
	}
	
	@Override
	public void startThis()
	{
		rSocket.startThis();
		sSocket.startThis();
	}
	
	public ServerSocketReceive receiver()
	{
		return rSocket;
	}
	
	public ServerSocketSend sender()
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
		// Tell all clients that the server stopped
		sender().sendPacketToAllClients(new PacketStopServer());
		
		try
		{
			// An improper way to ensure that all players receive the PacketStopServer before it closes.
			// TODO make a more proper way than just a sleep?
			Thread.sleep(1500);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		socket.close();
		playerList.clear();
		rSocket.stopThis();
		sSocket.stopThis();
	}
}
