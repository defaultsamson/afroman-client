package ca.afroman.entity.api;

import java.awt.geom.Rectangle2D;

import ca.afroman.level.api.Level;

public class Hitbox extends Rectangle2D.Double
{
	private static final long serialVersionUID = -318324421701678550L;
	
	private boolean isMicroManaged;
	public Level level = null;
	
	/**
	 * A hitbox.
	 * <p>
	 * TODO make a better Hitbox system (extend Entity)
	 * 
	 * @param isMicromanaged whether this is managed by an external manager (such as an Event), as opposed to directly being managed by the level
	 * @param x the x ordinate of this
	 * @param y the y ordinate of this
	 * @param width the width of this
	 * @param height the height of this
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
	
	/**
	 * @return
	 */
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
