package ca.pixel.game.entity;

import java.awt.Rectangle;

import ca.pixel.game.gfx.Texture;
import ca.pixel.game.world.Level;
import ca.pixel.game.world.LevelObject;

public abstract class Entity extends LevelObject
{
	protected int speed;
	protected final int originalSpeed;
	protected int numSteps = 0;
	protected boolean isMoving;
	protected Direction direction = Direction.NONE;
	protected Direction lastDirection = direction;
	protected boolean cameraFollow = false;
	
	public Entity(Level level, int x, int y, int speed, Rectangle... hitboxes)
	{
		super(level, x, y, hitboxes);
		this.speed = speed;
		this.originalSpeed = speed;
		level.addObject(this);
	}
	
	/**
	 * Makes the camera in the level follow this Entity.
	 * 
	 * @param follow
	 */
	public void setCameraToFollow(boolean follow)
	{
		cameraFollow = follow;
	}
	
	@Override
	public void tick()
	{
		if (cameraFollow)
		{
			level.setCameraCenterInWorld(x + (width / 2), y + (height / 2));
		}
	}
	
	public Level getLevel()
	{
		return level;
	}
	
	@Override
	public abstract void render(Texture renderTo);
	
	public void move(int xa, int ya)
	{
		/*
		 * s
		 * Does each component separately
		 * if (xa != 0 && ya != 0)
		 * {
		 * move(xa, 0);
		 * move(0, ya);
		 * }
		 */
		
		if (xa == 0 && ya == 0)
		{
			direction = Direction.NONE;
			return;
		}
		
		numSteps++;
		
		// Moves the obejct
		int deltaX = xa * speed;
		int deltaY = ya * speed;
		x += deltaX;
		y += deltaY;
		
		// Tests if it's allowed to move or not
		boolean canMove = true;
		for (LevelObject object : level.getObjects())
		{
			// Don't let it collide with itself
			if (object != this && object.isColliding(this))
			{
				canMove = false;
				break;
			}
		}
		
		if (canMove)
		{
			if (direction != Direction.NONE)
			{
				lastDirection = direction;
			}
			
			if (ya < 0) direction = Direction.UP;
			if (ya > 0) direction = Direction.DOWN;
			if (xa < 0) direction = Direction.LEFT;
			if (xa > 0) direction = Direction.RIGHT;
			// Don't do this because we already did it earlier
			// x += deltaX;
			// y += deltaY;
			
			isMoving = true;
		}
		else
		{
			// Reverts the coordinates back to before it was colliding
			x -= deltaX;
			y -= deltaY;
			
			// Stops it from moving
			direction = Direction.NONE;
			isMoving = false;
		}
	}
	
	public void setSpeed(int speed)
	{
		this.speed = speed;
	}
	
	public void resetSpeed()
	{
		speed = originalSpeed;
	}
}
