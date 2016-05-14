package ca.afroman.packet;

import ca.afroman.entity.api.Entity;

public class PacketAddLevelEntity extends Packet
{
	private Entity entity;
	
	/**
	 * Designed to be sent from the <b>server</b> to the <b>client</b>.
	 * <p>
	 * Adds an entity to a ClientLevel.
	 * 
	 * @param level the level to add the entity to.
	 * @param the object type. <b>WARNING: </b> only expecting a TILE or ENTITY.
	 */
	public PacketAddLevelEntity(Entity entity)
	{
		super(PacketType.ADD_LEVEL_ENTITY);
		this.entity = entity;
	}
	
	@Override
	public byte[] getData()
	{ // (type, leveltype, assetType, x, y, width, height, hitboxes)
		return (type.ordinal() + Packet.SEPARATOR + entity.getLevel().getType().ordinal() + "," + entity.getAssetType() + "," + entity.getX() + "," + entity.getY() + "," + entity.getWidth() + "," + entity.getHeight() + (entity.hasHitbox() ? "," + entity.hitboxesAsSaveable() : "")).getBytes();
	}
}
