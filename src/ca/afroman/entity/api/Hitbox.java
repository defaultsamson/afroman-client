package ca.afroman.entity.api;

import java.awt.geom.Rectangle2D;

import ca.afroman.level.api.Level;

public class Hitbox extends Rectangle2D.Double
{
	private static final long serialVersionUID = -318324421701678550L;
	
	private boolean isMicroManaged;
	public Level level = null;
	
	/**
	 * A level hitbox. This is invisible to standard hitbox operations such as adding and removing (excluding collision. Collision still is active against this hitbox)
	 * 
	 * @param id
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public Hitbox(boolean isMicroManaged, double x, double y, double width, double height)
	{
		super(x, y, width, height);
		
		this.isMicroManaged = isMicroManaged;
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
		Hitbox box = new Hitbox(isMicroManaged, x, y, width, height);
		box.addToLevel(level);
		return box;
	}
	
	public Level getLevel()
	{
		return level;
	}
	
	/**
	 * @return if this Hitbox is managed by a manager such as a HitboxToggle object.
	 */
	public boolean isMicroManaged()
	{
		return isMicroManaged;
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
