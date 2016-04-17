package ca.pixel.game.world;

import ca.pixel.game.Game;
import ca.pixel.game.assets.Assets;
import ca.pixel.game.gfx.Texture;
import ca.pixel.game.world.tiles.Tile;

public class Level
{
	private Tile[] tiles;
	public int width;
	public int height;
	public int xOffset = 0;
	public int yOffset = 0;
	
	public Level(int width, int height)
	{
		tiles = new Tile[width * height];
		
		this.width = height;
		this.height = height;
		
		// Initializes the level.
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				if (x * y % 10 < 5)// TODO Test generation. Feel free to replace
				{
					tiles[x + (y * width)] = Tile.GRASS;
				}
				else
				{
					tiles[x + (y * width)] = Tile.STONE;
				}
			}
		}
	}
	
	public void setCameraCenteredInWorld(int x, int y)
	{
		xOffset = (Game.WIDTH / 2) - x;
		yOffset = (Game.HEIGHT / 2) - y;
	}
	
	public void render(Texture renderTo)
	{
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < height; x++)
			{
				switch (getTile(x, y).getMaterial())
				{
					case GRASS:
						renderTo.draw(Assets.grass, (x * 8) - xOffset, (y * 8) - yOffset);
						break;
					case STONE:
						renderTo.draw(Assets.stone, (x * 8) - xOffset, (y * 8) - yOffset);
						break;
					case VOID:
						break;
				}
			}
		}
	}
	
	public void tick()
	{
		
	}
	
	public Tile getTile(int x, int y)
	{
		// If off-screen
		if (x < 0 || x > width || y < 0 || y > height)
		{
			return Tile.VOID;
			// return new Tile(Material.VOID, false, false);
		}
		
		return tiles[x + (y * width)];
	}
}
