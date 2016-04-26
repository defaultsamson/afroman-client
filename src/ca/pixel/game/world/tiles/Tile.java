package ca.pixel.game.world.tiles;

import java.awt.Rectangle;
import java.util.List;

import ca.pixel.game.assets.Texture;
import ca.pixel.game.entity.Level;
import ca.pixel.game.entity.LevelObject;

public class Tile extends LevelObject
{
	protected Texture texture;
	protected boolean isEmitter;
	
	private static Rectangle[] hitBoxListToArray(List<Rectangle> hitboxes)
	{
		Rectangle[] toReturn = new Rectangle[hitboxes.size()];
		
		for (int i = 0; i < toReturn.length; i++)
		{
			toReturn[i] = hitboxes.get(i);
		}
		
		return toReturn;
	}
	
	public Tile(Level level, int x, int y, Texture texture, boolean isEmitter, boolean isSolid)
	{
		this(x, y, texture, isEmitter, (isSolid ? new Rectangle(0, 0, 16, 16) : null));
	}
	
	public Tile(int x, int y, Texture texture, boolean isEmitter, List<Rectangle> hitboxes)
	{
		this(x, y, texture, isEmitter, hitBoxListToArray(hitboxes));
	}
	
	public Tile(int x, int y, Texture texture, boolean isEmitter, Rectangle... hitboxes)
	{
		super(x, y, hitboxes);
		this.isEmitter = isEmitter;
		this.texture = texture;
	}
	
	@Override
	public void addToLevel(Level level)
	{
		this.level = level;
		level.addTile(this);
	}
	
	public boolean isEmitter()
	{
		return isEmitter;
	}
	
	@Override
	public void render(Texture renderTo)
	{
		renderTo.draw(texture, x - level.getCameraXOffset(), y - level.getCameraYOffset());
		
		super.render(renderTo);
	}
	
	@Override
	public void tick()
	{
		
	}
	
	public Texture getTexture()
	{
		return texture;
	}
}
