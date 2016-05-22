package ca.afroman.assets;

import ca.afroman.interfaces.IRenderable;

public abstract class Asset implements IRenderable
{
	// This simply acts as a class to relate all the assets together for the Assets class
	
	private int width;
	private int height;
	private AssetType type;
	
	public Asset(AssetType type, int width, int height)
	{
		this.width = width;
		this.height = height;
		this.type = type;
	}
	
	@Override
	public abstract Asset clone();
	
	public AssetType getAssetType()
	{
		return type;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
}
