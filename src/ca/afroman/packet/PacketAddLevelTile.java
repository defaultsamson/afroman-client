package ca.afroman.packet;

import ca.afroman.entity.Entity;

public class PacketAddLevelTile extends Packet
{
	private Entity entity;
	
	/**
	 * Designed to be sent from the <b>server</b> to the <b>client</b> to add a tile to a ClientLevel.
	 * <p>
	 * Designed to be sent from the <b>client</b> to the <b>server</b> to add a tile to the server's Level.
	 * 
	 * @param level the level to add the tile to.
	 */
	public PacketAddLevelTile(Entity entity)
	{
		super(PacketType.ADD_LEVEL_TILE);
		this.entity = entity;
	}
	
	@Override
	public byte[] getData()
	{ // (type, leveltype, assetType, x, y, width, height, hitboxes)
		return (type.ordinal() + Packet.SEPARATOR + entity.getID() + "," + entity.getLevel().getType().ordinal() + "," + entity.getAssetType().ordinal() + "," + entity.getX() + "," + entity.getY() + "," + entity.getWidth() + "," + entity.getHeight() + (entity.hasHitbox() ? "," + entity.hitboxesAsSaveable() : "")).getBytes();
	}
}
