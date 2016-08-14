package ca.afroman.entity.api;

import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.entity.ServerPlayerEntity;
import ca.afroman.interfaces.ITickable;
import ca.afroman.level.Level;
import ca.afroman.packet.PacketSetPlayerLocation;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.server.ServerGame;
import ca.afroman.util.IDCounter;

public class Entity implements ITickable, IServerClient
{
	private static final boolean PLAYER_COLLISION = false;
	private static final boolean HITBOX_COLLISION = true;
	private static final boolean ENTITY_COLLISION = false;
	
	private static IDCounter idCounter = new IDCounter();
	
	public static IDCounter getIDCounter()
	{
		return idCounter;
	}
	
	// All the required variables needed to create an Entity
	private int id;
	protected Level level;
	protected AssetType assetType;
	protected Vector2DDouble position;
	protected boolean hasHitbox;
	protected Hitbox[] hitbox;
	protected Hitbox[] hitboxInLevel;
	
	private boolean serverSide;
	
	// All the movement related variables
	protected double speed;
	protected final double originalSpeed;
	protected int numSteps;
	protected Direction direction;
	protected Direction lastDirection;
	
	/**
	 * Creates a new Entity without a hitbox.
	 * 
	 * @param id the ID of this Entity
	 * @param x the x ordinate of this in the level
	 * @param y the y ordinate of this in the level
	 * @param width the width of this
	 * @param height the height of this
	 */
	public Entity(boolean isServerSide, int id, AssetType assetType, Vector2DDouble position)
	{
		this(isServerSide, id, assetType, position, false, new Hitbox[] { null });
	}
	
	/**
	 * Creates a new Entity.
	 * 
	 * @param id the ID of this Entity
	 * @param x the x ordinate of this in the level
	 * @param y the y ordinate of this in the level
	 * @param width the width of this
	 * @param height the height of this
	 * @param hitboxes the hitboxes of this, only relative to this, <i>not</i> the world
	 */
	public Entity(boolean isServerSide, int id, AssetType assetType, Vector2DDouble position, Hitbox... hitboxes)
	{
		this(isServerSide, id, assetType, position, true, hitboxes);
	}
	
	/**
	 * Creates a new Entity.
	 * 
	 * @param x the x ordinate of this in the level
	 * @param y the y ordinate of this in the level
	 * @param width the width of this
	 * @param height the height of this
	 * @param hitboxes the hitboxes of this, only relative to this, <i>not</i> the world
	 */
	private Entity(boolean isServerSide, int id, AssetType assetType, Vector2DDouble position, boolean hasHitbox, Hitbox... hitboxes)
	{
		this.id = id; // -1 if this is not an object in a level
		this.level = null;
		this.assetType = assetType;
		this.position = position;
		this.hasHitbox = hasHitbox;
		hitbox = hasHitbox ? hitboxes : null;
		
		if (hasHitbox)
		{
			hitboxInLevel = new Hitbox[hitbox.length];
			
			for (int i = 0; i < hitbox.length; i++)
				hitboxInLevel[i] = hitbox[i].clone();
		}
		else
		{
			hitboxInLevel = null;
		}
		updateHitboxInLevel();
		
		serverSide = isServerSide;
		
		speed = 1.0;
		originalSpeed = speed;
		numSteps = 0;
		direction = Direction.NONE;
		lastDirection = direction;
	}
	
	public static Hitbox[] hitBoxListToArray(List<Hitbox> hitboxes)
	{
		Hitbox[] toReturn = new Hitbox[hitboxes.size()];
		
		for (int i = 0; i < toReturn.length; i++)
		{
			toReturn[i] = hitboxes.get(i);
		}
		
		return toReturn;
	}
	
