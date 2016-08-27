package ca.afroman.entity;

import ca.afroman.client.Role;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.IRoleEntity;
import ca.afroman.level.Level;
import ca.afroman.level.LevelType;
import ca.afroman.packet.PacketSetPlayerLevel;
import ca.afroman.packet.PacketSetPlayerLocation;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.server.ServerGame;

public class ServerPlayerEntity extends Entity implements IRoleEntity
{
	private Role role;
	
	/**
	 * Creates a new Entity without a hitbox.
	 * 
	 * @param pos the position
	 * @param width the width of this
	 * @param height the height of this
	 */
	public ServerPlayerEntity(Role role, Vector2DDouble pos)
	{
		super(true, -1, (role == Role.PLAYER1 ? ClientPlayerEntity.PLAYER1_ASSET : ClientPlayerEntity.PLAYER2_ASSET), pos, new Hitbox(3, 5, 10, 11));
		
		this.role = role;
	}
	
	/**
	 * Removes an entity from their current level and puts them in another level.
	 * 
	 * @param level the new level.
	 */
	@Override
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		if (level != null)
		{
			level.getPlayers().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			level.getPlayers().add(this);
		}
		
		ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketSetPlayerLevel(this.getRole(), (level != null ? level.getType() : LevelType.NULL)));
	}
	
	@Override
	public Role getRole()
	{
		return role;
	}
	
	@Override
	public void onMove(byte xa, byte ya)
	{
		super.onMove(xa, ya);
		ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketSetPlayerLocation(this));
	}
	
	@Override
	public void setPosition(Vector2DDouble position)
	{
		super.setPosition(position);
		ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketSetPlayerLocation(this));
	}
}
