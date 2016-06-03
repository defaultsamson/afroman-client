package ca.afroman.packet;

import ca.afroman.client.Role;

public class PacketMovePlayer extends Packet
{
	private Role player;
	private int x;
	private int y;
	
	/**
	 * Designed to be sent from the <b>client</b> to the <b>server</b>.
	 * <p>
	 * Requests to have a player move.
	 * 
	 */
	public PacketMovePlayer(Role player, int xa, int ya)
	{
		super(PacketType.REQUEST_PLAYER_MOVE, false);
		this.player = player;
		this.x = xa;
		this.y = ya;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + "," + id + Packet.SEPARATOR + player.ordinal() + "," + this.x + "," + this.y).getBytes();
	}
}
