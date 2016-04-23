package ca.pixel.game.entity;

import java.awt.Rectangle;

import ca.pixel.game.world.Level;
import ca.pixel.game.world.LevelObject;

public abstract class Entity extends LevelObject
{
	protected int speed;
	protected final int originalSpeed;
	protected int numSteps = 0;
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
		
		// Tests if it can move in the x
		{
			x += deltaX;
			
			// Tests if it's allowed to move or not
			boolean canMove = true;
			for (LevelObject object : level.getObjects())
			{
				// Don't let it collide with itself
				if (object != this && this.isColliding(object))
				{
					canMove = false;
					break;
				}
			}
			
			for (LevelObject object : level.getTiles())
			{
				// Don't let it collide with itself
				if (object != this && this.isColliding(object))
				{
					canMove = false;
					break;
				}
			}
			
			if (!canMove)
			{
				x -= deltaX;
				deltaX = 0;
			}
		}
		
		// Tests if it can move Y
		{
			y += deltaY;
			
			// Tests if it's allowed to move or not
			boolean canMove = true;
			for (LevelObject object : level.getObjects())
			{
				// Don't let it collide with itself
				if (object != this && this.isColliding(object))
				{
					canMove = false;
					break;
				}
			}
			
			for (LevelObject object : level.getTiles())
			{
				// Don't let it collide with itself
				if (object != this && this.isColliding(object))
				{
					canMove = false;
					break;
				}
			}
			
			if (!canMove)
			{
				y -= deltaY;
				deltaY = 0;
			}
		}
		
		if (deltaY < 0) direction = Direction.UP;
		if (deltaY > 0) direction = Direction.DOWN;
		if (deltaX < 0) direction = Direction.LEFT;
		if (deltaX > 0) direction = Direction.RIGHT;
		if (deltaX == 0 && deltaY == 0) direction = Direction.NONE;
	}
	
	public void setSpeed(int speed)
	{
		this.speed = speed;
	}
	
	public void resetSpeed()
	{
		speed = originalSpeed;
	}
	
	public boolean isMoving()
	{
		return direction != Direction.NONE;
	}
}
