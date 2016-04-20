package ca.pixel.game.entity;

import ca.pixel.game.gfx.SpriteAnimation;
import ca.pixel.game.gfx.Texture;
import ca.pixel.game.world.Level;

public class SpriteEntity extends Entity
{
	protected SpriteAnimation[] sprites;
	protected SpriteAnimation[] idleSprites;
	
	public SpriteEntity(Level level, Texture texture, int x, int y, int width, int height, int speed)
	{
		this(level, texture, texture, texture, texture, x, y, width, height, speed);
	}
	
	public SpriteEntity(Level level, Texture texture1, Texture texture2, Texture texture3, Texture texture4, int x, int y, int width, int height, int speed)
	{
		this(level, new SpriteAnimation(0, texture1), new SpriteAnimation(0, texture2), new SpriteAnimation(0, texture3), new SpriteAnimation(0, texture4), new SpriteAnimation(0, texture1), new SpriteAnimation(0, texture2), new SpriteAnimation(0, texture3), new SpriteAnimation(0, texture4), x, y, width, height, speed);
	}
	
	public SpriteEntity(Level level, SpriteAnimation up, SpriteAnimation down, SpriteAnimation left, SpriteAnimation right, SpriteAnimation upIdle, SpriteAnimation downIdle, SpriteAnimation leftIdle, SpriteAnimation rightIdle, int x, int y, int width, int height, int speed)
	{
		super(level, x, y, width, height, speed);
		
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
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		for (SpriteAnimation sprite : sprites)
		{
			sprite.tick();
		}
		
		for (SpriteAnimation sprite : idleSprites)
		{
			sprite.tick();
		}
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
	
	@Override
	public void render(Texture renderTo)
	{
		renderTo.draw(this.getTexture(), x - level.getCameraXOffset(), y - level.getCameraYOffset());
	}
}
