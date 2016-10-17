package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.level.LevelObjectType;
import ca.afroman.level.LevelType;
import ca.afroman.util.ParamUtil;

public class TPTriggerWrapper
{
	/**
	 * Takes a saved version of a HitboxToggle's information and parses it.
	 * 
	 * @param input must be formatted as it is saved. e.g. TP_TRIGGER(-1.0, 82.0, 35.0, 10.0, {PLAYER_COLLIDE, PLAYER_UNCOLLIDE}, {}, {22})
	 * @return
	 */
	public static TPTriggerWrapper fromString(String input)
	{
		String[] split = input.split("\\(");
		LevelObjectType objectType = LevelObjectType.valueOf(split[0]);
		
		if (objectType == LevelObjectType.TP_TRIGGER)
		{
			String[] split2 = split[1].split("\\)");
			String rawParameters = split2.length > 0 ? split2[0] : "";
			String[] parameters = ParamUtil.getParameters(rawParameters);
			
			double x = Double.parseDouble(parameters[0]);
			double y = Double.parseDouble(parameters[1]);
			double width = Double.parseDouble(parameters[2]);
			double height = Double.parseDouble(parameters[3]);
			double tpX = Double.parseDouble(parameters[4]);
			double tpY = Double.parseDouble(parameters[5]);
			LevelType toTpTo = LevelType.valueOf(parameters[6]);
			
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
			
			return new TPTriggerWrapper(x, y, width, height, tpX, tpY, toTpTo, inTriggers, outTriggers);
		}
		else
		{
			return null;
		}
	}
	
	private LevelType toTpTo;
	private double x;
	private double y;
	private double tpX;
	private double tpY;
	private double width;
	private double height;
	private List<Integer> inTriggers;
	private List<Integer> outTriggers;
	
	public TPTriggerWrapper(double x, double y, double width, double height, double tpX, double tpY, LevelType toTpTo, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		this.toTpTo = toTpTo;
		this.x = x;
		this.y = y;
		this.tpX = tpX;
		this.tpY = tpY;
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
	
	public double getTPX()
	{
		return tpX;
	}
	
	public double getTPY()
	{
		return tpY;
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
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(LevelObjectType.TP_TRIGGER);
		sb.append('(');
		sb.append(x);
		sb.append(", ");
		sb.append(y);
		sb.append(", ");
		sb.append(width);
		sb.append(", ");
		sb.append(height);
		sb.append(", ");
		sb.append(tpX);
		sb.append(", ");
		sb.append(tpY);
		sb.append(", ");
		sb.append(toTpTo);
		sb.append(", {");
		
		// Saves in triggers
		for (int k = 0; k < inTriggers.size(); k++)
		{
			sb.append(inTriggers.get(k));
			if (k != inTriggers.size() - 1) sb.append(", ");
		}
		
		sb.append("}, {");
		
		// Saves out triggers
		for (int k = 0; k < outTriggers.size(); k++)
		{
			sb.append(outTriggers.get(k));
			if (k != outTriggers.size() - 1) sb.append(", ");
		}
		
		sb.append("})");
		
		return sb.toString();
	}
	
	public LevelType toTpTo()
	{
		return toTpTo;
	}
	
	public TPTrigger toTPTrigger(boolean isServerSide, int id)
	{
		TPTrigger t = new TPTrigger(isServerSide, id, x, y, width, height, inTriggers, outTriggers);
		t.setLevelToTPTo(toTpTo);
		t.setLocationToTPTo(tpX, tpY);
		return t;
	}
}
