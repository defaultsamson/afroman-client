package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.IServerClient;
import ca.afroman.level.Level;

public class HitboxToggleReceiver implements IEvent, IServerClient
{
	private boolean isServerSide;
	
	private Level level;
	private List<Integer> inTriggers;
	private List<Integer> outTriggers;
	private Hitbox hitbox;
	
	public HitboxToggleReceiver(boolean isServerSide, int id, double x, double y, double width, double height, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		this(isServerSide, id, new Hitbox(id, x, y, width, height), inTriggers, outTriggers);
	}
	
	public HitboxToggleReceiver(boolean isServerSide, int id, Hitbox box, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		this.isServerSide = isServerSide;
		level = null;
		this.inTriggers = (inTriggers != null ? inTriggers : new ArrayList<Integer>());
		this.outTriggers = (outTriggers != null ? outTriggers : new ArrayList<Integer>());
		hitbox = box;
	}
	
	public Hitbox getHitbox()
	{
		return hitbox;
	}
	
	@Override
	public boolean isServerSide()
	{
		return isServerSide;
	}
	
	@Override
	public void tick()
	{
		
	}
	
	@Override
	public void trigger(Entity triggerer)
	{
		onTrigger(triggerer);
		
		for (int out : getOutTriggers())
		{
			// TODO chain for all levels
			getLevel().chainScriptedEvents(triggerer, out);
		}
	}
	
	@Override
	public void onTrigger(Entity triggerer)
	{
		if (hitbox.getLevel() == null)
		{
			hitbox.addToLevel(level);
		}
		else
		{
			hitbox.removeFromLevel();
		}
	}
	
	@Override
	public int getID()
	{
		return hitbox.getID();
	}
	
	@Override
	public double getX()
	{
		return hitbox.getX();
	}
	
	@Override
	public double getY()
	{
		return hitbox.getY();
	}
	
	@Override
	public double getWidth()
	{
		return hitbox.getWidth();
	}
	
	@Override
	public double getHeight()
	{
		return hitbox.getHeight();
	}
	
	@Override
	public List<Integer> getInTriggers()
	{
		return inTriggers;
	}
	
	public void setInTriggers(List<Integer> trigs)
	{
		inTriggers = trigs;
	}
	
	@Override
	public List<Integer> getOutTriggers()
	{
		return outTriggers;
	}
	
	public void setOutTriggers(List<Integer> trigs)
	{
		outTriggers = trigs;
	}
	
	@Override
	public void removeFromLevel()
	{
		addToLevel(null);
	}
	
	@Override
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		if (level != null)
		{
			hitbox.removeFromLevel();
			level.getScriptedEvents().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			hitbox.addToLevel(level);
			level.getScriptedEvents().add(this);
		}
	}
	
	@Override
	public Level getLevel()
	{
		return hitbox.getLevel();
	}
}
