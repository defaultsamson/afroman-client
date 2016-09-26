package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.level.LevelObjectType;
import ca.afroman.util.ParamUtil;

public class HitboxTriggerWrapper
{
	/**
	 * Takes a saved version of a HitboxToggle's information and parses it.
	 * 
	 * @param input must be formatted as it is saved. e.g. HITBOX_TRIGGER(-1.0, 82.0, 35.0, 10.0, {PLAYER_COLLIDE, PLAYER_UNCOLLIDE}, {}, {22})
	 * @return
	 */
	public static HitboxTriggerWrapper fromString(String input)
	{
		String[] split = input.split("\\(");
		LevelObjectType objectType = LevelObjectType.valueOf(split[0]);
		
		if (objectType == LevelObjectType.HITBOX_TRIGGER)
		{
			String[] split2 = split[1].split("\\)");
			String rawParameters = split2.length > 0 ? split2[0] : "";
			String[] parameters = ParamUtil.getParameters(rawParameters);
			
			double x = Double.parseDouble(parameters[0]);
			double y = Double.parseDouble(parameters[1]);
			double width = Double.parseDouble(parameters[2]);
			double height = Double.parseDouble(parameters[3]);
			
			String[] rSubParameters = ParamUtil.getRawSubParameters(rawParameters);
			
			List<TriggerType> triggerTypes = new ArrayList<TriggerType>();
			String[] triggerParameters = ParamUtil.getParameters(rSubParameters[0]);
			if (triggerParameters != null)
			{
				for (String e : triggerParameters)
				{
					triggerTypes.add(TriggerType.valueOf(e));
				}
			}
			
			List<Integer> inTriggers = new ArrayList<Integer>();
			String[] inTriggerP = ParamUtil.getParameters(rSubParameters[1]);
			if (inTriggerP != null)
			{
				for (String e : inTriggerP)
				{
					inTriggers.add(Integer.parseInt(e));
				}
			}
			
			List<Integer> outTriggers = new ArrayList<Integer>();
			String[] outTriggerP = ParamUtil.getParameters(rSubParameters[2]);
			if (outTriggerP != null)
			{
				for (String e : outTriggerP)
				{
					outTriggers.add(Integer.parseInt(e));
				}
			}
			
			return new HitboxTriggerWrapper(x, y, width, height, triggerTypes, inTriggers, outTriggers);
		}
		else
		{
			return null;
		}
	}
	
	private double x;
	private double y;
	private double width;
	private double height;
	private List<TriggerType> triggers;
	private List<Integer> inTriggers;
	
	private List<Integer> outTriggers;
	
	public HitboxTriggerWrapper(double x, double y, double width, double height, List<TriggerType> triggers, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		this.triggers = triggers;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.inTriggers = inTriggers;
		this.outTriggers = outTriggers;
	}
	
	public double getHeight()
	{
		return height;
	}
	
	public List<Integer> getInTriggers()
	{
		return inTriggers;
	}
	
	public List<Integer> getOutTriggers()
	{
		return outTriggers;
	}
	
	public List<TriggerType> getTriggers()
	{
		return triggers;
	}
	
	public double getWidth()
	{
		return width;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
}
