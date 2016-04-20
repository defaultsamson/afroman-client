package ca.pixel.game.gfx;

import java.awt.Color;

public class PointLight
{
	protected int x;
	protected int y;
	protected int radius;
	protected float intensity;
	protected Color colour;
	
	public PointLight(int x, int y, int radius)
	{
		this(x, y, radius, 1.0F);
	}
	
	public PointLight(int x, int y, int radius, float intensity)
	{
		this(x, y, radius, intensity, ColourUtil.TRANSPARENT);
	}
	
	public PointLight(int x, int y, int radius, float intensity, Color colour)
	{
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.intensity = intensity;
		this.colour = colour;
	}
	
	public void tick()
	{
		
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
	
	public float getIntensity()
	{
		return intensity;
	}
	
	public Color getColour()
	{
		return colour;
	}
}
