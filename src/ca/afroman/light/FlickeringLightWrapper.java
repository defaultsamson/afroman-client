package ca.afroman.light;

import ca.afroman.level.LevelObjectType;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ParamUtil;

public class FlickeringLightWrapper
{
	/**
	 * Takes a saved version of a FlickeringLight's information and parses it.
	 * 
	 * @param input must be formatted as it is saved. e.g. FLICKERING_LIGHT(x, y, radius, radius2, ticksPerFrame)
	 * @return
	 */
	public static FlickeringLightWrapper fromString(String input)
	{
		String[] split = input.split("\\(");
		LevelObjectType objectType = LevelObjectType.valueOf(split[0]);
		
		if (objectType == LevelObjectType.FLICKERING_LIGHT)
		{
			String[] split2 = split[1].split("\\)");
			String rawParameters = split2.length > 0 ? split2[0] : "";
			String[] parameters = ParamUtil.getParameters(rawParameters);
			
			double x = Double.parseDouble(parameters[0]);
			double y = Double.parseDouble(parameters[1]);
			double radius1 = Double.parseDouble(parameters[2]);
			double radius2 = Double.parseDouble(parameters[3]);
			int tpf = Integer.parseInt(parameters[4]);
			
			return new FlickeringLightWrapper(x, y, radius1, radius2, tpf);
		}
		else
		{
			return null;
		}
	}
	
	private double x;
	private double y;
	private double radius1;
	private double radius2;
	private int ticksPerFrame;
	
	public FlickeringLightWrapper(double x, double y, double radius1, double radius2, int ticksPerFrame)
	{
		this.x = x;
		this.y = y;
		this.radius1 = radius1;
		this.radius2 = radius2;
		this.ticksPerFrame = ticksPerFrame;
	}
	
	public double getRadius1()
	{
		return radius1;
	}
	
	public double getRadius2()
	{
		return radius2;
	}
	
	public int getTicksPerFrame()
	{
		return ticksPerFrame;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public FlickeringLight toFlickeringLight(boolean isServerSide, int id)
	{
		return new FlickeringLight(isServerSide, id, new Vector2DDouble(x, y), radius1, radius2, ticksPerFrame);
	}
}
