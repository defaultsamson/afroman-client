package ca.afroman.packet;

import ca.afroman.level.LevelType;

public class PacketRemoveLevelTileID extends Packet
{
	private int layer;
	private LevelType levelType;
	private int id;
	
	/**
	 * Designed to be sent from the <b>server</b> to a <b>client</b>.
	 * Removes a tile with the provided id from the level with the given levelType.
	 * 
	 * @param levelType the type of the level to remove the tile from
	 * @param id the ID of the tile
	 */
	public PacketRemoveLevelTileID(int layer, LevelType levelType, int id)
	{
		super(PacketType.REMOVE_LEVEL_TILE);
		this.layer = layer;
		this.levelType = levelType;
		this.id = id;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + Packet.SEPARATOR + levelType.ordinal() + "," + layer + "," + id).getBytes();
	}
}
