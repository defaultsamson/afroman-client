package ca.pixel.game.world.tiles;

import ca.pixel.game.assets.Texture;
import ca.pixel.game.world.Level;

public class GrassTile extends Tile
{
	public GrassTile(Level level, int x, int y, Texture texture)
	{
		super(level, x, y, texture, Material.GRASS, false, false);
	}
	
	@Override
	public void render(Texture renderTo)
	{
		renderTo.draw(texture, x - level.getCameraXOffset(), y - level.getCameraYOffset());
		
		super.render(renderTo);
	}
}
