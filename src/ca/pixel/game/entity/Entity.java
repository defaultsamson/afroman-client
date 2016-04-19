package ca.pixel.game.entity;

import ca.pixel.game.gfx.Texture;
import ca.pixel.game.gfx.TextureArray;
import ca.pixel.game.world.Level;

public class Entity
{
	public int x, y, width, height;
	protected int speed;
	protected final int originalSpeed;
	protected int numSteps = 0;
	protected boolean isMoving;
	protected Direction direction = Direction.NONE;
	protected Level level;
	protected boolean cameraFollow = false;
	protected TextureArray textures;
	protected Texture currentTexture;
	protected int animationIndex;
	
	public Entity(Level level, Texture texture, int x, int y, int width, int height, int speed)
	{
		this(level, new TextureArray(texture, 1, 1), x, y, width, height, speed);
	}
	
	public Entity(Level level, TextureArray texture, int x, int y, int width, int height, int speed)
	{
		this.level = level;
		this.textures = texture;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.speed = speed;
		this.originalSpeed = speed;
		this.currentTexture = texture.getRandomTexture();
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
		
		updateTexture();
	}
	
	public void render(Texture renderTo)
	{
		renderTo.draw(this.getTexture(), x - level.getCameraXOffset(), y - level.getCameraYOffset());
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
			if (ya < 0) direction = Direction.UP;
			if (ya > 0) direction = Direction.DOWN;
			if (xa < 0) direction = Direction.LEFT;
			if (xa > 0) direction = Direction.RIGHT;
			x += xa * speed;
			y += ya * speed;
			
			isMoving = true;
		}
		else
		{
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
	
	public boolean hasCollided(int xa, int ya)
	{
		return false;
	}
	
	public void updateTexture()
	{
		animationIndex++;
		
		if (animationIndex >= 60)
		{
			animationIndex = 0;
		}
		
		switch (direction)
		{
			default:
			case UP:
				if (animationIndex <= 15)
				{
					currentTexture = textures.getTexture(9);
				}
				else if (animationIndex <= 30)
				{
					currentTexture = textures.getTexture(10);
				}
				else if (animationIndex <= 45)
				{
					currentTexture = textures.getTexture(11);
				}
				else if (animationIndex <= 60)
				{
					currentTexture = textures.getTexture(10);
				}
				break;
			case DOWN:
				if (animationIndex <= 15)
				{
					currentTexture = textures.getTexture(0);
				}
				else if (animationIndex <= 30)
				{
					currentTexture = textures.getTexture(1);
				}
				else if (animationIndex <= 45)
				{
					currentTexture = textures.getTexture(2);
				}
				else if (animationIndex <= 60)
				{
					currentTexture = textures.getTexture(1);
				}
				break;
			case LEFT:
				if (animationIndex <= 15)
				{
					currentTexture = textures.getTexture(3);
				}
				else if (animationIndex <= 30)
				{
					currentTexture = textures.getTexture(4);
				}
				else if (animationIndex <= 45)
				{
					currentTexture = textures.getTexture(5);
				}
				else if (animationIndex <= 60)
				{
					currentTexture = textures.getTexture(4);
				}
				break;
			case RIGHT:
				if (animationIndex <= 15)
				{
					currentTexture = textures.getTexture(6);
				}
				else if (animationIndex <= 30)
				{
					currentTexture = textures.getTexture(7);
				}
				else if (animationIndex <= 45)
				{
					currentTexture = textures.getTexture(8);
				}
				else if (animationIndex <= 60)
				{
					currentTexture = textures.getTexture(7);
				}
				break;
			case NONE:
				
				break;
		}
	}
	
	public Texture getTexture()
	{
		return currentTexture;
	}
}
