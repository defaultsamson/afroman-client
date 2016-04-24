package ca.pixel.game.world;

import java.awt.Rectangle;
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
import ca.pixel.game.world.tiles.Tile;

public class Level
{
	private List<Tile> tiles;
	private List<Entity> entities;
	private List<PointLight> lights;
	public int width;
	public int height;
	public int xOffset = 0;
	public int yOffset = 0;
	private LightMap lightmap;
	private PointLight playerLight;
	
	public Level()
	{
		tiles = new ArrayList<Tile>();
		entities = new ArrayList<Entity>();
		lights = new ArrayList<PointLight>();
		
		lightmap = new LightMap(Game.WIDTH, Game.HEIGHT);
		playerLight = new FlickeringLight(this, 0, 0, 50, 47, 4);
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
			
			int lineNum = 1;
			for (String line : lines)
			{
				if (line != null && !line.isEmpty() && !line.equals(" "))
				{
					try
					{
						String[] split1 = line.split("\\(");
						String type = split1[0];
						String[] split2 = split1[1].split("\\)");
						String[] parameters = split2.length > 0 ? split2[0].split(", ") : null;
						
						switch (type)
						{
							case "Level":
								level = new Level();
								break;
							case "Tile":
							{
								int x = Integer.parseInt(parameters[0]) * 16;
								int y = Integer.parseInt(parameters[1]) * 16;
								Texture texture = Assets.getTexture(Assets.valueOf(parameters[2]));
								boolean isEmitter = parameters[3].equals("true");
								
								// If it has custom hitboxes defined
								if (parameters.length > 5)
								{
									List<Rectangle> hitboxes = new ArrayList<Rectangle>();
									
									for (int i = 4; i < parameters.length; i += 4)
									{
										hitboxes.add(new Rectangle(Integer.parseInt(parameters[i]), Integer.parseInt(parameters[i + 1]), Integer.parseInt(parameters[i + 2]), Integer.parseInt(parameters[i + 3])));
									}
									
									new Tile(level, x, y, texture, isEmitter, hitboxes);
								}
								else // Else, just check for if it's solid or not
								{
									boolean isSolid = parameters[4].equals("true");
									
									new Tile(level, x, y, texture, isEmitter, isSolid);
								}
							}
								break;
							case "PointLight":
							{
								int x = Integer.parseInt(parameters[0]);
								int y = Integer.parseInt(parameters[1]);
								int radius = Integer.parseInt(parameters[2]);
								
								new PointLight(level, x, y, radius);
							}
								break;
						}
					}
					catch (Exception e)
					{
						System.err.println("Level failed to load line " + lineNum + ": " + line);
						e.printStackTrace();
					}
				}
				
				lineNum++;
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
				light.renderCentered(lightmap);
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
	
	public void addLight(PointLight light)
	{
		lights.add(light);
	}
	
	public void addEntity(Entity entity)
	{
		entities.add(entity);
	}
	
	public void addTile(Tile tile)
	{
		tiles.add(tile);
	}
	
	public List<Entity> getEntities()
	{
		return entities;
	}
	
	public List<Tile> getTiles()
	{
		return tiles;
	}
}
