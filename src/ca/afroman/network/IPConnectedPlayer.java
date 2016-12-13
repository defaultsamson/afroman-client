package ca.afroman.network;

import java.net.InetAddress;

import ca.afroman.game.Role;

public class IPConnectedPlayer extends ConnectedPlayer
{
	private IPConnection connection;
	
	/**
	 * A ConnectedPlayer with an assigned connection for managing IP and port for this player.
	 * 
	 * @param address the address of the player's connection
	 * @param port the port of the player's connection
	 * @param role the role of the player
	 * @param username the username of the player
	 */
	public IPConnectedPlayer(InetAddress address, int port, TCPSocketChannel tcpSocketChannel, short id, Role role, String username)
	{
		this(new IPConnection(address, port, tcpSocketChannel), id, role, username);
	}
	
	/**
	 * A ConnectedPlayer with an assigned connection for managing IP and port for this player.
	 * 
	 * @param address the address of the player's connection
	 * @param port the port of the player's connection
	 * @param role the role of the player
	 * @param username the username of the player
	 */
	public IPConnectedPlayer(IPConnection connection, short id, Role role, String username)
	{
		super(id, role, username);
		
		this.connection = connection;
	}
	
	/**
	 * @return this player's connection.
	 */
	public IPConnection getConnection()
	{
		return connection;
	}
}
