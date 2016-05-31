package ca.afroman.level;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.PointLight;
import ca.afroman.util.FileUtil;

public class Level
{
	private static final String LEVEL_DIR = "/level/";
	
	/** The level identified. */
	private LevelType type;
	/** PointLights in this Level. */
	private List<PointLight> lights;
	/** Entities that cannot be interacted with, and will always be there (All the different layers). */
	private List<List<Entity>> tiles;
	/** Entities that can move, be interacted with, or be removed. */
	private List<Entity> entities;
	/** Player objects. */
	private List<Entity> players;
	/** Hitbox in this Level. */
	private List<Hitbox> hitboxes;
	
	public Level(LevelType type)
	{
		this.type = type;
		
		lights = new ArrayList<PointLight>();
		tiles = new ArrayList<List<Entity>>();
		
		tiles.add(new ArrayList<Entity>());
		tiles.add(new ArrayList<Entity>());
		tiles.add(new ArrayList<Entity>());
		tiles.add(new ArrayList<Entity>());
		tiles.add(new ArrayList<Entity>());
		tiles.add(new ArrayList<Entity>());
		
		entities = new ArrayList<Entity>();
		players = new ArrayList<Entity>();
		hitboxes = new ArrayList<Hitbox>();
	}
	
	public void tick()
	{
		List<List<Entity>> tiles = getTiles();
		
		synchronized (tiles)
		{
			for (List<Entity> tileList : tiles)
			{
				for (Entity tile : tileList)
				{
					tile.tick();
				}
			}
		}
		
		for (Entity entity : getEntities())
		{
			entity.tick();
		}
		
		for (Entity entity : getPlayers())
		{
			entity.tick();
		}
	}
	
	public static Level fromFile(LevelType levelType)
	{
		Level level = null;
		
		List<String> lines = FileUtil.readAllLines(FileUtil.fileFromResource(LEVEL_DIR + levelType.getFileName()));
		
		int lineNum = 1;
		for (String line : lines)
		{
			if (line != null && !line.isEmpty() && !line.equals(" ") && !line.startsWith("//"))
			{
				try
				{
					String[] split1 = line.split("\\(");
					LevelObjectType objectType = LevelObjectType.valueOf(split1[0]);
					String[] split2 = split1[1].split("\\)");
					String[] parameters = split2.length > 0 ? split2[0].split(", ") : null;
					
					switch (objectType)
					{
						default:
							System.err.println("[LEVEL] [ERROR] Invalid LevelObjectType at line " + lineNum + ": " + line);
							break;
						case LEVEL: // ()
							// int x = Integer.parseInt(parameters[0]); TODO add spawn points
							// int y = Integer.parseInt(parameters[1]);
							
							level = new Level(levelType);
							break;
						case TILE: // (AssetType, x, y, width, height, hitbox ...)
						{
							int layer = Integer.parseInt(parameters[0]);
							double x = Double.parseDouble(parameters[1]);
							double y = Double.parseDouble(parameters[2]);
							double width = Double.parseDouble(parameters[3]);
							double height = Double.parseDouble(parameters[4]);
							AssetType type = AssetType.valueOf(parameters[5]);
							
							// If it has custom hitboxes defined
							if (parameters.length > 6)
							{
								List<Hitbox> tileHitboxes = new ArrayList<Hitbox>();
								
								for (int i = 6; i < parameters.length; i += 4)
								{
									tileHitboxes.add(new Hitbox(Double.parseDouble(parameters[i]), Double.parseDouble(parameters[i + 1]), Double.parseDouble(parameters[i + 2]), Double.parseDouble(parameters[i + 3])));
								}
								
								level.getTiles(layer).add(new Entity(Entity.getNextAvailableID(), level, type, x, y, width, height, Entity.hitBoxListToArray(tileHitboxes)));
							}
							else
							{
								level.getTiles(layer).add(new Entity(Entity.getNextAvailableID(), level, type, x, y, width, height));
							}
						}
							break;
						case HITBOX:
							level.getHitboxes().add(new Hitbox(Hitbox.getNextAvailableID(), Double.parseDouble(parameters[0]), Double.parseDouble(parameters[1]), Double.parseDouble(parameters[2]), Double.parseDouble(parameters[3])));
							break;
						case POINT_LIGHT:
							double x = Double.parseDouble(parameters[0]);
							double y = Double.parseDouble(parameters[1]);
							double radius = Double.parseDouble(parameters[2]);
							
							level.getLights().add(new PointLight(Entity.getNextAvailableID(), level, x, y, radius));
							break;
					}
					
					// switch (type)
					// {
					// case "PointLight": TODO
					// {
					// int x = Integer.parseInt(parameters[0]);
					// int y = Integer.parseInt(parameters[1]);
					// int radius = Integer.parseInt(parameters[2]);
					//
					// PointLight light = new PointLight(x, y, radius);
					// light.addToLevel(level);
					// }
					// break;
					// case "HitBox":
					// {
					// level.levelHitboxes.add(new Rectangle(Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]), Integer.parseInt(parameters[2]), Integer.parseInt(parameters[3])));
					// }
					// break;
					// }
				}
				catch (Exception e)
				{
					System.err.println("Level failed to load line " + lineNum + ": " + line);
					e.printStackTrace();
				}
			}
			
			lineNum++;
		}
		
		return level;
	}
	
