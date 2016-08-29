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
import ca.afroman.level.LevelType;
import ca.afroman.packet.PacketPlayerMove;
import ca.afroman.packet.PacketSetPlayerLevel;
import ca.afroman.packet.PacketSetPlayerLocation;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.server.ServerGame;

public class PlayerEntity extends ClientAssetEntityDirectional implements IRoleEntity
{
	public static final AssetType PLAYER1_ASSET = AssetType.PLAYER_ONE_RAW;
	public static final AssetType PLAYER2_ASSET = AssetType.PLAYER_TWO_RAW;
	
	private Role role;
	
	/**
	 * Creates a new ClientPlayerEntity.
	 * 
	 * @param pos the position
	 */
	public PlayerEntity(boolean isServerSide, Role role, Vector2DDouble pos)
	{
		super(isServerSide, -1, (role == Role.PLAYER1 ? PLAYER1_ASSET : PLAYER2_ASSET), (role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_UP).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_UP).clone()), (role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_DOWN).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_DOWN).clone()), (role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_LEFT).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_LEFT).clone()), (role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_RIGHT).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_RIGHT).clone()),
				(role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_UP).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_UP).clone()), (role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_DOWN).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_DOWN).clone()), (role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_LEFT).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_LEFT).clone()), (role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_RIGHT).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_RIGHT).clone()), pos, new Hitbox(3, 5, 10, 11));
		
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
		
		if (isServerSide()) ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketSetPlayerLevel(this.getRole(), (level != null ? level.getType() : LevelType.NULL)));
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
		if (isServerSide())
		{
			ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketSetPlayerLocation(this));
		}
		else
		{
			ClientGame.instance().sockets().sender().sendPacket(new PacketPlayerMove(xa, ya));
		}
	}
	
	@Override
	public void setPosition(Vector2DDouble position)
	{
		super.setPosition(position);
		
		if (isServerSide())
		{
			ServerGame.instance().sockets().sender().sendPacket(new PacketSetPlayerLocation(this));
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (isServerSide())
		{
			
		}
		else
		{
			// If it's not in build mode and the role of this is the role of the client, let them move
			if (!ClientGame.instance().isBuildMode() && this.role == ClientGame.instance().getRole())
			{
				byte xa = 0;
				byte ya = 0;
				
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
	}
}
