package ca.afroman.entity.api;

import java.util.List;

import ca.afroman.client.ClientGame;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.interfaces.ITickable;
import ca.afroman.level.api.Level;
import ca.afroman.packet.PacketPlayerMove;
import ca.afroman.resource.IDCounter;
import ca.afroman.resource.ModulusCounter;
import ca.afroman.resource.ServerClientObject;
import ca.afroman.resource.Vector2DDouble;

public class Entity extends ServerClientObject implements ITickable
{
	private static final boolean PLAYER_COLLISION = false;
	private static final boolean HITBOX_COLLISION = true;
	private static final boolean ENTITY_COLLISION = false;
	
	protected static final int MICROMANAGED_ID = -1;
	
	private static IDCounter serverIdCounter = new IDCounter();
	private static IDCounter clientIdCounter = new IDCounter();
	
	private static IDCounter getIDCounter(boolean isServerSide)
	{
		return isServerSide ? serverIdCounter : clientIdCounter;
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
	
	// All the required variables needed to create an Entity
	private int id;
	private boolean isMicromanaged;
	protected Level level;
	protected Vector2DDouble position;
	protected boolean hasHitbox;
	protected Hitbox[] hitbox;
	protected Hitbox[] hitboxInLevel;
	
	// All the movement related variables
	protected double speed;
	protected final double originalSpeed;
	protected int numSteps;
	protected Direction direction;
	protected Direction lastDirection;
	
	private byte deltaXa = 0;
	
	private byte deltaYa = 0;
	
	// private ModulusCounter setPosCounter = new ModulusCounter(60 * 5);
	private ModulusCounter deltaMoveCounter = new ModulusCounter(10);
	
	/**
	 * Creates a new Entity without a hitbox.
	 * 
	 * @param isServerSide
	 * @param id the ID of this Entity
	 */
	public Entity(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position)
	{
		this(isServerSide, isMicromanaged, position, false, new Hitbox[] { null });
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
	private Entity(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position, boolean hasHitbox, Hitbox... hitboxes)
	{
		super(isServerSide);
		
		this.isMicromanaged = isMicromanaged;
		
		this.id = isMicromanaged ? MICROMANAGED_ID : getIDCounter(isServerSide).getNext(); // -1 if this is not an object in a level
		
		this.level = null;
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
		
		speed = 1.0;
		originalSpeed = speed;
		numSteps = 0;
		direction = Direction.NONE;
		lastDirection = direction;
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
	public Entity(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position, Hitbox... hitboxes)
	{
		this(isServerSide, isMicromanaged, position, true, hitboxes);
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
	 * Only designed to be used by packet parsers for
	 * parsing movement.
	 * 
	 * @param dXa the change in X amplitude
	 * @param dYa the change in Y amplitude
	 */
	public void autoMove(byte dXa, byte dYa)
	{
		this.deltaXa += dXa;
		this.deltaYa += dYa;
	}
	
	public Direction getDirection()
	{
		return direction;
	}
	
	/**
	 * @return the hitbox of this Entity relative to itself.
	 */
	public Hitbox[] getHitbox()
	{
		return hitbox;
	}
	
	/**
	 * @return this entity's ID.
	 */
	public int getID()
	{
		return id;
	}
	
	public Direction getLastDirection()
	{
		return lastDirection;
	}
	
	public Level getLevel()
	{
		return level;
	}
	
	/**
	 * @return the position of this entity.
	 */
	public Vector2DDouble getPosition()
	{
		return position;
	}
	
	/**
	 * @return is this Entity has a hitbox.
	 */
	public boolean hasHitbox()
	{
		return hasHitbox;
	}
	
	protected boolean hasMoved()
	{
		return deltaXa != 0 || deltaYa != 0;
	}
	
	/**
	 * @return the hitbox with the offset of this Entity's in-level coordinates.
	 */
	public Hitbox[] hitboxInLevel()
	{
		return hitboxInLevel;
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
	
	/**
	 * @return if this entity is managed by a manager such as an Event object.
	 */
	public boolean isMicroManaged()
	{
		return isMicromanaged;
	}
	
	/**
	 * @return if this Entity is currently in motion.
	 */
	public boolean isMoving()
	{
		return direction != Direction.NONE;
	}
	
	public void move(byte xa, byte ya)
	{
		move(xa, ya, false);
	}
	
	@SuppressWarnings("unused")
	private void move(byte xa, byte ya, boolean autoMoved)
	{
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
			Direction tempLast = Direction.fromAmplitudes(xa, ya);
			if (tempLast != Direction.NONE)
			{
				lastDirection = tempLast;
			}
		}
		else
		{
			sendPacket = true;
			
			numSteps++;
			
			direction = Direction.fromAmplitudes(deltaX, deltaY);
			
			if (!autoMoved)
			{
				deltaXa += direction.getXAmplitude();
				deltaYa += direction.getYAmplitude();
				
				// System.out.println("Delta: " + deltaXa + ", " + deltaYa);
			}
		}
		
		if (sendPacket && !autoMoved)
		{
			onMove(xa, ya);
			System.out.println("Dong: " + isServerSide());
		}
	}
	
	public void onInteract()
	{
		
	}
	
	public void onMove(byte xa, byte ya)
	{
		
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
	 * Puts the speed back to the original speed.
	 */
	public void resetSpeed()
	{
		speed = originalSpeed;
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
	
	/**
	 * Designed for use from the server only.
	 * 
	 * @param position
	 */
	public void setPosition(Vector2DDouble position)
	{
		this.position.setPosition(position);
		updateHitboxInLevel();
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
	
	@Override
	public void tick()
	{
		// if (isServerSide())
		// {
		// if (setPosCounter.isAtInterval())
		// {
		// if (hasMoved())
		// {
		// if (this instanceof PlayerEntity)
		// {
		// System.out.println("Cancer1");
		// ClientGame.instance().sockets().sender().sendPacket(new PacketSetPlayerLocation(((PlayerEntity) this).getRole(), position));
		// }
		// else
		// {
		// // TODO entity move packet
		// // ClientGame.instance().sockets().sender().sendPacket(new PacketPlayerMove(((PlayerEntity) this).getRole(), deltaXa, deltaYa));
		// }
		// }
		// }
		// }
		// else
		// {
		// if (hasMoved())
		// {
		// if (deltaMoveCounter.isAtInterval())
		// {
		// if (this instanceof PlayerEntity)
		// {
		// ClientGame.instance().sockets().sender().sendPacket(new PacketPlayerMove(((PlayerEntity) this).getRole(), deltaXa, deltaYa));
		// }
		// else
		// {
		// ClientGame.instance().logger().log(ALogType.WARNING, "Only PlayerEntities are the only entities supposed to be controlled by the client side, not: " + this);
		// }
		//
		// deltaXa = 0;
		// deltaYa = 0;
		// }
		// }
		// }
		
		// if (isMicromanaged) // && !(this instanceof PlayerEntity)
		// {
		//
		// }
		
		// If it's the client side player that is controlled by the keyboard input
		// Then use the deltaX and deltaY to tell the server where it's moved
		if (this instanceof PlayerEntity && !isServerSide() && ((PlayerEntity) this).getRole() == ClientGame.instance().getRole())
		{
			if (hasMoved())
			{
				if (deltaMoveCounter.isAtInterval())
				{
					ClientGame.instance().sockets().sender().sendPacket(new PacketPlayerMove(((PlayerEntity) this).getRole(), deltaXa, deltaYa));
					
					deltaXa = 0;
					deltaYa = 0;
				}
			}
		}
		else
		// If it's not a player that is controlled by the ClientGame instance (the keyboard input)
		// Then automatically move it using its deltaX and deltaY
		{
			byte xa = 0;
			byte ya = 0;
			
			if (deltaXa > 0)
			{
				xa = 1;
				deltaXa -= 1;
			}
			else if (deltaXa < 0)
			{
				xa = -1;
				deltaXa += 1;
			}
			
			if (deltaYa > 0)
			{
				ya = 1;
				deltaYa -= 1;
			}
			else if (deltaYa < 0)
			{
				ya = -1;
				deltaYa += 1;
			}
			
			move(xa, ya, true);
		}
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
}
