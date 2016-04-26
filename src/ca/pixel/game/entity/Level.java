package ca.pixel.game.entity;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
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
import ca.pixel.game.gfx.FlickeringLight;
import ca.pixel.game.gfx.LightMap;
import ca.pixel.game.gfx.PointLight;
import ca.pixel.game.world.tiles.Tile;

public class Level
{
	private List<Tile> tiles;
	private List<Rectangle> levelHitboxes;
	private List<Entity> entities;
	private List<PointLight> lights;
	private int xOffset = 0;
	private int yOffset = 0;
	private LightMap lightmap;
	private PointLight playerLight;
	// private int playerPointX;
	// private int playerPointY; TODO add spawn points
	
	public Level()
	{
		tiles = new ArrayList<Tile>();
		levelHitboxes = new ArrayList<Rectangle>();
		entities = new ArrayList<Entity>();
		lights = new ArrayList<PointLight>();
		
		lightmap = new LightMap(Game.WIDTH, Game.HEIGHT);
		playerLight = new FlickeringLight(0, 0, 50, 47, 4);
		playerLight.addToLevel(this);
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
				if (line != null && !line.isEmpty() && !line.equals(" ") && !line.startsWith("//"))
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
							{
								// int x = Integer.parseInt(parameters[0]); TODO add spawn points
								// int y = Integer.parseInt(parameters[1]);
								
								level = new Level();
							}
								break;
							case "Tile":
							{
								int x = Integer.parseInt(parameters[0]);
								int y = Integer.parseInt(parameters[1]);
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
									
									Tile tile = new Tile(x, y, texture, isEmitter, hitboxes);
									tile.addToLevel(level);
								}
								else // Else, just check for if it's solid or not
								{
									boolean isSolid = parameters[4].equals("true");
									
									Tile tile = new Tile(level, x, y, texture, isEmitter, isSolid);
									tile.addToLevel(level);
								}
							}
								break;
							case "PointLight":
							{
								int x = Integer.parseInt(parameters[0]);
								int y = Integer.parseInt(parameters[1]);
								int radius = Integer.parseInt(parameters[2]);
								
								PointLight light = new PointLight(x, y, radius);
								light.addToLevel(level);
							}
								break;
							case "HitBox":
							{
								level.levelHitboxes.add(new Rectangle(Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2]), Integer.parseInt(parameters[3])));
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
	
	public List<String> toSaveFile()
	{
		List<String> toReturn = new ArrayList<String>();
		
		toReturn.add("Level()");
		
		toReturn.add("");
		toReturn.add("");
		toReturn.add("// The Tiles. Tiile(x, y, texture, radius, isEmitter, isSolid)");
		toReturn.add("// For texture, put the corrosponding Assets enum.");
		toReturn.add("");
		
		for (Tile tile : tiles)
		{
			String tileString = "Tile(" + tile.x + ", " + tile.y + ", " + Assets.getAssetEnum(tile.getTexture()).toString() + ", " + (tile.isEmitter() ? "true" : "false");
			
			if (tile.hasHitbox())
			{
				for (Rectangle box : tile.hitboxes)
				{
					tileString += ", " + (int) box.getX() + ", " + (int) box.getY() + ", " + (int) box.getWidth() + ", " + (int) box.getHeight();
				}
			}
			else
			{
				tileString += ", false";
			}
			
			tileString += ")";
			
			toReturn.add(tileString);
		}
		
		toReturn.add("");
		toReturn.add("");
		toReturn.add("// The world hitboxes. HitBox(x, y, width, height)");
		toReturn.add("");
		
		for (Rectangle box : levelHitboxes)
		{
			toReturn.add("HitBox(" + (int) box.getX() + ", " + (int) box.getY() + ", " + (int) box.getWidth() + ", " + (int) box.getHeight() + ")");
		}
		
		toReturn.add("");
		toReturn.add("");
		toReturn.add("// The Entities.");
		toReturn.add("");
		
		for (Entity entity : entities)
		{
			entity.getX();
		}
		
		toReturn.add("");
		toReturn.add("");
		toReturn.add("// The lights. PointLight(x, y, radius)");
		toReturn.add("");
		
		for (PointLight light : lights)
		{
			if (!light.equals(this.playerLight))
			{
				toReturn.add("PointLight(" + light.getX() + ", " + light.getY() + ", " + light.getRadius() + ")");
			}
		}
		
		// Copies the level data to the clipboard
		String toCopy = "";
		for (String copy : toReturn)
		{
			toCopy += copy + "\n";
		}
		StringSelection stringSelection = new StringSelection(toCopy);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
		
		return toReturn;
	}
	
	public void setCameraCenterInWorld(int x, int y)
	{
		xOffset = x - (Game.WIDTH / 2);
		yOffset = y - (Game.HEIGHT / 2);
	}
	
	// public void putPlayer() TODO add spawn points
	// {
	// // Sets the last level's pointx and y for if the player goes back
	// if (Game.instance().player.level != this)
	// {
	// Game.instance().player.level.playerPointX = Game.instance().player.getX();
	// Game.instance().player.level.playerPointY = Game.instance().player.getX();
	// }
	//
	// // Sets the player position to the new one for this level
	// Game.instance().player.setX(playerPointX);
	// Game.instance().player.setY(playerPointY);
	//
	// Game.instance().player.level = this;
	// }
	
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
		
		if (!Game.instance().isLightingDebugging())
		{
			// Draws all the lighting over everything else
			lightmap.clear();
			
			for (PointLight light : lights)
			{
				light.renderCentered(lightmap);
			}
			
			// Draws the light on the cursor if there is one
			if (Game.instance().isBuildMode() && currentBuildMode == 2)
			{
				lightmap.drawLight(Game.instance().input.getMouseX() - currentBuildLightRadius, Game.instance().input.getMouseY() - currentBuildLightRadius, currentBuildLightRadius);
			}
			
			lightmap.patch();
			
			renderTo.draw(lightmap, 0, 0);
		}
		
		// Draws out the hitboxes
		if (Game.instance().isHitboxDebugging())
		{
			for (Rectangle box : levelHitboxes)
			{
				int x = worldToScreenX((int) box.getX());
				int y = worldToScreenY((int) box.getY());
				int width = (int) box.getWidth() - 1;
				int height = (int) box.getHeight() - 1;
				
				renderTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 0.3F));
				renderTo.getGraphics().fillRect(x, y, width, height);
				renderTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 1F));
				renderTo.getGraphics().drawRect(x, y, width, height);
			}
		}
		
		// Draws the hitbox that's currently being drawn if there is one
		if (Game.instance().isBuildMode())
		{
			if (currentBuildMode == 1)
			{
				renderTo.draw(Assets.getTexture(Assets.fromOrdinal(currentBuildTextureOrdinal)), Game.instance().input.getMouseX(), Game.instance().input.getMouseY());
			}
			else if (currentBuildMode == 3 && hitboxClickCount == 1)
			{
				renderTo.getGraphics().drawRect(worldToScreenX(hitboxX1), worldToScreenY(hitboxY1), Game.instance().input.getMouseX() - worldToScreenX(hitboxX1), Game.instance().input.getMouseY() - worldToScreenY(hitboxY1));
			}
		}
	}
	
	private int currentBuildMode = 1;
	private int buildModes = 3;
	
	// Mode 1, Tiles
	private int currentBuildTextureOrdinal = 0;
	
	// Mode 2, PointLights
	private int currentBuildLightRadius = 10;
	
	// Mode 3, HitBoxes
	private int hitboxX1 = 0;
	private int hitboxY1 = 0;
	private int hitboxClickCount = 0;
	
	public void tick()
	{
		if (Game.instance().isBuildMode())
		{
			boolean isShifting = Game.instance().input.shift.isPressed();
			
			int speed = (isShifting ? 5 : 1);
			
			if (Game.instance().input.up.isPressed())
			{
				yOffset -= speed;
			}
			if (Game.instance().input.down.isPressed())
			{
				yOffset += speed;
			}
			if (Game.instance().input.left.isPressed())
			{
				xOffset -= speed;
			}
			if (Game.instance().input.right.isPressed())
			{
				xOffset += speed;
			}
			
			if (Game.instance().input.e.isPressedFiltered())
			{
				currentBuildMode++;
				
				if (currentBuildMode > buildModes)
				{
					currentBuildMode = 1;
				}
			}
			
			if (Game.instance().input.q.isPressedFiltered())
			{
				currentBuildMode--;
				
				if (currentBuildMode < 1)
				{
					currentBuildMode = buildModes;
				}
			}
			
			// Placing and removing blocks
			if (currentBuildMode == 1)
			{
				if (Game.instance().input.mouseLeft.isPressedFiltered())
				{
					new Tile(this, screenToWorldX(Game.instance().input.getMouseX()), screenToWorldY(Game.instance().input.getMouseY()), Assets.getTexture(Assets.fromOrdinal(currentBuildTextureOrdinal)), false, false);
				}
				
				if (Game.instance().input.mouseRight.isPressedFiltered())
				{
					tiles.remove(getTile(screenToWorldX(Game.instance().input.getMouseX()), screenToWorldY(Game.instance().input.getMouseY())));
				}
				
				int ordinalDir = 0;
				
				if (Game.instance().input.mouseWheelDown.isPressedFiltered())
				{
					ordinalDir -= speed;
				}
				
				if (Game.instance().input.mouseWheelUp.isPressedFiltered())
				{
					ordinalDir += speed;
				}
				
				// If it goes over the index bounds, loop back to 0
				currentBuildTextureOrdinal += ordinalDir;
				
				if (currentBuildTextureOrdinal > Assets.values().length - 1)
				{
					currentBuildTextureOrdinal = 0;
				}
				
				if (currentBuildTextureOrdinal < 0)
				{
					currentBuildTextureOrdinal = Assets.values().length - 1;
				}
				
				while (Assets.getTexture(Assets.fromOrdinal(currentBuildTextureOrdinal)) == null)
				{
					currentBuildTextureOrdinal += ordinalDir;
					
					if (currentBuildTextureOrdinal > Assets.values().length - 1)
					{
						currentBuildTextureOrdinal = 0;
					}
					
					if (currentBuildTextureOrdinal < 0)
					{
						currentBuildTextureOrdinal = Assets.values().length - 1;
					}
				}
			}
			
			// PointLight mode
			else if (currentBuildMode == 2)
			{
				if (Game.instance().input.mouseLeft.isPressedFiltered())
				{
					PointLight light = new PointLight(screenToWorldX(Game.instance().input.getMouseX()), screenToWorldY(Game.instance().input.getMouseY()), currentBuildLightRadius);
					light.addToLevel(this);
				}
				
				if (Game.instance().input.mouseRight.isPressedFiltered())
				{
					lights.remove(getLight(screenToWorldX(Game.instance().input.getMouseX()), screenToWorldY(Game.instance().input.getMouseY())));
				}
				
				if (Game.instance().input.mouseWheelDown.isPressedFiltered())
				{
					currentBuildLightRadius -= speed;
					if (currentBuildLightRadius < 1) currentBuildLightRadius = 1;
				}
				
				if (Game.instance().input.mouseWheelUp.isPressedFiltered())
				{
					currentBuildLightRadius += speed;
				}
			}
			
			// HitBox mode
			else if (currentBuildMode == 3)
			{
				if (Game.instance().input.mouseLeft.isPressedFiltered())
				{
					if (hitboxClickCount == 0)
					{
						hitboxX1 = screenToWorldX(Game.instance().input.getMouseX());
						hitboxY1 = screenToWorldY(Game.instance().input.getMouseY());
						hitboxClickCount = 1;
					}
					else if (hitboxClickCount == 1)
					{
						levelHitboxes.add(new Rectangle(hitboxX1, hitboxY1, Game.instance().input.getMouseX() - worldToScreenX(hitboxX1) + 1, Game.instance().input.getMouseY() - worldToScreenY(hitboxY1) + 1));
						hitboxClickCount = 0;
					}
				}
				
				if (Game.instance().input.mouseRight.isPressedFiltered())
				{
					if (hitboxClickCount == 1)
					{
						hitboxClickCount = 0;
					}
					else
					{
						levelHitboxes.remove(getHitbox(screenToWorldX(Game.instance().input.getMouseX()), screenToWorldY(Game.instance().input.getMouseY())));
					}
				}
			}
		}
		
		// Resets the click count if the mode is exited
		if (currentBuildMode != 3 || !Game.instance().isBuildMode())
		{
			hitboxClickCount = 0;
		}
		
		// Don't update entities if it's in building mode
		if (!Game.instance().isBuildMode())
		{
			for (LevelObject entity : entities)
			{
				entity.tick();
			}
		}
		
		for (PointLight light : lights)
		{
			light.tick();
		}
		
		// playerLight.setX(Game.instance().player.getX() + 8);
		// playerLight.setY(Game.instance().player.getY() + 8);
	}
	
	public int screenToWorldX(int x)
	{
		return x + xOffset;
	}
	
	public int screenToWorldY(int y)
	{
		return y + yOffset;
	}
	
	public int worldToScreenX(int x)
	{
		return x - xOffset;
	}
	
	public int worldToScreenY(int y)
	{
		return y - yOffset;
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
	
	public Rectangle getHitbox(int x, int y)
	{
		for (Rectangle box : levelHitboxes)
		{
			if (box.contains(x, y))
			{
				return box;
			}
		}
		
		return null;
	}
	
	public Tile getTile(int x, int y)
	{
		for (Tile tile : tiles)
		{
			int width = tile.getTexture().getWidth();
			int height = tile.getTexture().getHeight();
			
			if (new Rectangle(tile.getX(), tile.getY(), width, height).contains(x, y))
			{
				return tile;
			}
		}
		
		return null;
	}
	
	public PointLight getLight(int x, int y)
	{
		for (PointLight light : lights)
		{
			int width = light.getWidth();
			int height = light.getHeight();
			
			if (new Rectangle(light.getX() - light.getRadius(), light.getY() - light.getRadius(), width, height).contains(x, y))
			{
				return light;
			}
		}
		
		return null;
	}
	
	public List<Tile> getTiles()
	{
		return tiles;
	}
	
	public List<Rectangle> getLevelHitboxes()
	{
		return levelHitboxes;
	}
}
