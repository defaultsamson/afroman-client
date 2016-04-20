package ca.pixel.game.world;

import java.awt.Color;

import ca.pixel.game.gfx.ColourUtil;
import ca.pixel.game.gfx.LightMap;

public class PointLight
{
	private int x;
	private int y;
	private int radius;
	private int resolution;
	private float intensity;
	private Color colour;
	
	public PointLight(int x, int y, int radius, int resolution)
	{
		this(x, y, radius, resolution, 1.0F);
	}
	
	public PointLight(int x, int y, int radius, int resolution, float intensity)
	{
		this(x, y, radius, resolution, intensity, ColourUtil.TRANSPARENT);
	}
	
	public PointLight(int x, int y, int radius, int resolution, float intensity, Color colour)
	{
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.intensity = intensity;
		this.resolution = resolution;
		this.colour = colour;
	}
	
	public void renderCentered(LightMap renderTo, int xOffset, int yOffset)
	{
		renderTo.drawLight(x - xOffset - radius, y - yOffset - radius, radius, colour);
	}
	
	public void setX(int newX)
	{
		x = newX;
	}
	
	public void setY(int newY)
	{
		y = newY;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getWidth()
	{
		return radius * 2;
	}
	
	public int getHeight()
	{
		return radius * 2;
	}
	
	public int getRadius()
	{
		return radius;
	}
	
	public int getResolution()
	{
		return resolution;
	}
	
	public float getIntensity()
	{
		return intensity;
	}
	
	public Color getColour()
	{
		return colour;
	}
}
