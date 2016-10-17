package ca.afroman.events;

import java.util.List;

import ca.afroman.entity.api.Entity;
import ca.afroman.level.Level;

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
		
		boolean wasEnabled = enabled;
		
		if (level != null)
		{
			setEnabled(false);
			level.getScriptedEvents().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			setEnabled(wasEnabled);
			level.getScriptedEvents().add(this);
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
	
	@Override
	public String toString()
	{
		return toWrapper().toString();
	}
	
	public HitboxToggleWrapper toWrapper()
	{
		return new HitboxToggleWrapper(isEnabled(), getHitbox().getX(), getHitbox().getY(), getHitbox().getWidth(), getHitbox().getHeight(), getInTriggers(), getOutTriggers());
	}
}
