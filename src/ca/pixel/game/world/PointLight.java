package ca.pixel.game.world;

import ca.pixel.game.gfx.LightMap;

public class PointLight
{
	private int x;
	private int y;
	private int radius;
	private float intensity;
	private int colour;
	
	public PointLight(int x, int y, int radius)
	{
		this(x, y, radius, 1.0F, 0xFFFFFF);
	}
	
	public PointLight(int x, int y, int radius, float intensity)
	{
		this(x, y, radius, intensity, 0xFFFFFF);
	}
	
	public PointLight(int x, int y, int radius, float intensity, int colour)
	{
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.intensity = intensity;
		this.colour = colour;
	}
	
	public void render(LightMap renderTo)
	{
		renderTo.drawLight(x, y, radius, intensity, colour);
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getRadius()
	{
		return radius;
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
