package ca.afroman.events;

import java.util.List;

import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.game.Game;
import ca.afroman.level.api.Level;
import ca.afroman.log.ALogType;
import ca.afroman.resource.Vector2DDouble;

public class HitboxToggle extends Event
{
	protected boolean enabled;
	
	public HitboxToggle(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position, List<Integer> inTriggers, List<Integer> outTriggers, Hitbox... hitboxes)
	{
		super(isServerSide, isMicromanaged, position, inTriggers, outTriggers, hitboxes);
		
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
	
	public void setEnabled(boolean isActive)
	{
		// If it's already in the desired state
		if (isActive == enabled)
		{
			return;
		}
		
		if (isActive)
		{
			if (hasHitbox())
			{
				for (Hitbox box : getHitbox())
				{
					box.addToLevel(level);
				}
			}
			else
			{
				Game.instance(isServerSide()).logger().log(ALogType.WARNING, "HitboxToggle(id=" + getID() + ") has no hitboxes.");
			}
		}
		else
		{
			if (hasHitbox())
			{
				for (Hitbox box : getHitbox())
				{
					box.removeFromLevel();
				}
			}
			else
			{
				Game.instance(isServerSide()).logger().log(ALogType.WARNING, "HitboxToggle(id=" + getID() + ") has no hitboxes.");
			}
		}
		
		enabled = isActive;
	}
	
	@Override
	public void trigger(Entity triggerer)
	{
		setEnabled(!enabled);
		
		super.trigger(triggerer);
	}
}
