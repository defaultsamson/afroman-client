package ca.afroman.gfx;

import java.awt.Color;

import ca.afroman.assets.AssetType;
import ca.afroman.entity.Entity;
import ca.afroman.level.ClientLevel;
import ca.afroman.level.Level;

public class PointLight extends Entity
{
	protected ClientLevel level;
	protected Color colour;
	
	public PointLight(ClientLevel level, double x, double y, double radius)
	{
		this(level, x, y, radius, ColourUtil.TRANSPARENT);
	}
	
	public PointLight(ClientLevel level, double x, double y, double radius, Color colour)
	{
		super(level, AssetType.INVALID, x, y, radius * 2, radius * 2);
		
		this.colour = colour;
	}
	
	@Override
	public void tick()
	{
		
	}
	
	public void renderCentered(LightMap renderTo)
	{
		renderTo.drawLight(level.worldToScreenX(x) - getRadius(), level.worldToScreenY(y) - getRadius(), getRadius(), colour);
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
		if (this.level == level) return;
		
		if (this.level != null)
		{
			// TODO remove from the previous level
			for (Entity entity : this.level.getLights())
			{
				if (entity == this)
				{
					this.level.removeLight(this);
					this.level = (ClientLevel) level;
					this.level.addLight(this);
					return;
				}
			}
		}
		else
		{
			this.level = (ClientLevel) level;
			this.level.addLight(this);
		}
	}
}
