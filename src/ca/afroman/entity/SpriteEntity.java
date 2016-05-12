package ca.afroman.entity;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.level.ClientLevel;

public class SpriteEntity extends ClientEntity
{
	protected SpriteAnimation[] sprites;
	protected SpriteAnimation[] idleSprites;
	
	public SpriteEntity(int id, ClientLevel level, Texture texture, double x, double y, double width, double height, Hitbox... hitboxes)
	{
		this(id, level, texture, texture, texture, texture, x, y, width, height, hitboxes);
	}
	
	public SpriteEntity(int id, ClientLevel level, Texture texture1, Texture texture2, Texture texture3, Texture texture4, double x, double y, double width, double height, Hitbox... hitboxes)
	{
		this(id, level, new SpriteAnimation(0, texture1), new SpriteAnimation(0, texture2), new SpriteAnimation(0, texture3), new SpriteAnimation(0, texture4), new SpriteAnimation(0, texture1), new SpriteAnimation(0, texture2), new SpriteAnimation(0, texture3), new SpriteAnimation(0, texture4), x, y, width, height, hitboxes);
	}
	
	public SpriteEntity(int id, ClientLevel level, SpriteAnimation up, SpriteAnimation down, SpriteAnimation left, SpriteAnimation right, SpriteAnimation upIdle, SpriteAnimation downIdle, SpriteAnimation leftIdle, SpriteAnimation rightIdle, double x, double y, double width, double height, Hitbox... hitboxes)
	{
		super(id, level, AssetType.INVALID, x, y, width, height, hitboxes);
		
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
	
	public void render(Texture renderTo)
	{
		renderTo.draw(this.getTexture(), getLevel().worldToScreenX(x), getLevel().worldToScreenY(y));
	}
}
