package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.TriggerType;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.input.InputType;
import ca.afroman.level.Level;
import ca.afroman.log.ALogType;
import ca.afroman.server.ServerGame;

public class HitboxTrigger extends InputType implements IEvent
{
	private static int nextAvailableID = 0;
	
	private List<TriggerType> triggerTypes;
	private List<Integer> inTriggers;
	private List<Integer> outTriggers;
	private Hitbox hitbox;
	
	public HitboxTrigger(int id, double x, double y, double width, double height, List<TriggerType> triggerTypes, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		this(id, new Hitbox(id, x, y, width, height), triggerTypes, inTriggers, outTriggers);
	}
	
	public HitboxTrigger(int id, Hitbox box, List<TriggerType> triggerTypes, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		hitbox = box;
		this.triggerTypes = (triggerTypes != null ? triggerTypes : new ArrayList<TriggerType>());
		this.inTriggers = (inTriggers != null ? inTriggers : new ArrayList<Integer>());
		this.outTriggers = (outTriggers != null ? outTriggers : new ArrayList<Integer>());
	}
	
	public Hitbox getHitbox()
	{
		return hitbox;
	}
	
	/**
	 * Removes this scripted event from its current level and puts it in another level.
	 * 
	 * @param level the new level.
	 */
	public void addToLevel(Level newLevel)
	{
		if (hitbox.getLevel() == newLevel) return;
		
		if (hitbox.getLevel() != null)
		{
			synchronized (hitbox.getLevel().getScriptedEvents())
			{
				hitbox.getLevel().getScriptedEvents().remove(this);
			}
		}
		
		// Sets the new level
		hitbox.setLevel(newLevel);
		
		if (hitbox.getLevel() != null)
		{
			synchronized (hitbox.getLevel().getScriptedEvents())
			{
				hitbox.getLevel().getScriptedEvents().add(this);
			}
		}
	}
	
	/**
	 * @return the next available ID for use. (Ignored previous ID's that are now free for use. TODO?)
	 */
	public static int getNextAvailableID()
	{
		int toReturn = nextAvailableID;
		nextAvailableID++;
		return toReturn;
	}
	
	/**
	 * Resets the nextAvailableID so that it starts counting from 0 again.
	 * <p>
	 * <b>WARNING: </b>only intended for use on server shutdowns.
	 */
	public static void resetNextAvailableID()
	{
		nextAvailableID = 0;
	}
	
	@Override
	public List<Integer> getInTriggers()
	{
		return inTriggers;
	}
	
	@Override
	public List<Integer> getOutTriggers()
	{
		return outTriggers;
	}
	
	public List<TriggerType> getTriggerTypes()
	{
		return triggerTypes;
	}
	
	@Override
	public void tick()
	{
		if (this.triggerTypes.contains(TriggerType.PLAYER_COLLIDE))
		{
			boolean hasPressed = false;
			
			for (Entity player : this.hitbox.getLevel().getPlayers())
			{
				if (player.isColliding(this.getHitbox()))
				{
					this.setPressed(true);
					hasPressed = true;
					break;
				}
			}
			
			if (!hasPressed) this.setPressed(false);
		}
		
		if (this.isPressedFiltered()) ServerGame.instance().logger().log(ALogType.DEBUG, "Triggered: ");
	}
	
	@Override
	public void onTrigger()
	{
		
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
}
