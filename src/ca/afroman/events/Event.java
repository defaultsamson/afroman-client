package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.IServerClient;
import ca.afroman.level.Level;
import ca.afroman.resource.IDCounter;

public class Event implements IServerClient
{
	private static IDCounter idCounter = new IDCounter();
	
	public static IDCounter getIDCounter()
	{
		return idCounter;
	}
	
	private boolean isServerSide;
	
	protected Level level;
	protected List<Integer> inTriggers;
	protected List<Integer> outTriggers;
	protected Hitbox hitbox;
	private int id;
	
	public Event(boolean isServerSide, int id, double x, double y, double width, double height, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		this.isServerSide = isServerSide;
		
		level = null;
		this.inTriggers = (inTriggers != null ? inTriggers : new ArrayList<Integer>());
		this.outTriggers = (outTriggers != null ? outTriggers : new ArrayList<Integer>());
		hitbox = new Hitbox(x, y, width, height);
		this.id = id;
	}
	
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		if (level != null)
		{
			level.getScriptedEvents().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			level.getScriptedEvents().add(this);
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
	
	@Override
	public boolean isServerSide()
	{
		return isServerSide;
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
	
	/**
	 * Only ticks after triggered.
	 */
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
			level.chainScriptedEvents(triggerer, out);
		}
	}
}
