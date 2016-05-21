package ca.afroman.packet;

import ca.afroman.level.LevelType;

public class PacketRemoveLevelTileID extends Packet
{
	private int layer;
	private LevelType levelType;
	private int tileID;
	
	/**
	 * Designed to be sent from the <b>server</b> to a <b>client</b>.
	 * Removes a tile with the provided id from the level with the given levelType.
	 * 
	 * @param levelType the type of the level to remove the tile from
	 * @param id the ID of the tile
	 */
	public PacketRemoveLevelTileID(int layer, LevelType levelType, int tileID)
	{
		super(PacketType.REMOVE_LEVEL_TILE, true);
		this.layer = layer;
		this.levelType = levelType;
		this.tileID = tileID;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + "," + id + Packet.SEPARATOR + levelType.ordinal() + "," + layer + "," + tileID).getBytes();
	}
}
