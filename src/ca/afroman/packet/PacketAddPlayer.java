package ca.afroman.packet;

import ca.afroman.entity.ServerPlayerEntity;
import ca.afroman.level.LevelType;

public class PacketAddPlayer extends Packet
{
	private ServerPlayerEntity player;
	private LevelType levelType;
	
	/**
	 * Designed to be sent from the <b>server</b> to the <b>client</b>.
	 * <p>
	 * Adds a player to a ClientLevel.
	 * 
	 * @param level the level to add the hitbox to.
	 */
	public PacketAddPlayer(LevelType levelType, ServerPlayerEntity player)
	{
		super(PacketType.ADD_LEVEL_PLAYER);
		this.levelType = levelType;
		this.player = player;
	}
	
	@Override
	public byte[] getData()
	{
		return (type.ordinal() + Packet.SEPARATOR + levelType.ordinal() + "," + player.getRole().ordinal() + "," + player.getX() + "," + player.getY()).getBytes();
	}
}
