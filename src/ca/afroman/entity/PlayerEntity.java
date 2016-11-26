package ca.afroman.entity;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.api.DrawableEntityDirectional;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.IRoleEntity;
import ca.afroman.game.Role;
import ca.afroman.level.api.Level;
import ca.afroman.level.api.LevelType;
import ca.afroman.packet.PacketPlayerInteract;
import ca.afroman.packet.PacketPlayerMove;
import ca.afroman.packet.PacketSetPlayerLevel;
import ca.afroman.packet.PacketSetPlayerLocation;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.server.ServerGame;

public class PlayerEntity extends DrawableEntityDirectional implements IRoleEntity
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
		super(isServerSide, -1, getUp(isServerSide, role), getDown(isServerSide, role), getLeft(isServerSide, role), getRight(isServerSide, role), getIdleUp(isServerSide, role), getIdleDown(isServerSide, role), getIdleLeft(isServerSide, role), getIdleRight(isServerSide, role), pos, new Hitbox(3, 5, 10, 11));
		
		this.role = role;
	}
	
	private static SpriteAnimation getUp(boolean isServerSide, Role role)
	{
		return isServerSide ? null : role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_UP).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_UP).clone();
	}
	
	private static SpriteAnimation getDown(boolean isServerSide, Role role)
	{
		return isServerSide ? null : role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_DOWN).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_DOWN).clone();
	}
	
	private static SpriteAnimation getLeft(boolean isServerSide, Role role)
	{
		return isServerSide ? null : role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_LEFT).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_LEFT).clone();
	}
	
	private static SpriteAnimation getRight(boolean isServerSide, Role role)
	{
		return isServerSide ? null : role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_RIGHT).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_RIGHT).clone();
	}
	
	private static SpriteAnimation getIdleUp(boolean isServerSide, Role role)
	{
		return isServerSide ? null : role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_UP).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_UP).clone();
	}
	
	private static SpriteAnimation getIdleDown(boolean isServerSide, Role role)
	{
		return isServerSide ? null : role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_DOWN).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_DOWN).clone();
	}
	
	private static SpriteAnimation getIdleLeft(boolean isServerSide, Role role)
	{
		return isServerSide ? null : role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_LEFT).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_LEFT).clone();
	}
	
	private static SpriteAnimation getIdleRight(boolean isServerSide, Role role)
	{
		return isServerSide ? null : role == Role.PLAYER1 ? (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_RIGHT).clone() : (SpriteAnimation) Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_RIGHT).clone();
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
		
		if (isServerSide())
		{
			ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketSetPlayerLevel(this.getRole(), (level != null ? level.getLevelType() : LevelType.NULL)));
		}
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
	
	public void reset()
	{
		setCameraToFollow(false);
		removeFromLevel();
	}
	
	public void setPosition(boolean updateClientPosition, Vector2DDouble position)
	{
		super.setPosition(position);
		
		if (updateClientPosition && isServerSide())
		{
			ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketSetPlayerLocation(this));
		}
	}
	
	@Override
	public void setPosition(Vector2DDouble position)
	{
		setPosition(true, position);
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
				if (ClientGame.instance().getCurrentScreen() == null)
				{
					byte xa = 0;
					byte ya = 0;
					
					if (ClientGame.instance().input().interact.isPressedFiltered())
					{
						ClientGame.instance().sockets().sender().sendPacket(new PacketPlayerInteract());
					}
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
}
