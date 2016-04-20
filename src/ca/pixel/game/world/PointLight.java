package ca.pixel.game.world;

import ca.pixel.game.gfx.LightMap;

public class PointLight
{
	private int x;
	private int y;
	private int radius;
	private int resolution;
	private float intensity;
	private int colour;
	
	public PointLight(int x, int y, int radius, int resolution)
	{
		this(x, y, radius, resolution, 1.0F);
	}
	
	public PointLight(int x, int y, int radius, int resolution, float intensity)
	{
		this(x, y, radius, resolution, intensity, 0xFFFFFF);
	}
	
	public PointLight(int x, int y, int radius, int resolution, float intensity, int colour)
	{
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.intensity = intensity;
		this.resolution = resolution;
		this.colour = colour;
	}
	
	public void render(LightMap renderTo)
	{
		renderTo.drawLight(x, y, radius);
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
	
	public int getColour()
	{
		return colour;
	}
}
