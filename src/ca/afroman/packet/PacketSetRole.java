package ca.afroman.packet;

import ca.afroman.player.Role;

public class PacketSetRole extends Packet
{
	private int playerID;
	private Role role;
	
	/**
	 * Designed to be sent from the host's <b>client</b> to the <b>server</b>.
	 * <p>
	 * Requests that the player with the given ID be assigned the provided role.
	 */
	public PacketSetRole(int playerID, Role role)
	{
		super(PacketType.SETROLE);
		this.playerID = playerID;
		this.role = role;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + Packet.SEPARATOR + playerID + "," + role.ordinal()).getBytes();
	}
}
