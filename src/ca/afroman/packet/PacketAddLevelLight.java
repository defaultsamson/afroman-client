package ca.afroman.packet;

import ca.afroman.gfx.PointLight;

public class PacketAddLevelLight extends Packet
{
	private PointLight light;
	
	/**
	 * Designed to be sent from the <b>server</b> to the <b>client</b>.
	 * <p>
	 * Adds an entity to a ClientLevel.
	 * 
	 * @param level the level to add the entity to.
	 * @param the object type. <b>WARNING: </b> only expecting a TILE or ENTITY.
	 */
	public PacketAddLevelLight(PointLight light)
	{
		super(PacketType.ADD_LEVEL_POINTLIGHT);
		this.light = light;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + Packet.SEPARATOR + light.getLevel().getType().ordinal() + "," + light.getID() + "," + light.getX() + "," + light.getY() + "," + light.getRadius()).getBytes();
	}
}
