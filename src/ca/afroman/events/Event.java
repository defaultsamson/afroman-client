package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.level.api.Level;
import ca.afroman.resource.IDCounter;
import ca.afroman.resource.ServerClientObject;

public class Event extends ServerClientObject
{
	private static IDCounter serverIdCounter = new IDCounter();
	private static IDCounter clientIdCounter = new IDCounter();
	
	public static IDCounter getIDCounter(boolean isServerSide)
	{
		return isServerSide ? serverIdCounter : clientIdCounter;
	}
	
	protected Level level;
	protected List<Integer> inTriggers;
	protected List<Integer> outTriggers;
	protected Hitbox hitbox;
	private int id;
	
	public Event(boolean isServerSide, int id, double x, double y, double width, double height, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		super(isServerSide);
		
		level = null;
		this.inTriggers = (inTriggers != null ? inTriggers : new ArrayList<Integer>());
		this.outTriggers = (outTriggers != null ? outTriggers : new ArrayList<Integer>());
		hitbox = new Hitbox(true, x, y, width, height);
		this.id = id;
	}
	
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		if (level != null)
		{
			level.getEvents().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			level.getEvents().add(this);
		}
	}
	
	/**
	 * @return the hitbox which corresponds with this event.
	 */
	public Hitbox getHitbox()
	{
		return hitbox;
	}
	
	/**
	 * @return the ID of this event. FOR SERVER/CLIENT REFERENCE ONLY, it does not act as a trigger.
	 */
	public int getID()
	{
		return id;
	}
	
	/**
	 * @return the ID's that will trigger this.
	 */
	public List<Integer> getInTriggers()
	{
		return inTriggers;
	}
	
	public Level getLevel()
	{
		return level;
	}
	
	/**
	 * @return the ID's that, when this is triggered, will pass on to other event in the current level.
	 */
	public List<Integer> getOutTriggers()
	{
		return outTriggers;
	}
	
	/**
	 * Runs when this is triggered.
	 * <img src="https://i.imgur.com/dNVvntX.gif" alt="hHHHHHHH" height="120" width="120">
	 * <a href="https://i.imgur.com/dNVvntX.gif">ono</a>
	 */
	public void onTrigger(Entity triggerer)
	{
		
	}
	
	public void removeFromLevel()
	{
		addToLevel(null);
	}
	
	public void setInTriggers(List<Integer> trigs)
	{
		inTriggers = trigs;
	}
	
	public void setOutTriggers(List<Integer> trigs)
	{
		outTriggers = trigs;
	}
	
	public void tick()
	{
		
	}
	
	/**
	 * Triggers this.
	 */
	public void trigger(Entity triggerer)
	{
		onTrigger(triggerer);
		
		for (int out : getOutTriggers())
		{
			// TODO chain for all levels
			level.chainEvents(triggerer, out);
		}
	}
}
