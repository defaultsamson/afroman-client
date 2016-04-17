package ca.pixel.game.world.tiles;

import ca.pixel.game.gfx.Texture;
import ca.pixel.game.world.Level;
import ca.pixel.game.world.Material;

public class TileBasic extends Tile
{
	public TileBasic(Material material, boolean isSolid, boolean isEmitter)
	{
		super(material, isSolid, isEmitter);
	}

	@Override
	public void render(Texture renderTo, Level level, int x, int y)
	{
		
	}
	
}
