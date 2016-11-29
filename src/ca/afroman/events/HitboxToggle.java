package ca.afroman.events;

import java.util.List;

import ca.afroman.entity.api.Entity;
import ca.afroman.level.api.Level;

public class HitboxToggle extends Event
{
	protected boolean enabled;
	
	public HitboxToggle(boolean isServerSide, double x, double y, double width, double height, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		super(isServerSide, x, y, width, height, inTriggers, outTriggers);
		
		enabled = false;
	}
	
	@Override
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		boolean wasEnabled = enabled;
		
		if (level != null)
		{
			setEnabled(false);
			level.getEvents().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			setEnabled(wasEnabled);
			level.getEvents().add(this);
		}
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
}
