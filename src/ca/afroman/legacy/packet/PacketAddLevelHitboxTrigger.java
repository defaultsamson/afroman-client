package ca.afroman.legacy.packet;

import ca.afroman.events.HitboxTrigger;
import ca.afroman.level.LevelType;
import ca.afroman.packet.PacketType;

@Deprecated
public class PacketAddLevelHitboxTrigger extends Packet
{
	private HitboxTrigger event;
	private LevelType levelType;
	
	/**
	 * Designed to be sent from the <b>server</b> to the <b>client</b>.
	 * <p>
	 * Adds a hitbox to a ClientLevel.
	 * 
	 * @param level the level to add the hitbox to.
	 */
	public PacketAddLevelHitboxTrigger(LevelType levelType, HitboxTrigger event)
	{
		super(PacketType.ADD_EVENT_HITBOX_TRIGGER, true);
		this.levelType = levelType;
		this.event = event;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + "," + id + Packet.SEPARATOR + levelType.ordinal() + "," + event.getID() + "," + event.getX() + "," + event.getY() + "," + event.getWidth() + "," + event.getHeight() + "," + event.triggerTypesAsSendable() + "," + event.triggersAsSendable() + "," + event.chainTriggersAsSendable()).getBytes();
	}
}
