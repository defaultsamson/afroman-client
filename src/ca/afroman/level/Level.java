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
import ca.afroman.entity.api.IServerClient;
import ca.afroman.events.HitboxTrigger;
import ca.afroman.events.IEvent;
import ca.afroman.events.TriggerType;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.PointLight;
import ca.afroman.log.ALogType;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.server.ServerGame;
import ca.afroman.util.FileUtil;

public class Level implements IServerClient
{
	private static final String LEVEL_DIR = "/level/";
	
	public static Level fromFile(boolean isServerSide, LevelType levelType)
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
					String[] split = line.split("\\(");
					LevelObjectType objectType = LevelObjectType.valueOf(split[0]);
					String[] split2 = split[1].split("\\)");
					String rawParameters = split2.length > 0 ? split2[0] : "";
					String[] parameters = getParameters(rawParameters);
					
					switch (objectType)
					{
						default:
							ServerGame.instance().logger().log(ALogType.WARNING, "[LEVEL] [ERROR] Invalid LevelObjectType at line " + lineNum + ": " + line);
							break;
						case LEVEL: // ()
							// int x = Integer.parseInt(parameters[0]); TODO add spawn points for each level where the players will respawn at if they die. If they saved after they passed a checkpoint (spawnpoint) then put them back to the save point
							// int y = Integer.parseInt(parameters[1]);
							
							level = new Level(isServerSide, levelType);
							break;
						case TILE: // (AssetType, x, y, width, height, hitbox ...)
						{
							byte layer = Byte.parseByte(parameters[0]);
							Vector2DDouble pos = new Vector2DDouble(Double.parseDouble(parameters[1]), Double.parseDouble(parameters[2]));
							AssetType type = AssetType.valueOf(parameters[3]);
							
							new Entity(isServerSide, Entity.getIDCounter().getNext(), type, pos).addTileToLevel(level, layer);
							
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
						{
							Vector2DDouble pos = new Vector2DDouble(Double.parseDouble(parameters[0]), Double.parseDouble(parameters[1]));
							double radius = Double.parseDouble(parameters[2]);
							
							new PointLight(isServerSide, PointLight.getIDCounter().getNext(), pos, radius).addToLevel(level);
						}
							break;
						case HITBOX_TRIGGER:
						{
							double x = Double.parseDouble(parameters[0]);
							double y = Double.parseDouble(parameters[1]);
							double width = Double.parseDouble(parameters[2]);
							double height = Double.parseDouble(parameters[3]);
							
							String[] rSubParameters = getRawSubParameters(rawParameters);
							
							List<TriggerType> triggerTypes = new ArrayList<TriggerType>();
							String[] triggerParameters = getParameters(rSubParameters[0]);
							if (triggerParameters != null)
							{
								for (String e : triggerParameters)
								{
									triggerTypes.add(TriggerType.valueOf(e));
								}
							}
							
							List<Integer> inTriggers = new ArrayList<Integer>();
							String[] inTriggerP = getParameters(rSubParameters[1]);
							if (inTriggerP != null)
							{
								for (String e : inTriggerP)
								{
									inTriggers.add(Integer.parseInt(e));
								}
							}
							
							List<Integer> outTriggers = new ArrayList<Integer>();
							String[] outTriggerP = getParameters(rSubParameters[2]);
							if (outTriggerP != null)
							{
								for (String e : outTriggerP)
								{
									outTriggers.add(Integer.parseInt(e));
								}
							}
							
							new HitboxTrigger(isServerSide, HitboxTrigger.getIDCounter().getNext(), x, y, width, height, triggerTypes, inTriggers, outTriggers).addToLevel(level);;
						}
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
	
	public static String[] getParameters(String in)
	{
		return in.length() > 0 ? in.split(", ") : null;
	}
	
	public static String[] getRawSubParameters(String in)
	{
		int count = in.split("\\{").length - 1;
		
		if (count >= 1)
		{
			String[] ret = new String[count];
			
			// isolates all the sub-parameters
			for (int i = 0; i < count; i++)
			{
				String[] r1 = in.split("\\{")[1 + i].split("\\}");
				ret[i] = r1.length > 0 ? r1[0] : "";
			}
			
			return ret;
		}
		else
		{
			return null;
		}
	}
	
	private boolean isServerSide;
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
	
	public Level(boolean isServerSide, LevelType type)
	{
		this.isServerSide = isServerSide;
		this.type = type;
		
		lights = new ArrayList<PointLight>();
		tiles = new ArrayList<List<Entity>>(6);
		
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
	
	public void chainScriptedEvents(Entity triggerer, int inTrigger)
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
					event.trigger(triggerer);
					break;
				}
			}
		}
	}
	
	public List<Entity> getEntities()
	{
		return entities;
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
	
	/**
	 * Gets an entity at the given coordinates.
	 * 
	 * @param pos the position
	 * @return the entity. <b>null</b> if there are no entities at that given location.
	 */
	public Entity getEntity(Vector2DDouble pos)
	{
		for (Entity entity : getEntities())
		{
			for (Hitbox hitbox : entity.hitboxInLevel())
			{
				if (hitbox.contains(pos.getX(), pos.getY())) return entity;
			}
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
	 * Gets a hitbox at the given coordinates.
	 * 
	 * @param pos the position
	 * @return the entity. <b>null</b> if there are no hitboxes at that given location.
	 */
	public Hitbox getHitbox(Vector2DDouble pos)
	{
		Collections.reverse(getHitboxes());
		
		for (Hitbox hitbox : getHitboxes())
		{
			if (hitbox.contains(pos.getX(), pos.getY()))
			{
				Collections.reverse(getHitboxes());
				return hitbox;
			}
		}
		
		Collections.reverse(getHitboxes());
		return null;
	}
	
	public List<Hitbox> getHitboxes()
	{
		return hitboxes;
	}
	
	/**
	 * Gets a light with the given id.
	 * 
	 * @param id the id of the hitbox
	 * @return the hitbox. <b>null</b> if there are no lights with the given id.
	 */
	public PointLight getLight(int id)
	{
		for (PointLight light : lights)
		{
			if (light.getID() == id) return light;
		}
		return null;
	}
	
	/**
	 * Gets a PointLight at the given coordinates.
	 * 
	 * @param pos the position
	 * @return the entity. <b>null</b> if there are no PointLights at that given location.
	 */
	public PointLight getLight(Vector2DDouble pos)
	{
		Collections.reverse(lights);
		
		for (PointLight light : lights)
		{
			double radius = light.getRadius();
			
			double xa = (pos.getX() - light.getPosition().getX());
			double ya = (pos.getY() - light.getPosition().getY());
			
			// If the light is not unnamed
			if (light.getID() != -1) ;
			{
				// If the light contains the point
				// Old method, creates a square hitbox to check for collision
				// if (light.getID() != -1 && new Hitbox(light.getX() - radius, light.getY() - radius, (radius * 2) - 1, (radius * 2) - 1).contains(x, y))
				if (xa * xa + ya * ya < radius * radius) // (x - center_x)^2 + (y - center_y)^2 < radius^2
				{
					Collections.reverse(lights);
					return light;
				}
			}
		}
		
		Collections.reverse(lights);
		return null;
	}
	
	public List<PointLight> getLights()
	{
		return lights;
	}
	
	public List<Entity> getPlayers()
	{
		return players;
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
	
	/**
	 * Gets a hitbox at the given coordinates.
	 * 
	 * @param x the x in-level ordinate
	 * @param y the y in-level ordinate
	 * @return the entity. <b>null</b> if there are no entities at that given location.
	 */
	public IEvent getScriptedEvent(Vector2DDouble pos)
	{
		for (IEvent event : getScriptedEvents())
		{
			if (new Rectangle2D.Double(event.getX(), event.getY(), event.getWidth(), event.getHeight()).contains(pos.getX(), pos.getY())) return event;
		}
		return null;
	}
	
	// public void addEntityBehind(Entity entity)
	// {
	// Collections.reverse(entities);
	// entities.add(entity);
	// Collections.reverse(entities);
	// }
	
	public List<IEvent> getScriptedEvents()
	{
		return events;
	}
	
	/**
	 * Gets a tile at the given coordinates.
	 * 
	 * @param x the x in-level ordinate
	 * @param y the y in-level ordinate
	 * @return the tile. <b>null</b> if there are no tiles at that given location.
	 */
	public Entity getTile(byte layer, Vector2DDouble pos)
	{
		List<Entity> tiles = getTiles(layer);
		
		Collections.reverse(tiles);
		
		for (Entity tile : tiles)
		{
			// TODO generate removable hitboxes for tiles based on asset
			Hitbox surrounding = new Hitbox(tile.getPosition().getX(), tile.getPosition().getY(), 16, 16);
			
			if (surrounding.contains(pos.getX(), pos.getY()))
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
	
	public List<List<Entity>> getTiles()
	{
		return tiles;
	}
	
	public List<Entity> getTiles(byte layer)
	{
		return tiles.get(layer);
	}
	
	public LevelType getType()
	{
		return type;
	}
	
	@Override
	public boolean isServerSide()
	{
		return isServerSide;
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
		
		for (IEvent event : getScriptedEvents())
		{
			event.tick();
		}
	}
	
	public List<String> toSaveFile()
	{
		List<String> toReturn = new ArrayList<String>();
		
		toReturn.add(LevelObjectType.LEVEL + "()");
		
		toReturn.add("");
		toReturn.add("");
		toReturn.add("// The Tiles. LevelObjectType(layer, AssetType, x, y, width, height, hitboxes[if any])");
		toReturn.add("");
		
		int layer = 0;
		for (List<Entity> tileList : getTiles())
		{
			for (Entity tile : tileList)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(LevelObjectType.TILE.toString());
				sb.append('(');
				sb.append(layer);
				sb.append(", ");
				sb.append(tile.getPosition().getX());
				sb.append(", ");
				sb.append(tile.getPosition().getY());
				sb.append(", ");
				sb.append(tile.getAssetType().toString());
				
				if (tile.hasHitbox())
				{
					sb.append(tile.hitboxesAsSaveable());
				}
				
				sb.append(')');
				
				toReturn.add(sb.toString());
			}
			layer++;
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
				toReturn.add(LevelObjectType.POINT_LIGHT + "(" + light.getPosition().getX() + ", " + light.getPosition().getY() + ", " + light.getRadius() + ")");
			}
		}
		
		toReturn.add("");
		toReturn.add("");
		toReturn.add("// The HitboxTriggers. HitboxTrigger(x, y, width, height, triggerTypes, inTriggers, outTriggers)");
		toReturn.add("");
		
		for (IEvent e : getScriptedEvents())
		{
			if (e instanceof HitboxTrigger)
			{
				HitboxTrigger t = (HitboxTrigger) e;
				
				StringBuilder sb = new StringBuilder();
				sb.append(LevelObjectType.HITBOX_TRIGGER.toString());
				sb.append('(');
				sb.append(e.getX());
				sb.append(", ");
				sb.append(e.getY());
				sb.append(", ");
				sb.append(e.getWidth());
				sb.append(", ");
				sb.append(e.getHeight());
				sb.append(", {");
				
				// Saves trigger types
				for (int k = 0; k < t.getTriggerTypes().size(); k++)
				{
					sb.append(t.getTriggerTypes().get(k).toString());
					if (k != t.getTriggerTypes().size() - 1) sb.append(", ");
				}
				
				sb.append("}, {");
				
				// Saves in triggers
				for (int k = 0; k < t.getInTriggers().size(); k++)
				{
					sb.append(t.getInTriggers().get(k));
					if (k != t.getInTriggers().size() - 1) sb.append(", ");
				}
				
				sb.append("}, {");
				
				// Saves out triggers
				for (int k = 0; k < t.getOutTriggers().size(); k++)
				{
					sb.append(t.getOutTriggers().get(k));
					if (k != t.getOutTriggers().size() - 1) sb.append(", ");
				}
				
				sb.append("})");
				
				toReturn.add(sb.toString());
			}
		}
		
		// Copies the level data to the clipboard
		StringBuilder sb = new StringBuilder();
		for (String copy : toReturn)
		{
			sb.append(copy);
			sb.append("\n");
		}
		StringSelection stringSelection = new StringSelection(sb.toString());
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
		
		return toReturn;
	}
}
