package ca.afroman.entity;

import ca.afroman.assets.AssetType;
import ca.afroman.level.Level;
import ca.afroman.packet.PacketAddPlayer;
import ca.afroman.player.Role;
import ca.afroman.server.ServerGame;

public class ServerPlayerEntity extends Entity
{
	public static final AssetType PLAYER1_ASSET = AssetType.RAW_PLAYER_ONE;
	public static final AssetType PLAYER2_ASSET = AssetType.RAW_PLAYER_TWO;
	
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
		super(-1, null, (role == Role.PLAYER1 ? PLAYER1_ASSET : (role == Role.PLAYER1 ? PLAYER2_ASSET : null)), x, y, 16, 16, new Hitbox(3, 5, 10, 10));
		
		this.role = role;
	}
	
	public Role getRole()
	{
		return role;
	}
	
	/**
	 * Removes an entity from their current level and puts them in another level.
	 * 
	 * @param level the new level.
	 */
	@Override
	public void addToLevel(Level level)
	{
		if (this.level != null)
		{
			Entity ePlayer = this.level.getPlayer(role);
			
			if (ePlayer != null && ePlayer instanceof ServerPlayerEntity)
			{
				ServerPlayerEntity player = (ServerPlayerEntity) ePlayer;
				
				this.level.removePlayer(player);
				this.level = level;
				this.level.addPlayer(this);
			}
		}
		else
		{
			this.level = level;
			this.level.addPlayer(this);
		}
		
		ServerGame.instance().socket().sendPacketToAllClients(new PacketAddPlayer(level.getType(), this));
	}
}
