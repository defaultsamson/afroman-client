package ca.afroman.light;

import java.awt.Color;

import ca.afroman.entity.api.Entity;
import ca.afroman.level.api.Level;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ColourUtil;

public class PointLight extends Entity
{
	protected Color colour;
	protected double radius;
	private boolean isMicromanaged;
	
	private PointLight(boolean isServerSide, boolean isMicromanaged, Vector2DDouble pos, double radius, Color colour)
	{
		super(isServerSide, -1, pos);
		
		this.colour = colour;
		this.radius = radius;
		this.isMicromanaged = isMicromanaged;
	}
	
	public PointLight(boolean isMicromanaged, Vector2DDouble pos, double radius)
	{
		this(false, isMicromanaged, pos, radius, ColourUtil.TRANSPARENT);
	}
	
	/**
	 * Removes an entity from their current level and puts them in another level.
	 * 
	 * @param level the new level.
	 */
	@Override
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		if (level != null)
		{
			level.getPointLights().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			level.getPointLights().add(this);
		}
	}
	
	public Color getColour()
	{
		return colour;
	}
	
	public double getRadius()
	{
		return radius;
	}
	
	/**
	 * Tells if this light is managed by a manager such as aa Entity object.
	 * 
	 * @return if the id of this light is -1
	 */
	public boolean isMicroManaged()
	{
		return isMicromanaged;
	}
	
	@SuppressWarnings("deprecation")
	public void renderCentered(LightMap renderTo)
	{
		if (level != null)
		{
			renderTo.drawLight(level.worldToScreen(position).add((int) -getRadius(), (int) -getRadius()), getRadius(), colour);
		}
	}
	
	public void setRadius(double radius)
	{
		this.radius = radius;
	}
	
	@Override
	public void tick()
	{
		
	}
}
