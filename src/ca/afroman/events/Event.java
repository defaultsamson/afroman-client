package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.level.api.Level;
import ca.afroman.resource.Vector2DDouble;

public abstract class Event extends Entity
{
	protected List<Integer> inTriggers;
	protected List<Integer> outTriggers;
	
	public Event(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position, List<Integer> inTriggers, List<Integer> outTriggers, Hitbox... hitboxes)
	{
		super(isServerSide, isMicromanaged, position, hitboxes);
		
		initTriggers(inTriggers, outTriggers);
	}
	
	@Override
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
	 * @return the ID's that will trigger this.
	 */
	public List<Integer> getInTriggers()
	{
		return inTriggers;
	}
	
	@Override
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
	
	private void initTriggers(List<Integer> inTriggers, List<Integer> outTriggers)
	{
		this.inTriggers = (inTriggers != null ? inTriggers : new ArrayList<Integer>());
		this.outTriggers = (outTriggers != null ? outTriggers : new ArrayList<Integer>());
	}
	
	@Override
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
	
	@Override
	public void tick()
	{
		
	}
	
	/**
	 * Triggers this (Runs when triggered).
	 * <p>
	 * Invoke <code>super.trigger(triggerer)<code> <i>after</i> the subclass code when overriding this method.
	 */
	public void trigger(Entity triggerer)
	{
		for (int out : getOutTriggers())
		{
			// TODO chain for all levels?
			level.chainEvents(triggerer, out);
		}
	}
}
