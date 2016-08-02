package ca.afroman.events;

import java.util.List;

import ca.afroman.level.Level;

public interface IEvent
{
	/**
	 * Only ticks after triggered.
	 */
	public void tick();
	
	/**
	 * Triggers
	 * <img src="https://i.imgur.com/dNVvntX.gif" alt="Smiley face" height="120" width="120">
	 * <a href="https://i.imgur.com/dNVvntX.gif">ono</a>
	 */
	public void onTrigger();
	
	/**
	 * @return the ID of this event. FOR SERVER/CLIENT REFERENCE ONLY, it does not act as a trigger.
	 */
	public int getID();
	
	/**
	 * @return the x position of this event.
	 */
	public double getX();
	
	/**
	 * @return the y position of this event.
	 */
	public double getY();
	
	/**
	 * @return the width of this event's area.
	 */
	public double getWidth();
	
	/**
	 * @return the height of this event's area.
	 */
	public double getHeight();
	
	/**
	 * @return the ID's that will trigger this.
	 */
	public List<Integer> getInTriggers();
	
	/**
	 * @return the ID's that, when this is triggered, will pass on to other event in the current level.
	 */
	public List<Integer> getOutTriggers();
	
	public void removeFromLevel();
	
	public void addToLevel(Level level);
}
