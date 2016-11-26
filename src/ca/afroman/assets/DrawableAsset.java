package ca.afroman.assets;

import ca.afroman.interfaces.IRenderable;
import ca.afroman.resource.Vector2DInt;

public class DrawableAsset extends Asset implements IRenderable
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
	public DrawableAsset clone()
	{
		return new DrawableAsset(getAssetType(), width, height);
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	@Override
	public void render(Texture renderTo, Vector2DInt pos)
	{
		// Override this in all children
	}
}
