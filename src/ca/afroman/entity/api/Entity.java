package ca.afroman.entity.api;

import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.ClientPlayerEntity;
import ca.afroman.entity.ServerPlayerEntity;
import ca.afroman.interfaces.ITickable;
import ca.afroman.level.Level;
import ca.afroman.packet.PacketPlayerMove;
import ca.afroman.packet.PacketSetPlayerLocation;
import ca.afroman.server.ServerGame;
import ca.afroman.util.IDCounter;

public class Entity implements ITickable
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
	protected double x;
	protected double y;
	protected boolean hasHitbox;
	protected Hitbox[] hitbox;
	
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
	public Entity(int id, AssetType assetType, double x, double y)
	{
		this(id, assetType, x, y, false, new Hitbox[] { null });
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
	public Entity(int id, AssetType assetType, double x, double y, Hitbox... hitboxes)
	{
		this(id, assetType, x, y, true, hitboxes);
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
	private Entity(int id, AssetType assetType, double x, double y, boolean hasHitbox, Hitbox... hitboxes)
	{
		this.id = id; // -1 if this is not an object in a level
		this.level = null;
		this.assetType = assetType;
		this.x = x;
		this.y = y;
		this.hasHitbox = hasHitbox;
		hitbox = (hasHitbox ? hitboxes : null);
		
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
	
	public void setLocation(double newX, double newY)
	{
		this.x = newX;
		this.y = newY;
		
		// TODO separate server-side entity checks
		if (this instanceof ServerPlayerEntity)
		{
			ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketSetPlayerLocation((ServerPlayerEntity) this));
		}
	}
	
	/**
	 * Sets the level x ordinate of this Entity.
	 * 
	 * @deprecated Use setLocation() for overriding a player's location, as this doesn't communicate with the server.
	 */
	@Deprecated
	public void setX(double newX)
	{
		this.x = newX;
	}
	
	/**
	 * Sets the level y ordinate of this Entity.
	 * 
	 * @deprecated Use setLocation() for overriding a player's location, as this doesn't communicate with the server.
	 */
	@Deprecated
	public void setY(double newY)
	{
		this.y = newY;
	}
	
	/**
	 * @return the level x ordinate of this Entity.
	 */
	public double getX()
	{
		return x;
	}
	
	/**
	 * @return the level y ordinate of this Entity.
	 */
	public double getY()
	{
		return y;
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
		for (Hitbox box : hitboxInLevel())
		{
			for (Hitbox oBox : worldHitboxes)
			{
				// If the hitboxes are colliding in world
				if (oBox.intersects(box)) return true;
			}
		}
		return false;
	}
	
	/**
	 * @return the hitbox with the offset of this Entity's in-level coordinates.
	 */
	public Hitbox[] hitboxInLevel()
	{
		Hitbox[] boxes = new Hitbox[hitbox.length];
		
		for (int i = 0; i < hitbox.length; i++)
		{
			boxes[i] = new Hitbox(hitbox[i].x + x, hitbox[i].y + y, hitbox[i].width, hitbox[i].height);
		}
		
		return boxes;
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
		// It's it's not set to move anyways
		// if (xa == 0 && ya == 0)
		// {
		// direction = Direction.NONE;
		// return;
		// }
		
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
			x += deltaX;
			
			// Tests if it's allowed to move or not
			boolean canMove = true;
			
			if (ENTITY_COLLISION)
			{
				for (Entity other : getLevel().getEntities())
				{
					// Don't let it collide with itself
					if (other != this && this.isColliding(other))
					{
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
				x -= deltaX;
				deltaX = 0;
			}
		}
		
		// Tests if it can move in the y
		if (ya != 0)
		{
			y += deltaY;
			
			// Tests if it's allowed to move or not
			boolean canMove = true;
			
			if (ENTITY_COLLISION)
			{
				for (Entity other : getLevel().getEntities())
				{
					// Don't let it collide with itself
					if (other != this && this.isColliding(other))
					{
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
				y -= deltaY;
				deltaY = 0;
			}
		}
		
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
			
			numSteps++;
			
			if (deltaY < 0) direction = Direction.UP;
			if (deltaY > 0) direction = Direction.DOWN;
			if (deltaX < 0) direction = Direction.LEFT;
			if (deltaX > 0) direction = Direction.RIGHT;
		}
		
		if (sendPacket)
		{
			// TODO separate server-side entity checks
			if (this instanceof ServerPlayerEntity)
			{
				ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketSetPlayerLocation((ServerPlayerEntity) this));
			}
			else if (this instanceof ClientPlayerEntity)
			{
				ClientGame.instance().sockets().sender().sendPacket(new PacketPlayerMove(xa, ya));
			}
			else
			{
				// TODO
				// ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketUpdateEntityLocation(this));
			}
		}
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
		String toReturn = "";
		
		if (this.hasHitbox())
		{
			for (int i = 0; i < getHitbox().length; i++)
			{
				Hitbox box = getHitbox()[i];
				
				toReturn += box.getX() + ", " + box.getY() + ", " + box.getWidth() + ", " + box.getHeight() + (i == getHitbox().length - 1 ? "" : ", ");
			}
		}
		
		return toReturn;
	}
	
	/**
	 * @return this player's ID.
	 */
	public int getID()
	{
		return id;
	}
}
