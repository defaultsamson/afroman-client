package ca.afroman.packet;

import ca.afroman.level.LevelType;

public class PacketRemoveLevelTileLocation extends Packet
{
	private LevelType levelType;
	private double x;
	private double y;
	
	/**
	 * Designed to be sent from the <b>client</b> to the <b>server</b>.
	 * Removes a tile from the level with the given levelType at the given coordinates.
	 * 
	 * @param levelType the type of the level to remove the tile from
	 * @param x the x ordinate of the tile location
	 * @param y the y ordinate of the tile location
	 */
	public PacketRemoveLevelTileLocation(LevelType levelType, double x, double y)
	{
		super(PacketType.REMOVE_LEVEL_TILE);
		this.levelType = levelType;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + Packet.SEPARATOR + levelType.ordinal() + "," + x + "," + y).getBytes();
	}
}
