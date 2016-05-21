package ca.afroman.packet;

import java.util.List;

import ca.afroman.network.IPConnectedPlayer;

public class PacketUpdatePlayerList extends Packet
{
	private String players = "";
	
	/**
	 * Designed to be sent from the <b>server</b> to the <b>client</b>.
	 * <p>
	 * Sends the client a list of all the connected player.
	 * 
	 * @param connections all the connected players' connections
	 */
	public PacketUpdatePlayerList(List<IPConnectedPlayer> connections)
	{
		super(PacketType.UPDATE_PLAYERLIST, true);
		
		for (IPConnectedPlayer connection : connections)
		{
			players += connection.getID() + "," + connection.getRole().ordinal() + "," + connection.getUsername() + ",";
		}
		
		// Removes the extra comma
		players = players.substring(0, players.length() - 1);
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + "," + id + Packet.SEPARATOR + players).getBytes();
	}
}
