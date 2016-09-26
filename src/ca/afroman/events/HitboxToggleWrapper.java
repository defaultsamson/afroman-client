package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.level.LevelObjectType;
import ca.afroman.util.ParamUtil;

public class HitboxToggleWrapper
{
	/**
	 * Takes a saved version of a HitboxToggle's information and parses it.
	 * 
	 * @param input must be formatted as it is saved. e.g. HITBOX_TOGGLE(false, 0.0, 85.0, 33.0, 4.0, {22}, {})
	 * @return
	 */
	public static HitboxToggleWrapper fromString(String input)
	{
		String[] split = input.split("\\(");
		LevelObjectType objectType = LevelObjectType.valueOf(split[0]);
		
		if (objectType == LevelObjectType.HITBOX_TOGGLE)
		{
			String[] split2 = split[1].split("\\)");
			String rawParameters = split2.length > 0 ? split2[0] : "";
			String[] parameters = ParamUtil.getParameters(rawParameters);
			
			boolean isEnabled = Boolean.parseBoolean(parameters[0]);
			double x = Double.parseDouble(parameters[1]);
			double y = Double.parseDouble(parameters[2]);
			double width = Double.parseDouble(parameters[3]);
			double height = Double.parseDouble(parameters[4]);
			
			String[] rSubParameters = ParamUtil.getRawSubParameters(rawParameters);
			
			List<Integer> inTriggers = new ArrayList<Integer>();
			String[] inTriggerP = ParamUtil.getParameters(rSubParameters[0]);
			if (inTriggerP != null)
			{
				for (String e : inTriggerP)
				{
					inTriggers.add(Integer.parseInt(e));
				}
			}
			
			List<Integer> outTriggers = new ArrayList<Integer>();
			String[] outTriggerP = ParamUtil.getParameters(rSubParameters[1]);
			if (outTriggerP != null)
			{
				for (String e : outTriggerP)
				{
					outTriggers.add(Integer.parseInt(e));
				}
			}
			
			return new HitboxToggleWrapper(isEnabled, x, y, width, height, inTriggers, outTriggers);
		}
		else
		{
			return null;
		}
	}
	
	private boolean isEnabled;
	private double x;
	private double y;
	private double width;
	private double height;
	private List<Integer> inTriggers;
	
	private List<Integer> outTriggers;
	
	public HitboxToggleWrapper(boolean isEnabled, double x, double y, double width, double height, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		this.isEnabled = isEnabled;
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
	
	public boolean isEnabled()
	{
		return isEnabled;
	}
}
