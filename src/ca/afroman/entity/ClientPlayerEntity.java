package ca.afroman.entity;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.client.ClientGame;
import ca.afroman.client.Role;
import ca.afroman.entity.api.ClientAssetEntityDirectional;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.IRoleEntity;
import ca.afroman.level.Level;

public class ClientPlayerEntity extends ClientAssetEntityDirectional implements IRoleEntity
{
	public static final AssetType PLAYER1_ASSET = AssetType.PLAYER_ONE_RAW;
	public static final AssetType PLAYER2_ASSET = AssetType.PLAYER_TWO_RAW;
	
	private Role role;
	
	/**
	 * Creates a new ClientPlayerEntity.
	 * 
	 * @param x the x ordinate of this in the level
	 * @param y the y ordinate of this in the level
	 */
	public ClientPlayerEntity(Role role, double x, double y)
	{
		super(-1, (role == Role.PLAYER1 ? PLAYER1_ASSET : PLAYER2_ASSET), (role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_UP).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_UP).clone()), (role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_DOWN).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_DOWN).clone()), (role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_LEFT).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_LEFT).clone()), (role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_RIGHT).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_RIGHT).clone()),
				(role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_UP).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_UP).clone()), (role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_DOWN).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_DOWN).clone()), (role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_LEFT).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_LEFT).clone()), (role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_RIGHT).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_RIGHT).clone()), x, y, new Hitbox(3, 5, 10, 11));
		
		this.role = role;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		// If it's not in build mode and the role of this is the role of the client, let them move
		if (!ClientGame.instance().isBuildMode() && this.role == ClientGame.instance().sockets().getConnectedPlayer().getRole())
		{
			int xa = 0;
			int ya = 0;
			
			if (ClientGame.instance().input().up.isPressed())
			{
				ya = -1;
			}
			if (ClientGame.instance().input().down.isPressed())
			{
				ya = 1;
			}
			if (ClientGame.instance().input().left.isPressed())
			{
				xa = -1;
			}
			if (ClientGame.instance().input().right.isPressed())
			{
				xa = 1;
			}
			
			this.move(xa, ya);
		}
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
			synchronized (level.getPlayers())
			{
				level.getPlayers().remove(this);
			}
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			synchronized (level.getPlayers())
			{
				level.getPlayers().add(this);
			}
		}
	}
	
	@Override
	public Role getRole()
	{
		return role;
	}
}
