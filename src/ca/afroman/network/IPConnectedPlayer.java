package ca.afroman.network;

import java.net.InetAddress;

import ca.afroman.player.Role;

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
	public IPConnectedPlayer(InetAddress address, int port, Role role, String username)
	{
		super(role, username);
		
		connection = new IPConnection(address, port);
	}
	
	/**
	 * @return this player's connection.
	 */
	public IPConnection getConnection()
	{
		return connection;
	}
}
