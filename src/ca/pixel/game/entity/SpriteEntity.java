package ca.pixel.game.entity;

import java.awt.Rectangle;

import ca.pixel.game.assets.SpriteAnimation;
import ca.pixel.game.assets.Texture;

public class SpriteEntity extends Entity
{
	protected SpriteAnimation[] sprites;
	protected SpriteAnimation[] idleSprites;
	
	public SpriteEntity(Texture texture, int x, int y, int width, int height, int speed, Rectangle... hitboxes)
	{
		this(texture, texture, texture, texture, x, y, speed, hitboxes);
	}
	
	public SpriteEntity(Texture texture1, Texture texture2, Texture texture3, Texture texture4, int x, int y, int speed, Rectangle... hitboxes)
	{
		this(new SpriteAnimation(0, texture1), new SpriteAnimation(0, texture2), new SpriteAnimation(0, texture3), new SpriteAnimation(0, texture4), new SpriteAnimation(0, texture1), new SpriteAnimation(0, texture2), new SpriteAnimation(0, texture3), new SpriteAnimation(0, texture4), x, y, speed, hitboxes);
	}
	
	public SpriteEntity(SpriteAnimation up, SpriteAnimation down, SpriteAnimation left, SpriteAnimation right, SpriteAnimation upIdle, SpriteAnimation downIdle, SpriteAnimation leftIdle, SpriteAnimation rightIdle, int x, int y, int speed, Rectangle... hitboxes)
	{
		super(x, y, speed, hitboxes);
		
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
			case UP:
				return sprites[0].getCurrentFrame();
			default:
			case DOWN:
				return sprites[1].getCurrentFrame();
			case LEFT:
				return sprites[2].getCurrentFrame();
			case RIGHT:
				return sprites[3].getCurrentFrame();
			case NONE:
				switch (lastDirection)
				{
					case UP:
						return idleSprites[0].getCurrentFrame();
					default:
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
		super.render(renderTo);
	}
}
