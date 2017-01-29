package ca.afroman.entity.api;

import ca.afroman.battle.BattleEntity;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.events.BattleScene;
import ca.afroman.game.Game;
import ca.afroman.interfaces.ITickable;
import ca.afroman.level.api.Level;
import ca.afroman.log.ALogType;
import ca.afroman.packet.level.PacketEntityMove;
import ca.afroman.packet.level.PacketPlayerMoveClientServer;
import ca.afroman.packet.level.PacketSetEntityLocation;
import ca.afroman.packet.level.PacketSetPlayerLocationServerClient;
import ca.afroman.resource.IDCounter;
import ca.afroman.resource.ModulusCounter;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.server.ServerGame;

public abstract class Entity extends PositionLevelObject implements ITickable
{
	private static final boolean PLAYER_COLLISION = false;
	private static final boolean HITBOX_COLLISION = true;
	private static final boolean ENTITY_COLLISION = false;
	
	protected static final int MICROMANAGED_ID = -1;
	
	private static IDCounter serverIdCounter = null;
	private static IDCounter clientIdCounter = null;
	
	/**
	 * The ID counter that keeps track of Entity ID's.
	 * 
	 * @param isServerSide whether this is being counted from the server or the client
	 * @return the ID counter for the specified game instance.
	 */
	private static IDCounter getIDCounter(boolean isServerSide)
	{
		if (isServerSide)
		{
			if (serverIdCounter == null) serverIdCounter = new IDCounter();
			return serverIdCounter;
		}
		else
		{
			if (clientIdCounter == null) clientIdCounter = new IDCounter();
			return clientIdCounter;
		}
	}
	
	// All the required variables needed to create an Entity
	private int id;
	protected boolean hasHitbox;
	protected Hitbox[] hitbox;
	
	// All the movement related variables
	protected double speed;
	protected final double originalSpeed;
	protected int numSteps;
	protected Direction direction;
	protected Direction lastDirection;
	private boolean cameraFollow;
	private BattleScene battle;
	/** Left bottom **/
	protected BattleEntity battleEntity1;
	/** Left middle **/
	protected BattleEntity battleEntity2;
	/** Left top **/
	protected BattleEntity battleEntity3;
	
	private byte deltaXa = 0;
	private byte deltaYa = 0;
	
	// Used for Entity movement to tell how much of the deltaXa/Ya that it's progressed
	private byte deltaXaMoved = 0;
	private byte deltaYaMoved = 0;
	
	private boolean hasMovedSince = false;
	private ModulusCounter setPosCounter = new ModulusCounter(60 * 2); // Every 2 seconds
	private ModulusCounter deltaMoveCounter = new ModulusCounter(60 / 15); // 15 times per second
	
	/**
	 * Creates a new Entity.
	 * 
	 * @param isServerSide whether this is on the server instance or not
	 * @param isMicromanaged whether this is managed by an external manager (such as an Event), as opposed to directly being managed by the level
	 * @param position the position
	 * @param hitboxes the hitboxes (if any)
	 */
	public Entity(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position, Hitbox... hitboxes)
	{
		super(isServerSide, isMicromanaged, position);
		
		this.id = isMicromanaged ? MICROMANAGED_ID : getIDCounter(isServerSide).getNext(); // -1 if this is not an object in a level
		
		// if (!isMicromanaged) System.out.println(isServerSide + ": Doing Ur Mum: (" + this.id + ")" + this);
		
		this.hasHitbox = hitboxes.length > 0;
		hitbox = hasHitbox ? hitboxes : null;
		updateHitboxInLevel();
		
		battle = null;
		
		speed = 1.0;
		originalSpeed = speed;
		numSteps = 0;
		direction = Direction.NONE;
		lastDirection = direction;
		cameraFollow = false;
	}
	
	/**
	 * Removes an entity from its current level and puts it in another level.
	 * 
	 * @param level the new level
	 */
	@Override
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
	 * parsing movement across the network.
	 * 
	 * @param dXa the change in X amplitude
	 * @param dYa the change in Y amplitude
	 */
	public void autoMove(byte dXa, byte dYa)
	{
		this.deltaXa += dXa;
		this.deltaYa += dYa;
	}
	
	public BattleScene getBattle()
	{
		return battle;
	}
	
	/** Left bottom **/
	public BattleEntity getBattleEntity1()
	{
		return battleEntity1;
	}
	
	/** Left middle **/
	public BattleEntity getBattleEntity2()
	{
		return battleEntity2;
	}
	
	/** Left top **/
	public BattleEntity getBattleEntity3()
	{
		return battleEntity3;
	}
	
