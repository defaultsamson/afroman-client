package ca.pixel.game.entity;

import ca.pixel.game.gfx.Texture;
import ca.pixel.game.world.Level;

public class Entity
{
	public int x, y, width, height;
	protected int speed;
	protected final int originalSpeed;
	protected int numSteps = 0;
	protected boolean isMoving;
	protected Direction movingDir;
	protected Level level;
	protected boolean cameraFollow = false;
	protected Texture texture;
	
	public Entity(Level level, Texture texture, int x, int y, int width, int height, int speed)
	{
		this.level = level;
		this.texture = texture;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.speed = speed;
		this.originalSpeed = speed;
		level.addEntity(this);
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
	
	public void tick()
	{
		if (cameraFollow)
		{
			level.setCameraCenterInWorld(x + (width / 2), y + (height / 2));
		}
	}
	
	public void render(Texture renderTo)
	{
		renderTo.draw(texture, x - level.getCameraXOffset(), y - level.getCameraYOffset());
	}
	
	public void move(int xa, int ya)
	{
		/*
		 * Does each component separately
		 * if (xa != 0 && ya != 0)
		 * {
		 * move(xa, 0);
		 * move(0, ya);
		 * }
		 */
		
		numSteps++;
		
		if (!hasCollided(xa, ya))
		{
			if (ya < 0) movingDir = Direction.UP;
			if (ya > 0) movingDir = Direction.DOWN;
			if (xa < 0) movingDir = Direction.LEFT;
			if (xa > 0) movingDir = Direction.RIGHT;
			x += xa * speed;
			y += ya * speed;
			
			isMoving = true;
		}
		else
		{
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
	
	public boolean hasCollided(int xa, int ya)
	{
		return false;
	}
	
	public Texture getTexture()
	{
		return texture;
	}
}
