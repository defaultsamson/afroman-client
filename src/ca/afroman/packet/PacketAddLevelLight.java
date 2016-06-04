package ca.afroman.packet;

import ca.afroman.gfx.PointLight;
import ca.afroman.level.LevelType;

public class PacketAddLevelLight extends Packet
{
	private PointLight light;
	private LevelType level;
	
	/**
	 * Designed to be sent from the <b>server</b> to the <b>client</b>.
	 * <p>
	 * Adds an entity to a ClientLevel.
	 * 
	 * @param level the level to add the entity to.
	 * @param the object type. <b>WARNING: </b> only expecting a TILE or ENTITY.
	 */
	public PacketAddLevelLight(LevelType level, PointLight light)
	{
		super(PacketType.ADD_LEVEL_POINTLIGHT, true);
		this.level = level;
		this.light = light;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + "," + id + Packet.SEPARATOR + level.ordinal() + "," + light.getID() + "," + light.getX() + "," + light.getY() + "," + light.getRadius()).getBytes();
	}
}