	/**
	 * @return the current direction that this is travelling in.
	 */
	public Direction getDirection()
	{
		return direction;
	}
	
	/**
	 * @return the hitbox of this Entity relative to itself, <i>not</i> the world.
	 */
	public Hitbox[] getHitbox()
	{
		return hitbox;
	}
	
	/**
	 * @return this entity's ID (-1 if this is micromanaged because it's redundant).
	 */
	public int getID()
	{
		return id;
	}
	
	/**
	 * @return the last direction that this was travelling in before it stopped.
	 */
	public Direction getLastDirection()
	{
		return lastDirection;
	}
	
	/**
	 * @return the level that this is in.
	 */
	@Override
	public Level getLevel()
	{
		return level;
	}
	
	/**
	 * @return if this has a change in xa or ya to
	 *         relay, or that has been relayed across the network.
	 */
	private boolean hasDeltaDeltaMovement()
	{
		return deltaXaMoved != 0 || deltaYaMoved != 0;
	}
	
	/**
	 * @return if this has a change in xa or ya to
	 *         relay, or that has been relayed across the network.
	 */
	private boolean hasDeltaMovement()
	{
		return deltaXa != 0 || deltaYa != 0;
	}
	
	/**
	 * @return whether this Entity has a hitbox or not.
	 */
	public boolean hasHitbox()
	{
		return hasHitbox;
	}
	
	public boolean isBattleable()
	{
		return battleEntity1 != null || battleEntity2 != null || battleEntity3 != null;
	}
	
	//
	// /**
	// * @return the hitbox of this with the offset of this's in-level coordinates.
	// */
	// public Hitbox[] hitboxInLevel()
	// {
	// return hitboxInLevel;
	// }
	//
	/**
	 * Tells if this's hitbox is intersecting another.
	 * 
	 * @param other the other Entity
	 * @return if the hitboxes are colliding.
	 */
	public boolean isColliding(Entity other)
	{
		if (this.hasHitbox() && other.hasHitbox())
		{
			return isColliding(other.getHitbox());
		}
		return false;
	}
	
	/**
	 * Tells if this's hitboxes are intersecting an array on in-level hitboxes.
	 * 
	 * @param other the other Entity
	 * @return if the hitboxes are colliding.
	 */
	public boolean isColliding(Hitbox... levelHitboxes)
	{
		if (hasHitbox)
		{
			for (Hitbox box : getHitbox())
			{
				for (Hitbox oBox : levelHitboxes)
				{
					// If the hitboxes are colliding in world
					if (oBox.isColliding(box)) return true;
				}
			}
		}
		return false;
	}
	
	public boolean isInBattle()
	{
		return battle != null;
	}
	
	/**
	 * @return if this is currently in motion.
	 */
	public boolean isMoving()
	{
		return direction != Direction.NONE;
	}
	
	/**
	 * Moves this by the provided amplitudes, at this's speed.
	 * 
	 * @param xa the horizontal amplitude
	 * @param ya the vertical amplitude
	 */
	public void move(byte xa, byte ya)
	{
		move(xa, ya, false);
	}
	
