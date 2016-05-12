package ca.afroman.packet;

import ca.afroman.entity.Hitbox;
import ca.afroman.level.LevelType;

public class PacketAddLevelHitbox extends Packet
{
	private Hitbox hitbox;
	private LevelType levelType;
	
	/**
	 * Designed to be sent from the <b>server</b> to the <b>client</b>.
	 * <p>
	 * Adds a hitbox to a ClientLevel.
	 * 
	 * @param level the level to add the hitbox to.
	 */
	public PacketAddLevelHitbox(LevelType levelType, Hitbox hitbox)
	{
		super(PacketType.ADD_LEVEL_HITBOX);
		this.levelType = levelType;
		this.hitbox = hitbox;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + Packet.SEPARATOR + levelType.ordinal() + "," + hitbox.getID() + "," + hitbox.getX() + "," + hitbox.getY() + "," + hitbox.getWidth() + "," + hitbox.getHeight()).getBytes();
	}
}
