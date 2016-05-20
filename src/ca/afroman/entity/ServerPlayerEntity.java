package ca.afroman.entity;

import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.IRoleEntity;
import ca.afroman.level.Level;
import ca.afroman.packet.PacketAddPlayer;
import ca.afroman.player.Role;
import ca.afroman.server.ServerGame;

public class ServerPlayerEntity extends Entity implements IRoleEntity
{
	private Role role;
	
	/**
	 * Creates a new Entity without a hitbox.
	 * 
	 * @param x the x ordinate of this in the level
	 * @param y the y ordinate of this in the level
	 * @param width the width of this
	 * @param height the height of this
	 */
	public ServerPlayerEntity(Role role, double x, double y)
	{
		super(-1, null, (role == Role.PLAYER1 ? ClientPlayerEntity.PLAYER1_ASSET : ClientPlayerEntity.PLAYER2_ASSET), x, y, 16, 16, new Hitbox(3, 5, 10, 11));
		
		this.role = role;
	}
	
	/**
	 * Removes an entity from their current level and puts them in another level.
	 * 
	 * @param level the new level.
	 */
	@Override
	public void addToLevel(Level level)
	{
		if (getLevel() != null)
		{
			getLevel().getPlayers().remove(this);
		}
		
		this.level = level;
		
		if (getLevel() != null)
		{
			getLevel().getPlayers().add(this);
		}
		
		ServerGame.instance().socket().sendPacketToAllClients(new PacketAddPlayer(level.getType(), this));
	}
	
	@Override
	public Role getRole()
	{
		return role;
	}
	
	@Override
	public Entity getEntity()
	{
		return this;
	}
}
