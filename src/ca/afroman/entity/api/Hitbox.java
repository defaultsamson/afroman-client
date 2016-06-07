package ca.afroman.entity.api;

import java.awt.geom.Rectangle2D;

import ca.afroman.level.Level;

public class Hitbox extends Rectangle2D.Double
{
	private static int nextAvailableID = 0;
	
	// All the required variables needed to create an Entity
	private int id;
	
	public Level level = null;
	
	private static final long serialVersionUID = -318324421701678550L;
	
	public Hitbox(double x, double y, double width, double height)
	{
		this(-1, x, y, width, height);
	}
	
	public Hitbox(int id, double x, double y, double width, double height)
	{
		super(x, y, width, height);
		
		this.id = id;
	}
	
	/**
	 * @return this hitbox's ID.
	 */
	public int getID()
	{
		return id;
	}
	
	/**
	 * @return the next available ID for use. (Ignored previous ID's that are now free for use. TODO?)
	 */
	public static int getNextAvailableID()
	{
		int toReturn = nextAvailableID;
		nextAvailableID++;
		return toReturn;
	}
	
	/**
	 * Resets the nextAvailableID so that it starts counting from 0 again.
	 * <p>
	 * <b>WARNING: </b>only intended for use on server shutdowns.
	 */
	public static void resetNextAvailableID()
	{
		nextAvailableID = 0;
	}
	
	/**
	 * Removes this hitbox from its current level.
	 */
	public void removeFromLevel()
	{
		addToLevel(null);
	}
	
	/**
	 * Removes this hitbox from its current level and puts it in another level.
	 * 
	 * @param level the new level.
	 */
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		if (level != null)
		{
			synchronized (level.getHitboxes())
			{
				level.getHitboxes().remove(this);
			}
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			synchronized (level.getHitboxes())
			{
				level.getHitboxes().add(this);
			}
		}
	}
	
	public Level getLevel()
	{
		return level;
	}
	
	/**
	 * <b>WARNING:</b> Used when adding a hitbox bound object to a level. 
	 * ONLY USE THIS IF YOU KNOW WHAT YOU'RE DOING.
	 * 
	 * @param level the new level
	 */
	public void setLevel(Level level)
	{
		this.level = level;
	}
}
