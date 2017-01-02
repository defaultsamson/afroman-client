package ca.afroman.assets;

import ca.afroman.resource.Vector2DInt;

public abstract class DrawableAsset extends Asset
{
	// This simply acts as a class to relate all the assets together for the Assets class
	
	protected int width;
	protected int height;
	
	/**
	 * An Asset that can be rendered using the <code>DrawableAsset.render()</code> method.
	 * 
	 * @param type the AssetType that corresponds with this
	 * @param width the drawable width of this
	 * @param height the drawable height of this
	 */
	public DrawableAsset(AssetType type, int width, int height)
	{
		super(type);
		
		this.width = width;
		this.height = height;
	}
	
	@Override
	public abstract DrawableAsset clone();
	
	/**
	 * @return the drawable height of this.
	 */
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * @return the drawable width of this.
	 */
	public int getWidth()
	{
		return width;
	}
	
	public abstract void render(Texture renderTo, int x, int y);
	
	/**
	 * Renders this to a given Texture object at a provided position.
	 * 
	 * @param renderTo the object to draw this to
	 * @param pos the position to draw this at
	 */
	public void render(Texture renderTo, Vector2DInt pos)
	{
		render(renderTo, pos.getX(), pos.getY());
	}
	
	/**
	 * Replaces all of a given colour in this to a provided destination colour.
	 * <p>
	 * ColourUtil can be used to convert a colour from a hex String to an integer.
	 * 
	 * @param from the colour to replace
	 * @param to the colour to replace <b>from</b> with
	 * @return this.
	 */
	public abstract DrawableAsset replaceColour(int from, int to);
}
