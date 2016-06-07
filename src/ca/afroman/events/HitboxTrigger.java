package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.TriggerType;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.input.InputType;
import ca.afroman.level.Level;
import ca.afroman.log.ALogType;
import ca.afroman.server.ServerGame;

public class HitboxTrigger extends InputType implements IEvent
{
	private static int nextAvailableID = 0;
	
	private List<TriggerType> triggerTypes;
	private List<Integer> triggers;
	private List<Integer> outChainedTriggers;
	private Hitbox hitbox;
	
	public HitboxTrigger(int id, double x, double y, double width, double height, List<TriggerType> triggerTypes, List<Integer> inChainedTriggers, List<Integer> outChainedTriggers)
	{
		hitbox = new Hitbox(id, x, y, width, height);
		this.triggerTypes = (triggerTypes != null ? triggerTypes : new ArrayList<TriggerType>());
		this.triggers = (triggers != null ? triggers : new ArrayList<Integer>());
		this.outChainedTriggers = (outChainedTriggers != null ? outChainedTriggers : new ArrayList<Integer>());
	}
	
	/**
	 * Used for packet sending only. (PacketAddLevelHitboxTrigger)
	 * 
	 * @return
	 */
	public String triggerTypesAsSendable()
	{
		String toReturn = "a";
		
		for (TriggerType type : triggerTypes)
		{
			toReturn += "," + type.ordinal();
		}
		
		return toReturn;
	}
	
	/**
	 * Used for packet sending only. (PacketAddLevelHitboxTrigger)
	 * 
	 * @return
	 */
	public String triggersAsSendable()
	{
		String toReturn = "b";
		
		for (int trig : triggers)
		{
			toReturn += "," + trig;
		}
		
		return toReturn;
	}
	
	/**
	 * Used for packet sending only. (PacketAddLevelHitboxTrigger)
	 * 
	 * @return
	 */
	public String chainTriggersAsSendable()
	{
		String toReturn = "c";
		
		for (int trig : outChainedTriggers)
		{
			toReturn += "," + trig;
		}
		
		return toReturn;
	}
	
	/**
	 * Trigger this ScriptedEvent using a trigger type. It this scripted
	 * event is set to be able to be triggered by the TriggerType <b>type</b>
	 * then it will invoke this event's onTriggered() method.
	 * 
	 * @param type the TriggerType
	 */
	public void attemptTrigger(TriggerType type)
	{
		for (TriggerType trigger : triggerTypes)
		{
			if (trigger == type)
			{
				onTrigger();
				return;
			}
		}
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
	public List<Integer> getTriggers()
	{
		return triggers;
	}
	
	@Override
	public List<Integer> getChainTriggers()
	{
		return outChainedTriggers;
	}

	@Override
	public void onTrigger()
	{
		this.setPressed(true);
		
		if (this.isPressedFiltered()) ServerGame.instance().logger().log(ALogType.DEBUG, "#Triggered1");
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
