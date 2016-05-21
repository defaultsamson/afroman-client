package ca.afroman.packet;

import ca.afroman.entity.ServerPlayerEntity;

public class PacketUpdatePlayerLocation extends Packet
{
	private ServerPlayerEntity entity;
	
	/**
	 * Designed to be sent from the <b>server</b> to the <b>client</b>.
	 * <p>
	 * Sets the location of an player.
	 * 
	 * @param the player. <b>WARNING: </b> only expecting a server side PLAYER.
	 */
	public PacketUpdatePlayerLocation(ServerPlayerEntity entity)
	{
		super(PacketType.SET_PLAYER_LOCATION, false);
		this.entity = entity;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + "," + id + Packet.SEPARATOR + entity.getRole().ordinal() + "," + entity.getDirection().ordinal() + "," + entity.getLastDirection().ordinal() + "," + entity.getX() + "," + entity.getY()).getBytes();
	}
}
