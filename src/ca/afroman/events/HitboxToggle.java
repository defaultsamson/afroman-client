package ca.afroman.events;

import java.util.List;

import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.level.Level;
import ca.afroman.level.LevelObjectType;

public class HitboxToggle extends Event
{
	private boolean enabled;
	
	public HitboxToggle(boolean isServerSide, int id, double x, double y, double width, double height, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		super(isServerSide, id, x, y, width, height, inTriggers, outTriggers);
		
		enabled = false;
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
			if (enabled)
			{
				// hitbox.addToLevel(level);
			}
			level.getScriptedEvents().add(this);
		}
	}
	
	@Override
	public Hitbox getHitbox()
	{
		return hitbox;
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	@Override
	public void onTrigger(Entity triggerer)
	{
		setEnabled(!enabled);
	}
	
	@Override
	public void removeFromLevel()
	{
		addToLevel(null);
	}
	
	public void setEnabled(boolean isActive)
	{
		// If it's already in the desired state
		if (isActive == enabled)
		{
			return;
		}
		
		if (isActive)
		{
			hitbox.addToLevel(level);
		}
		else
		{
			hitbox.removeFromLevel();
		}
		
		enabled = isActive;
	}
	
	@Override
	public void setInTriggers(List<Integer> trigs)
	{
		inTriggers = trigs;
	}
	
	@Override
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
		sb.append(getHitbox().getX());
		sb.append(", ");
		sb.append(getHitbox().getY());
		sb.append(", ");
		sb.append(getHitbox().getWidth());
		sb.append(", ");
		sb.append(getHitbox().getHeight());
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
}
