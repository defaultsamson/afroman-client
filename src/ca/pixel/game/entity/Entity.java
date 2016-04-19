package ca.pixel.game.entity;

import ca.pixel.game.gfx.SpriteAnimation;
import ca.pixel.game.gfx.Texture;
import ca.pixel.game.world.Level;

public class Entity
{
	public int x, y, width, height;
	protected int speed;
	protected final int originalSpeed;
	protected int numSteps = 0;
	protected boolean isMoving;
	protected Direction direction = Direction.NONE;
	protected Direction lastDirection = direction;
	protected Level level;
	protected boolean cameraFollow = false;
	protected SpriteAnimation[] sprites;
	protected SpriteAnimation[] idleSprites;
	
	public Entity(Level level, Texture texture, int x, int y, int width, int height, int speed)
	{
		this(level, texture, texture, texture, texture, x, y, width, height, speed);
	}
	
	public Entity(Level level, Texture texture1, Texture texture2, Texture texture3, Texture texture4, int x, int y, int width, int height, int speed)
	{
		this(level, new SpriteAnimation(0, texture1), new SpriteAnimation(0, texture2), new SpriteAnimation(0, texture3), new SpriteAnimation(0, texture4), new SpriteAnimation(0, texture1), new SpriteAnimation(0, texture2), new SpriteAnimation(0, texture3), new SpriteAnimation(0, texture4), x, y, width, height, speed);
	}
	
	public Entity(Level level, SpriteAnimation up, SpriteAnimation down, SpriteAnimation left, SpriteAnimation right, SpriteAnimation upIdle, SpriteAnimation downIdle, SpriteAnimation leftIdle, SpriteAnimation rightIdle, int x, int y, int width, int height, int speed)
	{
		this.level = level;
		this.sprites = new SpriteAnimation[4];
		this.sprites[0] = up;
		this.sprites[1] = down;
		this.sprites[2] = left;
		this.sprites[3] = right;
		this.idleSprites = new SpriteAnimation[4];
		this.idleSprites[0] = upIdle;
		this.idleSprites[1] = downIdle;
		this.idleSprites[2] = leftIdle;
		this.idleSprites[3] = rightIdle;
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
		
		for (SpriteAnimation sprite : sprites)
		{
			sprite.tick();
		}
		
		for (SpriteAnimation sprite : idleSprites)
		{
			sprite.tick();
		}
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
			if (direction != Direction.NONE)
			{
				lastDirection = direction;
			}
			
			if (ya < 0) direction = Direction.UP;
			if (ya > 0) direction = Direction.DOWN;
			if (xa < 0) direction = Direction.LEFT;
			if (xa > 0) direction = Direction.RIGHT;
			x += xa * speed;
			y += ya * speed;
			
			if (xa == 0 && ya == 0)
			{
				direction = Direction.NONE;
			}
			
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
	
	public Texture getTexture()
	{
		switch (direction)
		{
			default:
			case UP:
				return sprites[0].getCurrentFrame();
			case DOWN:
				return sprites[1].getCurrentFrame();
			case LEFT:
				return sprites[2].getCurrentFrame();
			case RIGHT:
				return sprites[3].getCurrentFrame();
			case NONE:
				switch (lastDirection)
				{
					default:
					case UP:
						return idleSprites[0].getCurrentFrame();
					case DOWN:
						return idleSprites[1].getCurrentFrame();
					case LEFT:
						return idleSprites[2].getCurrentFrame();
					case RIGHT:
						return idleSprites[3].getCurrentFrame();
				}
		}
	}
}
