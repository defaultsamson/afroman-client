package ca.afroman.gfx;

import java.awt.Color;

import ca.afroman.assets.AssetType;
import ca.afroman.entity.api.Entity;
import ca.afroman.level.ClientLevel;
import ca.afroman.level.Level;
import ca.afroman.resource.IDCounter;
import ca.afroman.resource.Vector2DDouble;

public class PointLight extends Entity
{
	private static IDCounter idCounter = new IDCounter();
	
	public static IDCounter getIDCounter()
	{
		return idCounter;
	}
	
	protected Color colour;
	
	private double radius;
	
	public PointLight(boolean isServerSide, int id, Vector2DDouble pos, double radius)
	{
		this(isServerSide, id, pos, radius, ColourUtil.TRANSPARENT);
	}
	
	public PointLight(boolean isServerSide, int id, Vector2DDouble pos, double radius, Color colour)
	{
		super(isServerSide, id, AssetType.INVALID, pos);
		
		this.colour = colour;
		this.radius = radius;
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
			level.getLights().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			level.getLights().add(this);
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
	
	@SuppressWarnings("deprecation")
	public void renderCentered(LightMap renderTo)
	{
		if (level instanceof ClientLevel)
		{
			ClientLevel cLevel = (ClientLevel) this.level;
			
			renderTo.drawLight(cLevel.worldToScreen(position).add((int) -getRadius(), (int) -getRadius()), getRadius(), colour);
		}
	}
	
	@Override
	public void tick()
	{
		
	}
}
