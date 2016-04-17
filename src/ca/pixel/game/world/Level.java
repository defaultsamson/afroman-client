package ca.pixel.game.world;

import ca.pixel.game.Game;
import ca.pixel.game.gfx.Texture;

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
				tiles[x + (y * width)] = new Tile(Material.GRASS);
			}
		}
	}
	
	public void setCameraCenteredInWorld(int x, int y)
	{
		xOffset = x + (Game.WIDTH / 2);
		yOffset = y + (Game.HEIGHT / 2);
	}
	
	public void render(Texture renderTo)
	{
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < height; x++)
			{
				getTile(x, y);
			}
		}
	}
	
	public Tile getTile(int x, int y)
	{
		// If off-screen
		if (x < 0 || x > width || y < 0 || y > height)
		{
			return new Tile(Material.VOID);
		}
		
		return tiles[x + (y * width)];
	}
}
