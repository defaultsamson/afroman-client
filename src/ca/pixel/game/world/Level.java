package ca.pixel.game.world;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import ca.pixel.game.Game;
import ca.pixel.game.assets.Assets;
import ca.pixel.game.assets.Texture;
import ca.pixel.game.entity.Entity;
import ca.pixel.game.gfx.FlickeringLight;
import ca.pixel.game.gfx.LightMap;
import ca.pixel.game.gfx.PointLight;
import ca.pixel.game.world.tiles.Material;
import ca.pixel.game.world.tiles.Tile;

public class Level
{
	private Tile[] tiles;
	private List<LevelObject> entities;
	public int width;
	public int height;
	public int xOffset = 0;
	public int yOffset = 0;
	private LightMap lightmap = new LightMap(Game.WIDTH, Game.HEIGHT);
	private PointLight playerLight = new FlickeringLight(60, 150, 50, 47, 4);
	private List<PointLight> lights;
	
	public Level(int width, int height)
	{
		tiles = new Tile[width * height];
		
		this.width = height;
		this.height = height;
		
		entities = new ArrayList<LevelObject>();
		lights = new ArrayList<PointLight>();
		
		// Initializes the level.
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				if (x * y % 10 < 5)// TODO Test generation. Feel free to replace
				{
					tiles[x + (y * width)] = new Tile(this, x * 16, y * 16, Assets.getTexture(Assets.TILE_GRASS), Material.GRASS, false, false);
				}
				else if (x * 13 / y % 13 < 4)// TODO Test generation. Feel free to replace
				{
					tiles[x + (y * width)] = new Tile(this, x * 16, y * 16, Assets.getTexture(Assets.TILE_WALL_GRASS), Material.WALL, false, true);
				}
				else
				{
					tiles[x + (y * width)] = new Tile(this, x * 16, y * 16, Assets.getTexture(Assets.TILE_DIRT), Material.DIRT, false, false);
				}
			}
		}
		
		lights.add(playerLight);
		
		lights.add(new PointLight(60, 150, 10));
		
		for (int x = 0; x < 30; x++)
			for (int y = 0; y < 30; y++)
				lights.add(new PointLight((x * 15) + 500, (y * 15) + 500, 20));
		
		lights.add(new PointLight(140, 170, 20));
		lights.add(new PointLight(20, 240, 20));
		lights.add(new PointLight(40, 260, 20));
		lights.add(new PointLight(0, 700, 120));
		
	}
	
	public static Level fromFile(String path)
	{
		Level level = null;
		
		try
		{
			InputStream in = Level.class.getResourceAsStream(path);
			
			File tempFile = File.createTempFile("level", ".lv");
			FileOutputStream out = new FileOutputStream(tempFile);
			
			byte[] buffer = new byte[1024];
			int size = 0;
			while ((size = in.read(buffer)) > -1)
			{
				out.write(buffer, 0, size);
			}
			
			in.close();
			out.close();
			
			List<String> lines = Files.readAllLines(tempFile.toPath());
			
			for (String line : lines)
			{
				String[] split1 = line.split("\\(");
				String type = split1[0];
				String[] parameters = split1[1].replace(")", "").split(", ");
				
				Material sauce = Material.valueOf("GRASS");
				
				switch (type)
				{
					case "Tile":
						
						break;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return level;
	}
	
	public void setCameraCenterInWorld(int x, int y)
	{
		xOffset = x - (Game.WIDTH / 2);
		yOffset = y - (Game.HEIGHT / 2);
	}
	
	public void render(Texture renderTo)
	{
		// Renders Tiles
		for (Tile tile : tiles)
		{
			tile.render(renderTo);
		}
		
		for (LevelObject entity : entities)
		{
			entity.render(renderTo);
		}
		
		if (!Game.instance().lightingDebug)
		{
			// Draws all the lighting over everything else
			lightmap.clear();
			
			for (PointLight light : lights)
			{
				light.renderCentered(lightmap, xOffset, yOffset);
			}
			
			lightmap.patch();
			
			renderTo.draw(lightmap, 0, 0);
		}
	}
	
	public void tick()
	{
		for (LevelObject entity : entities)
		{
			entity.tick();
		}
		
		for (PointLight light : lights)
		{
			light.tick();
		}
		
		playerLight.setX(Game.instance().player.getX() + 8);
		playerLight.setY(Game.instance().player.getY() + 8);
	}
	
	public int getCameraXOffset()
	{
		return xOffset;
	}
	
	public int getCameraYOffset()
	{
		return yOffset;
	}
	
	public Tile getTile(int x, int y)
	{
		// If off-screen
		if (x < 0 || x > width || y < 0 || y > height)
		{
			return null;
			// new Tile(this, 0, 0, Material.VOID, false, false);
		}
		
		return tiles[x + (y * width)];
	}
	
	public void addObject(Entity entity)
	{
		entities.add(entity);
	}
	
	public List<LevelObject> getObjects()
	{
		return entities;
	}
	
	public Tile[] getTiles()
	{
		return tiles;
	}
}
