package ca.afroman.packet;

import ca.afroman.server.ServerSocket;

public class PacketRequestConnection extends Packet
{
	private String username;
	private String password;
	
	/**
	 * Designed to be sent from the host's <b>client</b> to the <b>server</b>.
	 * <p>
	 * Requests the server to allow a player to join.
	 * 
	 * @param username the username of the player being sent
	 */
	public PacketRequestConnection(String username)
	{
		this(username, "");
	}
	
	/**
	 * Designed to be sent from the host's <b>client</b> to the <b>server</b>.
	 * <p>
	 * Requests the server to allow a player to join.
	 * 
	 * @param username the username of the player being sent
	 * @param password the password being sent for the server
	 */
	public PacketRequestConnection(String username, String password)
	{
		super(PacketType.REQUEST_CONNECTION);
		this.username = username;
		this.password = password;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + Packet.SEPARATOR + username + "," + password + "," + ServerSocket.GAME_VERSION).getBytes();
	}
}
