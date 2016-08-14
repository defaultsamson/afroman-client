package ca.afroman.assets;

import ca.afroman.interfaces.IRenderable;

public abstract class DrawableAsset extends Asset implements IRenderable
{
	// This simply acts as a class to relate all the assets together for the Assets class
	
	private int width;
	private int height;
	
	public DrawableAsset(AssetType type, int width, int height)
	{
		super(type);
		
		this.width = width;
		this.height = height;
	}
	
	public double getHeight()
	{
		return height;
	}
	
	public double getWidth()
	{
		return width;
	}
}
