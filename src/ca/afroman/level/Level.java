package ca.afroman.level;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.gfx.PointLight;

public class Level
{
	private static final String LEVEL_DIR = "/level/";
	
	/** The level identified. */
	private LevelType type;
	/** PointLights in this Level. */
	private List<PointLight> lights;
	/** Entities that cannot be interacted with, and will always be there. */
	private List<Entity> tiles;
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
		tiles = new ArrayList<Entity>();
		entities = new ArrayList<Entity>();
		players = new ArrayList<Entity>();
		hitboxes = new ArrayList<Hitbox>();
	}
	
	public synchronized void tick()
	{
		for (Entity tile : tiles)
		{
			tile.tick();
		}
		
		for (Entity entity : entities)
		{
			entity.tick();
		}
		
		for (Entity entity : players)
		{
			entity.tick();
		}
	}
	
	public static Level fromFile(LevelType levelType)
	{
		Level level = null;
		
		try
		{
			// Loads the file
			InputStream in = Level.class.getResourceAsStream(LEVEL_DIR + levelType.getFileName());
			
			// Puts the file's contents into a temp file
			File tempFile = File.createTempFile(levelType.getFileName(), ".tmplv");
			FileOutputStream out = new FileOutputStream(tempFile);
			
			byte[] buffer = new byte[1024];
			
			int size = 0;
			while ((size = in.read(buffer)) > -1)
			{
				out.write(buffer, 0, size);
			}
			
			in.close();
			out.close();
			
			// Loads all the lines from the temp file
			List<String> lines = Files.readAllLines(tempFile.toPath());
			
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
								double x = Double.parseDouble(parameters[0]);
								double y = Double.parseDouble(parameters[1]);
								double width = Double.parseDouble(parameters[2]);
								double height = Double.parseDouble(parameters[3]);
								AssetType type = AssetType.valueOf(parameters[4]);
								
								// If it has custom hitboxes defined
								if (parameters.length > 5)
								{
									List<Hitbox> tileHitboxes = new ArrayList<Hitbox>();
									
									for (int i = 4; i < parameters.length; i += 4)
									{
										tileHitboxes.add(new Hitbox(Double.parseDouble(parameters[i]), Double.parseDouble(parameters[i + 1]), Double.parseDouble(parameters[i + 2]), Double.parseDouble(parameters[i + 3])));
									}
									
									level.getTiles().add(new Entity(Entity.getNextAvailableID(), level, type, x, y, width, height, Entity.hitBoxListToArray(tileHitboxes)));
								}
								else
								{
									level.getTiles().add(new Entity(Entity.getNextAvailableID(), level, type, x, y, width, height));
								}
							}
								break;
							case HITBOX:
								level.getHitboxes().add(new Hitbox(Hitbox.getNextAvailableID(), Double.parseDouble(parameters[0]), Double.parseDouble(parameters[1]), Double.parseDouble(parameters[2]), Double.parseDouble(parameters[3])));
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
			
			tempFile.delete();
		}
		catch (
		
		IOException e)
		{
			e.printStackTrace();
		}
		
		return level;
	}
	
	public List<String> toSaveFile()
	{
		List<String> toReturn = new ArrayList<String>();
		
		toReturn.add(LevelObjectType.LEVEL + "()");
		
		toReturn.add("");
		toReturn.add("");
		toReturn.add("// The Tiles. LevelObjectType(AssetType, x, y, width, height, hitboxes[if any])");
		toReturn.add("");
		
		for (Entity tile : getTiles())
		{
			String tileString = LevelObjectType.TILE + "(" + tile.getX() + ", " + tile.getY() + ", " + tile.getWidth() + ", " + tile.getHeight() + ", " + tile.getAssetType();
			
			if (tile.hasHitbox())
			{
				tileString += tile.hitboxesAsSaveable();
			}
			
			tileString += ")";
			
			toReturn.add(tileString);
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
		// for (PointLight light : lights)
		// {
		// if (!light.equals(this.playerLight))
		// {
		// toReturn.add("PointLight(" + light.getX() + ", " + light.getY() + ", " + light.getRadius() + ")");
		// }
		// }
		
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
	
	public synchronized List<Entity> getTiles()
	{
		return tiles;
	}
	
	/**
	 * Gets a tile at the given coordinates.
	 * 
	 * @param x the x in-level ordinate
	 * @param y the y in-level ordinate
	 * @return the tile. <b>null</b> if there are no tiles at that given location.
	 */
	public Entity getTile(double x, double y)
	{
		Collections.reverse(getTiles());
		
		for (Entity tile : getTiles())
		{
			Hitbox surrounding = new Hitbox(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight());
			
			if (surrounding.contains(x, y))
			{
				Collections.reverse(getTiles());
				return tile;
			}
		}
		Collections.reverse(getTiles());
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
		for (Entity tile : getTiles())
		{
			if (tile.getID() == id) return tile;
		}
		return null;
	}
	
	public void addTileBehind(Entity tile)
	{
		Collections.reverse(getTiles());
		getTiles().add(tile);
		Collections.reverse(getTiles());
	}
	
	public synchronized List<Entity> getEntities()
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
	
	public synchronized List<Hitbox> getHitboxes()
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
	
	public synchronized List<Entity> getPlayers()
	{
		return players;
	}
	//
	// /**
	// * Gets the player at the given coordinates.
	// *
	// * @param x the x in-level ordinate
	// * @param y the y in-level ordinate
	// * @return the player. <b>null</b> if there are no players at that given location.
	// */
	// public Entity getPlayer(double x, double y)
	// {
	// for (Entity entity : players)
	// {
	// if (entity instanceof ServerPlayerEntity)
	// {
	// for (Hitbox hitbox : entity.hitboxInLevel())
	// {
	// if (hitbox.contains(x, y)) return entity;
	// }
	// }
	// else
	// {
	// System.out.println("[LEVEL] Non-PlayerEntity in the player list of level " + this.type);
	// }
	// }
	// return null;
	// }
	//
	// /**
	// * Gets the player with the given role.
	// *
	// * @param role whether it's player 1 or 2
	// * @return the player.
	// */
	// public Entity getPlayer(Role role)
	// {
	// for (Entity entity : getPlayers())
	// {
	// if (entity instanceof ServerPlayerEntity)
	// {
	// if (((ServerPlayerEntity) entity).getRole() == role) return entity;
	// }
	// else
	// {
	// System.out.println("[LEVEL] Non-PlayerEntity in the player list of level " + this.type);
	// }
	// }
	// return null;
	// }
	
	public LevelType getType()
	{
		return type;
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
	
	public synchronized List<PointLight> getLights()
	{
		return lights;
	}
}