	public void setPosition(Vector2DDouble position)
	{
		this.position = position;
		updateHitboxInLevel();
		
		// TODO separate server-side entity checks
		if (this instanceof ServerPlayerEntity)
		{
			ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketSetPlayerLocation((ServerPlayerEntity) this));
		}
	}
	
	/**
	 * @return the position of this entity.
	 */
	public Vector2DDouble getPosition()
	{
		return position;
	}
	
	/**
	 * @return the asset type associated with this Entity.
	 */
	public AssetType getAssetType()
	{
		return assetType;
	}
	
	/**
	 * Removes an entity from their current level.
	 * <p>
	 * <b>WARNING:</b> If adding a tile, use removeTileFromLevel().
	 */
	public void removeFromLevel()
	{
		addToLevel(null);
	}
	
	/**
	 * Removes a tile from their current level.
	 * <p>
	 * <b>WARNING:</b> This method will remove this entity from the level. Otherwise, for standard entities, use removeFromLevel()
	 */
	public void removeTileFromLevel()
	{
		addTileToLevel(null, (byte) 0);
	}
	
	/**
	 * Removes an entity from their current level and puts them in another level.
	 * <p>
	 * <b>WARNING:</b> If adding a tile, use addTileToLevel().
	 * 
	 * @param level the new level.
	 */
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		if (level != null)
		{
			level.getEntities().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			level.getEntities().add(this);
		}
	}
	
	/**
	 * Removes a tile from their current level and puts them in another level.
	 * <p>
	 * <b>WARNING:</b> This method will add this entity to the tile layers in the level. Otherwise, for standard entities, use addToLevel()
	 * 
	 * @param level the new level
	 * @param layer the new layer
	 */
	public void addTileToLevel(Level newLevel, byte layer)
	{
		if (level == newLevel) return;
		
		if (level != null)
		{
			synchronized (level.getTiles())
			{
				// Searches all the old layers in case the old tile isn't on the same layer as the new one being specified
				for (List<Entity> tiles : level.getTiles())
				{
					if (tiles.contains(this))
					{
						tiles.remove(this);
					}
				}
			}
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			synchronized (level.getTiles(layer))
			{
				level.getTiles(layer).add(this);
			}
		}
	}
	
	public Level getLevel()
	{
		return level;
	}
	
	/**
	 * Tells if this Entity's hitbox is intersecting another.
	 * 
	 * @param other the other Entity
	 * @return if the hitboxes are colliding.
	 */
	public boolean isColliding(Entity other)
	{
		if (this.hasHitbox() && other.hasHitbox())
		{
			return isColliding(other.hitboxInLevel());
		}
		return false;
	}
	
	public boolean isColliding(Hitbox... worldHitboxes)
	{
		if (hitboxInLevel() != null)
		{
			for (Hitbox box : hitboxInLevel())
			{
				for (Hitbox oBox : worldHitboxes)
				{
					// If the hitboxes are colliding in world
					if (oBox.intersects(box)) return true;
				}
			}
		}
		return false;
	}
	
	private void updateHitboxInLevel()
	{
		if (hasHitbox)
		{
			for (int i = 0; i < hitboxInLevel.length; i++)
			{
				hitboxInLevel[i].x = hitbox[i].getX() + position.getX();
				hitboxInLevel[i].y = hitbox[i].getY() + position.getY();
			}
		}
	}
	
	/**
	 * @return the hitbox with the offset of this Entity's in-level coordinates.
	 */
	public Hitbox[] hitboxInLevel()
	{
		return hitboxInLevel;
	}
	
	/**
	 * @return the hitbox of this Entity relative to itself.
	 */
	public Hitbox[] getHitbox()
	{
		return hitbox;
	}
	
	@Override
	public void tick()
	{
		
	}
	
	/**
	 * @return is this Entity has a hitbox.
	 */
	public boolean hasHitbox()
	{
		return hasHitbox;
	}
	
	public void onInteract()
	{
		
	}
	
	/**
	 * Sets a new previous direction for this. Useful for
	 * setting the direction that this Entity is facing when
	 * its current direction is none.
	 * 
	 * @param dir the direction to set
	 */
	public void setLastDirection(Direction dir)
	{
		lastDirection = dir;
	}
	
	public Direction getLastDirection()
	{
		return lastDirection;
	}
	
	/**
	 * Sets a new direction for this, and sets the previous direction to whatever it was previously.
	 * 
	 * @param dir the direction to set
	 */
	public void setDirection(Direction dir)
	{
		lastDirection = direction;
		direction = dir;
	}
	
	public Direction getDirection()
	{
		return direction;
	}
	
	@SuppressWarnings("unused")
	public void move(byte xa, byte ya)
	{
		if (isServerSide()) System.out.println("Movingsd4");
		
		if (getLevel() == null)
		{
			return;
		}
		
		// Moves the obejct
		double deltaX = xa * speed;
		double deltaY = ya * speed;
		
		// Tests if it can move in the x
		if (xa != 0)
		{
			position.add(deltaX, 0);
			
			updateHitboxInLevel();
			
			// Tests if it's allowed to move or not
			boolean canMove = true;
			
			if (ENTITY_COLLISION)
			{
				for (Entity other : getLevel().getEntities())
				{
					// Don't let it collide with itself
					if (other != this && this.isColliding(other))
					{
						if (isServerSide()) System.out.println("1");
						canMove = false;
						break;
					}
				}
			}
			
			if (canMove && HITBOX_COLLISION) // Only do the next calculations if it has not yet determined that this Entity can't move
			{
				for (Hitbox hitbox : getLevel().getHitboxes())
				{
					if (this.isColliding(hitbox))
					{
						if (isServerSide()) System.out.println("2");
						canMove = false;
						break;
					}
				}
			}
			
			if (canMove && PLAYER_COLLISION) // Only do the next calculations if it has not yet determined that this Entity can't move
			{
				for (Entity player : getLevel().getPlayers())
				{
					for (Hitbox hitbox : player.hitboxInLevel())
					{
						if (this != player && this.isColliding(hitbox))
						{
							canMove = false;
							break;
						}
					}
				}
			}
			
			// If it it now intersecting another hitbox, move it back in the x direction
			if (!canMove)
			{
				position.add(-deltaX, 0);
				deltaX = 0;
			}
		}
		
		// Tests if it can move in the y
		if (ya != 0)
		{
			position.add(0, deltaY);
			
			updateHitboxInLevel();
			
			// Tests if it's allowed to move or not
			boolean canMove = true;
			
			if (ENTITY_COLLISION)
			{
				for (Entity other : getLevel().getEntities())
				{
					// Don't let it collide with itself
					if (other != this && this.isColliding(other))
					{
						if (isServerSide()) System.out.println("3");
						canMove = false;
						break;
					}
				}
			}
			
			if (canMove && HITBOX_COLLISION) // Only do the next calculations if it has not yet determined that this Entity can't move
			{
				for (Hitbox hitbox : getLevel().getHitboxes())
				{
					if (this.isColliding(hitbox))
					{
						if (isServerSide()) System.out.println("4");
						canMove = false;
						break;
					}
				}
			}
			
			if (canMove && PLAYER_COLLISION) // Only do the next calculations if it has not yet determined that this Entity can't move
			{
				for (Entity player : getLevel().getPlayers())
				{
					for (Hitbox hitbox : player.hitboxInLevel())
					{
						if (this != player && this.isColliding(hitbox))
						{
							canMove = false;
							break;
						}
					}
				}
			}
			
			// If it is now intersecting another hitbox, move it back in the x direction
			if (!canMove)
			{
				position.add(0, -deltaY);
				deltaY = 0;
			}
		}
		
		updateHitboxInLevel();
		
		// Used to send packets from the server to the client to update the player direction after it's stopped moving to stop the animation
		boolean sendPacket = false;
		
		if (direction != Direction.NONE) lastDirection = direction;
		
		// If hasn't moved (Either isn't allowed to or simply isn't moving)
		if (deltaX == 0 && deltaY == 0)
		{
			// If the direction isn't already none, then send a packet after it has been set to none.
			if (direction != Direction.NONE) sendPacket = true;
			
			direction = Direction.NONE;
			
			// Change the last direction so this entity faces in the direction that it tried to move in
			if (ya < 0) lastDirection = Direction.UP;
			if (ya > 0) lastDirection = Direction.DOWN;
			if (xa < 0) lastDirection = Direction.LEFT;
			if (xa > 0) lastDirection = Direction.RIGHT;
		}
		else
		{
			sendPacket = true;
			
			if (isServerSide()) System.out.println("Movingsd5");
			
			numSteps++;
			
			if (deltaY < 0) direction = Direction.UP;
			if (deltaY > 0) direction = Direction.DOWN;
			if (deltaX < 0) direction = Direction.LEFT;
			if (deltaX > 0) direction = Direction.RIGHT;
		}
		
		if (sendPacket)
		{
			onMove(xa, ya);
		}
	}
	
	public void onMove(byte xa, byte ya)
	{
		
	}
	
	/**
	 * Sets the speed of this.
	 * 
	 * @param speed the new speed.
	 */
	public void setSpeed(double speed)
	{
		this.speed = speed;
	}
	
	/**
	 * Puts the speed back to the original speed.
	 */
	public void resetSpeed()
	{
		speed = originalSpeed;
	}
	
	/**
	 * @return if this Entity is currently in motion.
	 */
	public boolean isMoving()
	{
		return direction != Direction.NONE;
	}
	
	/**
	 * @return hitboxes in a savable form for level saving and sending.
	 */
	public String hitboxesAsSaveable()
	{
		StringBuilder sb = new StringBuilder();
		
		if (this.hasHitbox())
		{
			for (int i = 0; i < getHitbox().length; i++)
			{
				Hitbox box = getHitbox()[i];
				
				sb.append(box.getX());
				sb.append(", ");
				sb.append(box.getY());
				sb.append(", ");
				sb.append(box.getWidth());
				sb.append(", ");
				sb.append(box.getHeight());
				if (i == getHitbox().length - 1) sb.append(", ");
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * @return this player's ID.
	 */
	public int getID()
	{
		return id;
	}
	
	@Override
	public boolean isServerSide()
	{
		return serverSide;
	}
}
