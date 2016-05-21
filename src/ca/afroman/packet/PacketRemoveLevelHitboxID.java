package ca.afroman.packet;

import ca.afroman.level.LevelType;

public class PacketRemoveLevelHitboxID extends Packet
{
	private LevelType levelType;
	private int hitboxID;
	
	/**
	 * Designed to be sent from the <b>server</b> to a <b>client</b>.
	 * Removes a tile with the provided id from the level with the given levelType.
	 * 
	 * @param levelType the type of the level to remove the tile from
	 * @param id the ID of the tile
	 */
	public PacketRemoveLevelHitboxID(LevelType levelType, int hitboxID)
	{
		super(PacketType.REMOVE_LEVEL_HITBOX, true);
		this.levelType = levelType;
		this.hitboxID = hitboxID;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + "," + id + Packet.SEPARATOR + levelType.ordinal() + "," + hitboxID).getBytes();
	}
}