	public List<String> toSaveFile()
	{
		List<String> toReturn = new ArrayList<String>();
		
		toReturn.add(LevelObjectType.LEVEL + "()");
		
		toReturn.add("");
		toReturn.add("");
		toReturn.add("// The Tiles. LevelObjectType(layer, AssetType, x, y, width, height, hitboxes[if any])");
		toReturn.add("");
		
		int i = 0;
		for (List<Entity> tileList : getTiles())
		{
			for (Entity tile : tileList)
			{
				String tileString = LevelObjectType.TILE + "(" + i + ", " + tile.getX() + ", " + tile.getY() + ", " + tile.getWidth() + ", " + tile.getHeight() + ", " + tile.getAssetType();
				
				if (tile.hasHitbox())
				{
					tileString += tile.hitboxesAsSaveable();
				}
				
				tileString += ")";
				
				toReturn.add(tileString);
			}
			i++;
		}
		
		toReturn.add("");
		toReturn.add("");
		toReturn.add("// The world hitboxes. HitBox(x, y, width, height)");
		toReturn.add("");
		
		for (Hitbox box : getHitboxes())
		{
			toReturn.add(LevelObjectType.HITBOX + "(" + box.getX() + ", " + box.getY() + ", " + box.getWidth() + ", " + box.getHeight() + ")");
		}
		
		// TODO
		// toReturn.add("");
		// toReturn.add("");
		// toReturn.add("// The Entities.");
		// toReturn.add("");
		//
		// for (Entity entity : entities)
		// {
		// entity.getX();
		// }
		//
		// toReturn.add("");
		// toReturn.add("");
		// toReturn.add("// The lights. PointLight(x, y, radius)");
		// toReturn.add("");
		//
		
		toReturn.add("");
		toReturn.add("");
		toReturn.add("// The lights. PointLight(x, y, radius)");
		toReturn.add("// The lights. FlickeringLight(x, y, radius, radius2, ticksPerFrame)");
		toReturn.add("");
		
		for (PointLight light : getLights())
		{
			if (light instanceof FlickeringLight)
			{
				// TODO
			}
			else
			{
				toReturn.add(LevelObjectType.POINT_LIGHT + "(" + light.getX() + ", " + light.getY() + ", " + light.getRadius() + ")");
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
	
	public List<List<Entity>> getTiles()
	{
		return tiles;
	}
	
	public List<Entity> getTiles(int layer)
	{
		return getTiles().get(layer);
	}
	
	/**
	 * Gets a tile at the given coordinates.
	 * 
	 * @param x the x in-level ordinate
	 * @param y the y in-level ordinate
	 * @return the tile. <b>null</b> if there are no tiles at that given location.
	 */
	public Entity getTile(int layer, double x, double y)
	{
		List<Entity> tiles = getTiles(layer);
		
		synchronized (tiles)
		{
			Collections.reverse(tiles);
			
			for (Entity tile : tiles)
			{
				Hitbox surrounding = new Hitbox(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight());
				
				if (surrounding.contains(x, y))
				{
					Collections.reverse(tiles);
					return tile;
				}
			}
			Collections.reverse(tiles);
			return null;
		}
	}
	
	/**
	 * Gets a tile with the given id.
	 * 
	 * @param id the id of the tile
	 * @return the entity. <b>null</b> if there are no tiles with the given id.
	 */
	public Entity getTile(int id)
	{
		for (List<Entity> tileList : getTiles())
		{
			for (Entity tile : tileList)
			{
				if (tile.getID() == id) return tile;
			}
		}
		return null;
	}
	
	public List<Entity> getEntities()
	{
		return entities;
	}
	
	/**
	 * Gets an entity at the given coordinates.
	 * 
	 * @param x the x in-level ordinate
	 * @param y the y in-level ordinate
	 * @return the entity. <b>null</b> if there are no entities at that given location.
	 */
	public Entity getEntity(double x, double y)
	{
		for (Entity entity : getEntities())
		{
			for (Hitbox hitbox : entity.hitboxInLevel())
			{
				if (hitbox.contains(x, y)) return entity;
			}
		}
		return null;
	}
	
	/**
	 * Gets an entity with the given id.
	 * 
	 * @param id the id of the entity
	 * @return the entity. <b>null</b> if there are no entities with the given id.
	 */
	public Entity getEntity(int id)
	{
		for (Entity entity : getEntities())
		{
			if (entity.getID() == id) return entity;
		}
		return null;
	}
	
	// public void addEntityBehind(Entity entity)
	// {
	// Collections.reverse(entities);
	// entities.add(entity);
	// Collections.reverse(entities);
	// }
	
	public List<Hitbox> getHitboxes()
	{
		return hitboxes;
	}
	
	/**
	 * Gets a hitbox at the given coordinates.
	 * 
	 * @param x the x in-level ordinate
	 * @param y the y in-level ordinate
	 * @return the entity. <b>null</b> if there are no entities at that given location.
	 */
	public Hitbox getHitbox(double x, double y)
	{
		for (Hitbox hitbox : getHitboxes())
		{
			if (hitbox.contains(x, y)) return hitbox;
		}
		return null;
	}
	
	/**
	 * Gets a hitbox with the given id.
	 * 
	 * @param id the id of the hitbox
	 * @return the hitbox. <b>null</b> if there are no hitboxes with the given id.
	 */
	public Hitbox getHitbox(int id)
	{
		for (Hitbox box : getHitboxes())
		{
			if (box.getID() == id) return box;
		}
		return null;
	}
	
	/**
	 * Removes a hitbox at the given coordinates.
	 * 
	 * @param x the x in-level ordinate
	 * @param y the y in-level ordinate
	 */
	public void removeHitbox(double x, double y)
	{
		Hitbox hitbox = getHitbox(x, y);
		if (hitbox != null) getHitboxes().remove(hitbox);
	}
	
	public List<Entity> getPlayers()
	{
		return players;
	}
	
	public LevelType getType()
	{
		return type;
	}
	
	/**
	 * Gets a light with the given id.
	 * 
	 * @param id the id of the hitbox
	 * @return the hitbox. <b>null</b> if there are no hitboxes with the given id.
	 */
	public PointLight getLight(int id)
	{
		for (PointLight light : getLights())
		{
			if (light.getID() == id) return light;
		}
		return null;
	}
	
	public PointLight getLight(double x, double y)
	{
		for (PointLight light : getLights())
		{
			double width = light.getWidth();
			double height = light.getHeight();
			
			if (new Hitbox(light.getX() - light.getRadius(), light.getY() - light.getRadius(), width, height).contains(x, y))
			{
				return light;
			}
		}
		
		return null;
	}
	
	public List<PointLight> getLights()
	{
		return lights;
	}
}
