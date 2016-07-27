package ca.afroman.level;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.events.IEvent;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.PointLight;
import ca.afroman.log.ALogType;
import ca.afroman.server.ServerGame;
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
	/** Scripted Events in this level. */
	private List<IEvent> events;
	
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
		events = new ArrayList<IEvent>();
	}
	
	public void tick()
	{
		List<List<Entity>> tiles = getTiles();
		
		for (List<Entity> tileList : tiles)
		{
			for (Entity tile : tileList)
			{
				tile.tick();
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
							ServerGame.instance().logger().log(ALogType.WARNING, "[LEVEL] [ERROR] Invalid LevelObjectType at line " + lineNum + ": " + line);
							break;
						case LEVEL: // ()
							// int x = Integer.parseInt(parameters[0]); TODO add spawn points for each level where the players will respawn at if they die. If they saved after they passed a checkpoint (spawnpoint) then put them back to the save point
							// int y = Integer.parseInt(parameters[1]);
							
							level = new Level(levelType);
							break;
						case TILE: // (AssetType, x, y, width, height, hitbox ...)
						{
							byte layer = Byte.parseByte(parameters[0]);
							double x = Double.parseDouble(parameters[1]);
							double y = Double.parseDouble(parameters[2]);
							AssetType type = AssetType.valueOf(parameters[3]);
							
							new Entity(Entity.getIDCounter().getNext(), type, x, y).addTileToLevel(level, layer);
							
							// If it has custom hitboxes defined
							// if (parameters.length > 4)
							// {
							// List<Hitbox> tileHitboxes = new ArrayList<Hitbox>();
							//
							// for (int i = 4; i < parameters.length; i += 4)
							// {
							// tileHitboxes.add(new Hitbox(Double.parseDouble(parameters[i]), Double.parseDouble(parameters[i + 1]), Double.parseDouble(parameters[i + 2]), Double.parseDouble(parameters[i + 3])));
							// }
							// // TODO pack new Entity(Entity.getNextAvailableID(), type, x, y, Entity.hitBoxListToArray(tileHitboxes)).addTileToLevel(level, layer);
							// }
							// else
							// {
							// // TODO pack new Entity(Entity.getNextAvailableID(), type, x, y).addTileToLevel(level, layer);
							// }
						}
							break;
						case HITBOX:
							level.getHitboxes().add(new Hitbox(Hitbox.getIDCounter().getNext(), Double.parseDouble(parameters[0]), Double.parseDouble(parameters[1]), Double.parseDouble(parameters[2]), Double.parseDouble(parameters[3])));
							break;
						case POINT_LIGHT:
							double x = Double.parseDouble(parameters[0]);
							double y = Double.parseDouble(parameters[1]);
							double radius = Double.parseDouble(parameters[2]);
							
							new PointLight(PointLight.getIDCounter().getNext(), x, y, radius).addToLevel(level);
							break;
					}
				}
				catch (Exception e)
				{
					ServerGame.instance().logger().log(ALogType.CRITICAL, "Level failed to load line " + lineNum + ": " + line, e);
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
				String tileString = LevelObjectType.TILE + "(" + i + ", " + tile.getX() + ", " + tile.getY() + ", " + tile.getAssetType();
				
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
		
		List<PointLight> lights = getLights();
		
		for (PointLight light : lights)
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
	
	public List<IEvent> getScriptedEvents()
	{
		return events;
	}
	
	/**
	 * Gets a hitbox at the given coordinates.
	 * 
	 * @param x the x in-level ordinate
	 * @param y the y in-level ordinate
	 * @return the entity. <b>null</b> if there are no entities at that given location.
	 */
	public IEvent getScriptedEvent(double x, double y)
	{
		for (IEvent event : getScriptedEvents())
		{
			if (new Rectangle2D.Double(event.getX(), event.getY(), event.getWidth(), event.getHeight()).contains(x, y)) return event;
		}
		return null;
	}
	
	/**
	 * Gets a hitbox with the given id.
	 * 
	 * @param id the id of the hitbox
	 * @return the hitbox. <b>null</b> if there are no hitboxes with the given id.
	 */
	public IEvent getScriptedEvent(int id)
	{
		for (IEvent box : getScriptedEvents())
		{
			if (box.getID() == id) return box;
		}
		return null;
	}
	
	public void chainScriptedEvents(int inTrigger)
	{
		// TODO detect infinite loops?
		for (IEvent event : getScriptedEvents())
		{
			for (int eventTrigger : event.getInTriggers())
			{
				// If the event has the specified eventID
				if (eventTrigger == inTrigger)
				{
					// Trigger it
					event.onTrigger();
					
					// Pass trigger to the event's chain triggers
					for (int eventChainTrigger : event.getOutTriggers())
					{
						chainScriptedEvents(eventChainTrigger);
					}
					return;
				}
			}
		}
	}
	
	public List<List<Entity>> getTiles()
	{
		return tiles;
	}
	
	public List<Entity> getTiles(byte layer)
	{
		return tiles.get(layer);
	}
	
	/**
	 * Gets a tile at the given coordinates.
	 * 
	 * @param x the x in-level ordinate
	 * @param y the y in-level ordinate
	 * @return the tile. <b>null</b> if there are no tiles at that given location.
	 */
	public Entity getTile(byte layer, double x, double y)
	{
		List<Entity> tiles = getTiles(layer);
		
		Collections.reverse(tiles);
		
		for (Entity tile : tiles)
		{
			// TODO generate removable hitboxes for tiles based on asset
			Hitbox surrounding = new Hitbox(tile.getX(), tile.getY(), 16, 16);
			
			if (surrounding.contains(x, y))
			{
				Collections.reverse(tiles);
				return tile;
			}
		}
		Collections.reverse(tiles);
		return null;
	}
	
	/**
	 * Gets a tile with the given id.
	 * 
	 * @param id the id of the tile
	 * @return the entity. <b>null</b> if there are no tiles with the given id.
	 */
	public Entity getTile(int id)
	{
		for (List<Entity> tileList : tiles)
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
		Collections.reverse(getHitboxes());
		
		for (Hitbox hitbox : getHitboxes())
		{
			if (hitbox.contains(x, y))
			{
				Collections.reverse(getHitboxes());
				return hitbox;
			}
		}
		
		Collections.reverse(getHitboxes());
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
		for (PointLight light : lights)
		{
			if (light.getID() == id) return light;
		}
		return null;
	}
	
	public PointLight getLight(double x, double y)
	{
		Collections.reverse(lights);
		
		for (PointLight light : lights)
		{
			double radius = light.getRadius();
			
			if (light.getID() != -1 && new Hitbox(light.getX() - radius, light.getY() - radius, (radius * 2) - 1, (radius * 2) - 1).contains(x, y))
			{
				Collections.reverse(lights);
				return light;
			}
		}
		
		Collections.reverse(lights);
		return null;
	}
	
	public List<PointLight> getLights()
	{
		return lights;
	}
}
