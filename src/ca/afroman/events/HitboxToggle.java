package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.IServerClient;
import ca.afroman.level.Level;
import ca.afroman.level.LevelObjectType;

public class HitboxToggle implements IEvent, IServerClient
{
	private boolean isServerSide;
	
	private Level level;
	private List<Integer> inTriggers;
	private List<Integer> outTriggers;
	private Hitbox hitbox;
	
	public HitboxToggle(boolean isServerSide, int id, double x, double y, double width, double height, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		this(isServerSide, id, new Hitbox(id, x, y, width, height), inTriggers, outTriggers);
	}
	
	public HitboxToggle(boolean isServerSide, int id, Hitbox box, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		this.isServerSide = isServerSide;
		level = null;
		this.inTriggers = (inTriggers != null ? inTriggers : new ArrayList<Integer>());
		this.outTriggers = (outTriggers != null ? outTriggers : new ArrayList<Integer>());
		hitbox = box;
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
	public double getHeight()
	{
		return hitbox.getHeight();
	}
	
	public Hitbox getHitbox()
	{
		return hitbox;
	}
	
	@Override
	public int getID()
	{
		return hitbox.getID();
	}
	
	@Override
	public List<Integer> getInTriggers()
	{
		return inTriggers;
	}
	
	@Override
	public Level getLevel()
	{
		return hitbox.getLevel();
	}
	
	@Override
	public List<Integer> getOutTriggers()
	{
		return outTriggers;
	}
	
	@Override
	public double getWidth()
	{
		return hitbox.getWidth();
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
	
	public boolean isEnabled()
	{
		return hitbox.getLevel() != null;
	}
	
	@Override
	public boolean isServerSide()
	{
		return isServerSide;
	}
	
	@Override
	public void onTrigger(Entity triggerer)
	{
		setEnabled(!isEnabled());
	}
	
	@Override
	public void removeFromLevel()
	{
		addToLevel(null);
	}
	
	public void setEnabled(boolean isActive)
	{
		if (isActive)
		{
			hitbox.addToLevel(level);
		}
		else
		{
			hitbox.removeFromLevel();
		}
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
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(LevelObjectType.HITBOX_TOGGLE.toString());
		sb.append('(');
		sb.append(isEnabled() ? "true" : "false");
		sb.append(", ");
		sb.append(getX());
		sb.append(", ");
		sb.append(getY());
		sb.append(", ");
		sb.append(getWidth());
		sb.append(", ");
		sb.append(getHeight());
		sb.append(", {");
		
		// Saves in triggers
		for (int k = 0; k < getInTriggers().size(); k++)
		{
			sb.append(getInTriggers().get(k));
			if (k != getInTriggers().size() - 1) sb.append(", ");
		}
		
		sb.append("}, {");
		
		// Saves out triggers
		for (int k = 0; k < getOutTriggers().size(); k++)
		{
			sb.append(getOutTriggers().get(k));
			if (k != getOutTriggers().size() - 1) sb.append(", ");
		}
		
		sb.append("})");
		
		return sb.toString();
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
}
