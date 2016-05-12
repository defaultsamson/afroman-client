package ca.afroman.entity;

import ca.afroman.ClientGame;
import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.level.ClientLevel;
import ca.afroman.level.Level;
import ca.afroman.player.Role;

public class ClientPlayerEntity extends SpriteEntity
{
	public static final AssetType PLAYER1_ASSET = AssetType.RAW_PLAYER_ONE;
	public static final AssetType PLAYER2_ASSET = AssetType.RAW_PLAYER_TWO;
	
	private Role role;
	
	/**
	 * Creates a new ClientPlayerEntity.
	 * 
	 * @param x the x ordinate of this in the level
	 * @param y the y ordinate of this in the level
	 */
	public ClientPlayerEntity(Role role, double x, double y)
	{
		super(-1, null, (role == Role.PLAYER1 ? Assets.getSpriteAnimation(AssetType.PLAYER_ONE_UP) : Assets.getSpriteAnimation(AssetType.PLAYER_TWO_UP)), (role == Role.PLAYER1 ? Assets.getSpriteAnimation(AssetType.PLAYER_ONE_DOWN) : Assets.getSpriteAnimation(AssetType.PLAYER_TWO_DOWN)), (role == Role.PLAYER1 ? Assets.getSpriteAnimation(AssetType.PLAYER_ONE_LEFT) : Assets.getSpriteAnimation(AssetType.PLAYER_TWO_LEFT)), (role == Role.PLAYER1 ? Assets.getSpriteAnimation(AssetType.PLAYER_ONE_RIGHT) : Assets.getSpriteAnimation(AssetType.PLAYER_TWO_RIGHT)), (role == Role.PLAYER1 ? Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_UP) : Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_UP)), (role == Role.PLAYER1 ? Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_DOWN) : Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_DOWN)), (role == Role.PLAYER1 ? Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_LEFT) : Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_LEFT)),
				(role == Role.PLAYER1 ? Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_RIGHT) : Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_RIGHT)), x, y, 16, 16, new Hitbox(3, 5, 10, 10));
		
		this.role = role;
	}
	
	public Role getRole()
	{
		return role;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		// If it's not in build mode and the role of this is the role of the client, let them move
		if (!ClientGame.instance().isBuildMode() && this.role == ClientGame.instance().getRole())
		{
			int xa = 0;
			int ya = 0;
			
			if (ClientGame.instance().input.up.isPressed())
			{
				ya = -1;
			}
			if (ClientGame.instance().input.down.isPressed())
			{
				ya = 1;
			}
			if (ClientGame.instance().input.left.isPressed())
			{
				xa = -1;
			}
			if (ClientGame.instance().input.right.isPressed())
			{
				xa = 1;
			}
			
			this.move(xa, ya);
		}
	}
	
	@Override
	public ClientLevel getLevel()
	{
		return (ClientLevel) this.level;
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
			
			if (ePlayer != null && ePlayer instanceof ClientPlayerEntity)
			{
				ClientPlayerEntity player = (ClientPlayerEntity) ePlayer;
				
				this.level.removePlayer(player);
				this.level = level;
				this.level.addPlayer(this);
				return;
			}
		}
		else
		{
			this.level = level;
			this.level.addPlayer(this);
		}
	}
}
