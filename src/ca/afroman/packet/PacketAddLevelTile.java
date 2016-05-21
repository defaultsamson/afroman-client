package ca.afroman.packet;

import ca.afroman.assets.AssetType;
import ca.afroman.entity.api.Entity;

public class PacketAddLevelTile extends Packet
{
	private int layer;
	private Entity entity;
	
	/**
	 * Designed to be sent from the <b>server</b> to the <b>client</b> to add a tile to a ClientLevel.
	 * <p>
	 * Designed to be sent from the <b>client</b> to the <b>server</b> to add a tile to the server's Level.
	 * 
	 * @param level the level to add the tile to.
	 */
	public PacketAddLevelTile(int layer, Entity entity)
	{
		super(PacketType.ADD_LEVEL_TILE, true);
		this.layer = layer;
		this.entity = entity;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + "," + id + Packet.SEPARATOR + entity.getID() + "," + entity.getLevel().getType().ordinal() + "," + layer + "," + (entity.getAssetType() != null ? entity.getAssetType().ordinal() : AssetType.INVALID.ordinal()) + "," + entity.getX() + "," + entity.getY() + "," + entity.getWidth() + "," + entity.getHeight() + (entity.hasHitbox() ? "," + entity.hitboxesAsSaveable() : "")).getBytes();
	}
}
