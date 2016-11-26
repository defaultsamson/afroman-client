package ca.afroman.assets;

import ca.afroman.resource.Vector2DInt;

public abstract class DrawableAsset extends Asset
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
	
	@Override
	public abstract DrawableAsset clone();
	
	public int getHeight()
	{
		return height;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public abstract void render(Texture renderTo, Vector2DInt pos);
}