	/**
	 * Moves this by the provided amplitudes, at this's speed.
	 * 
	 * @param xa the horizontal amplitude
	 * @param ya the vertical amplitude
	 * @param autoMoved whether this is being moved automatically by the network, or being controlled locally
	 */
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
			getPosition().add(deltaX, 0);
			
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
					for (Hitbox hitbox : player.getHitbox())
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
				getPosition().add(-deltaX, 0);
				deltaX = 0;
			}
		}
		
		// Tests if it can move in the y
		if (ya != 0)
		{
			getPosition().add(0, deltaY);
			
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
					for (Hitbox hitbox : player.getHitbox())
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
				getPosition().add(0, -deltaY);
				deltaY = 0;
			}
		}
		
		updateHitboxInLevel();
		
		if (direction != Direction.NONE) lastDirection = direction;
		
		// If hasn't moved (Either isn't allowed to or simply isn't moving)
		if (deltaX == 0 && deltaY == 0)
		{
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
			numSteps++;
			
			direction = Direction.fromAmplitudes(deltaX, deltaY);
			
			if (!autoMoved)
			{
				deltaXa += direction.getXAmplitude();
				deltaYa += direction.getYAmplitude();
			}
			else
			{
				deltaXaMoved += direction.getXAmplitude();
				deltaYaMoved += direction.getYAmplitude();
			}
			
			hasMovedSince = true;
		}
	}
	
	/**
	 * Removes this from its current level.
	 */
	@Override
	public void removeFromLevel()
	{
		addToLevel(null);
	}
	
	/**
	 * Puts this's speed back to the original speed.
	 */
	public void resetSpeed()
	{
		speed = originalSpeed;
	}
	
	public void setBattle(BattleScene battle)
	{
		if (battle == null)
		{
			if (this.battle != null)
			{
				this.battle.removeEntityFromBattle(this);
				this.addToLevel(this.battle.getLevel());
				this.battle = null;
			}
			else
			{
				Game.instance(isServerSide()).logger().log(ALogType.WARNING, "Entity " + id + " is already in a null battle");
			}
		}
		else
		{
			if (this.battle == null)
			{
				this.battle = battle;
				battle.addEntityToBattle(this);
				removeFromLevel();
			}
			else
			{
				Game.instance(isServerSide()).logger().log(ALogType.WARNING, "Entity " + id + " is already in a battle");
			}
		}
	}
	
	/**
	 * Makes the level camera follow this Entity or not.
	 * 
	 * @param follow whether or not to follow
	 */
	public void setCameraToFollow(boolean follow)
	{
		cameraFollow = follow;
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
		// TODO this is unused. Remove?.. Make sure you won't need this later
		lastDirection = dir;
	}
	
	/**
	 * <i>Designed for use from the server only.</i>
	 * <p>
	 * Sets the position of this to the provided getPosition().
	 * 
	 * @param position the new position
	 */
	// @Override
	// public void setPosition(Vector2DDouble position)
	// {
	// super.setPosition(position);
	// updateHitboxInLevel();
	// }
	
	@Override
	public void setPosition(double x, double y)
	{
		super.setPosition(x, y);
		updateHitboxInLevel();
	}
	
	/**
	 * Sets this's speed.
	 * 
	 * @param speed the new speed
	 */
	public void setSpeed(double speed)
	{
		this.speed = speed;
	}
	
	@Override
	public void tick()
	{
		// if this is serverside
		if (isServerSide())
		{
			// TODO move this to the level code?
			// And is at the set interval for sending position updates
			if (setPosCounter.isAtInterval())
			{
				// And this has moved since the last time
				if (hasMovedSince)
				{
					// Update the position
					if (this instanceof PlayerEntity)
					{
						ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketSetPlayerLocationServerClient(((PlayerEntity) this).getRole(), getPosition(), !hasDeltaMovement()));
					}
					else
					{
						ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketSetEntityLocation(level.getLevelType(), getID(), getPosition(), !hasDeltaMovement()));
					}
					
					hasMovedSince = false;
				}
			}
			
			if (!(this instanceof PlayerEntity))
			{
				if (hasDeltaDeltaMovement() && deltaMoveCounter.isAtInterval())
				{
					ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketEntityMove(level.getLevelType(), getID(), deltaXaMoved, deltaYaMoved));
					
					deltaXaMoved = 0;
					deltaYaMoved = 0;
				}
			}
			
			// Old system for moving client-side entities smoothly
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
		else // Client side
		{
			if (cameraFollow)
			{
				getLevel().setCameraCenterInWorld(new Vector2DDouble(getPosition().getX() + (16 / 2), getPosition().getY() + (16 / 2)));
			}
			
			// If it's the client side player that is controlled by the keyboard input
			// Then use the deltaX and deltaY to tell the server where it's moved
			if (this instanceof PlayerEntity && ((PlayerEntity) this).getRole() == ClientGame.instance().getRole())
			{
				// If has moved, and it's been a given amount of ticks, tell the server the position of this
				if (hasDeltaMovement() && deltaMoveCounter.isAtInterval())
				{
					ClientGame.instance().sockets().sender().sendPacket(new PacketPlayerMoveClientServer(deltaXa, deltaYa));
					
					deltaXa = 0;
					deltaYa = 0;
				}
			}
			// If it's not a player that is controlled by the ClientGame instance (the keyboard input)
			// Then automatically move it using its deltaX and deltaY
			else
			{
				// Old system for moving client-side entities smoothly
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
	}
	
	/**
	 * Updates this's in-level hitboxes so that they match the current position of this.
	 */
	protected void updateHitboxInLevel()
	{
		updateHitboxInLevel(hitbox);
	}
	
	/**
	 * Updates this's in-level hitboxes so that they match the current position of this.
	 */
	protected void updateHitboxInLevel(Hitbox... hitbox)
	{
		if (hitbox != null && hitbox.length > 0)
		{
			for (int i = 0; i < hitbox.length; i++)
			{
				Hitbox box = hitbox[i];
				if (box != null)
				{
					hitbox[i].updateRelativeHitboxToPosition(getPosition());
				}
			}
		}
	}
}
