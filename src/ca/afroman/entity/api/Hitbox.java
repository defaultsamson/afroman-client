package ca.afroman.entity.api;

import java.awt.geom.Rectangle2D;

import ca.afroman.level.Level;
import ca.afroman.util.IDCounter;

public class Hitbox extends Rectangle2D.Double
{
	private static IDCounter idCounter = new IDCounter();
	
	private static final long serialVersionUID = -318324421701678550L;
	
	public static IDCounter getIDCounter()
	{
		return idCounter;
	}
	
	// All the required variables needed to create an Entity
	private int id;
	
	public Level level = null;
	
	// TODO use a Vector2DDouble instead
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
	 * Removes this hitbox from its current level and puts it in another level.
	 * 
	 * @param level the new level.
	 */
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		if (level != null)
		{
			level.getHitboxes().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			level.getHitboxes().add(this);
		}
	}
	
	@Override
	public Hitbox clone()
	{
		Hitbox box = new Hitbox(x, y, width, height);
		box.addToLevel(level);
		return box;
	}
	
	/**
	 * @return this hitbox's ID.
	 */
	public int getID()
	{
		return id;
	}
	
	public Level getLevel()
	{
		return level;
	}
	
	/**
	 * Removes this hitbox from its current level.
	 */
	public void removeFromLevel()
	{
		addToLevel(null);
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
