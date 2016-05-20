package ca.afroman.gfx;

import java.awt.Color;

import ca.afroman.assets.AssetType;
import ca.afroman.entity.api.Entity;
import ca.afroman.level.ClientLevel;
import ca.afroman.level.Level;

public class PointLight extends Entity
{
	protected Color colour;
	
	public PointLight(int id, Level level, double x, double y, double radius)
	{
		this(id, level, x, y, radius, ColourUtil.TRANSPARENT);
	}
	
	public PointLight(int id, Level level, double x, double y, double radius, Color colour)
	{
		super(id, level, AssetType.INVALID, x, y, radius * 2, radius * 2);
		
		this.colour = colour;
	}
	
	@Override
	public void tick()
	{
		
	}
	
	public void renderCentered(LightMap renderTo)
	{
		if (level instanceof ClientLevel)
		{
			ClientLevel cLevel = (ClientLevel) this.level;
			
			renderTo.drawLight(cLevel.worldToScreenX(x) - getRadius(), cLevel.worldToScreenY(y) - getRadius(), getRadius(), colour);
		}
	}
	
	public double getRadius()
	{
		return width / 2;
	}
	
	public Color getColour()
	{
		return colour;
	}
	
	/**
	 * Removes an entity from their current level and puts them in another level.
	 * 
	 * @param level the new level.
	 */
	@Override
	public void addToLevel(Level level)
	{
		if (getLevel() == level) return;
		
		if (getLevel() != null)
		{
			// TODO remove from the previous level
			for (Entity entity : this.level.getLights())
			{
				if (entity == this)
				{
					getLevel().getLights().remove(this);
					this.level = level;
					getLevel().getLights().add(this);
					return;
				}
			}
		}
		else
		{
			this.level = level;
			getLevel().getLights().add(this);
		}
	}
}
