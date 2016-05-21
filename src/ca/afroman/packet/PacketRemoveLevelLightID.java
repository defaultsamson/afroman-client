package ca.afroman.packet;

import ca.afroman.level.LevelType;

public class PacketRemoveLevelLightID extends Packet
{
	private LevelType levelType;
	private int lightID;
	
	/**
	 * Designed to be sent from the <b>server</b> to a <b>client</b>.
	 * Removes a tile with the provided id from the level with the given levelType.
	 * 
	 * @param levelType the type of the level to remove the tile from
	 * @param id the ID of the tile
	 */
	public PacketRemoveLevelLightID(LevelType levelType, int lightID)
	{
		super(PacketType.REMOVE_LEVEL_POINTLIGHT, true);
		this.levelType = levelType;
		this.lightID = lightID;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + "," + id + Packet.SEPARATOR + levelType.ordinal() + "," + lightID).getBytes();
	}
}
