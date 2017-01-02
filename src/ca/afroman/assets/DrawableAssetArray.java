package ca.afroman.assets;

import java.util.Random;

import ca.afroman.resource.Vector2DInt;

public class DrawableAssetArray extends DrawableAsset
{
	/** Holds the assets. */
	private DrawableAsset[] assets;
	
	/**
	 * A wrapper for an array of DrawableAssets.
	 * 
	 * @param type the AssetType that corresponds with this
	 * @param assets the array of DrawableAssets to wrap
	 */
	public DrawableAssetArray(AssetType type, DrawableAsset... assets)
	{
		super(type, assets[0].getWidth(), assets[0].getHeight());
		
		this.assets = assets;
	}
	
	@Override
	public DrawableAssetArray clone()
	{
		return new DrawableAssetArray(getAssetType(), assets);
	}
	
	/**
	 * Clones this AssetArray object, as well as all of the individual assets that it contains.
	 * Just a heads up that this is quite resource intensive if this AssetArray contains a lot of
	 * sub-assets, so there'd better be good reason to use this.
	 * 
	 * @return the cloned AssetArray.
	 */
	public DrawableAssetArray cloneWithAllSubAssets()
	{
		DrawableAsset[] newAssets = new DrawableAsset[assets.length];
		
		for (int i = 0; i < assets.length; i++)
		{
			newAssets[i] = assets[i].clone();
		}
		
		return new DrawableAssetArray(getAssetType(), newAssets);
	}
	
	@Override
	public void dispose()
	{
		for (Asset asset : assets)
		{
			asset.dispose();
		}
	}
	
	/**
	 * Gets the DrawableAsset from this at the provided index.
	 * 
	 * @param index the index
	 * @return the DrawableAsset at the given index.
	 *         <p>
	 *         <code>null</code> if the provided index is out of bounds of the internal array in this.
	 */
	public DrawableAsset getDrawableAsset(int index)
	{
		if (index < 0 || index >= assets.length) return null;
		
		return assets[index];
	}
	
	/**
	 * @return the internal array of DrawableAssets.
	 */
	public DrawableAsset[] getDrawableAssets()
	{
		return assets;
	}
	
	/**
	 * @return a random DrawableAsset from this.
	 */
	public DrawableAsset getRandomDrawableAsset()
	{
		return getDrawableAsset(new Random().nextInt(size()));
	}
	
	/**
	 * Gets a random DrawableAsset from this with a provided seed in coordinate form.
	 * 
	 * @param xSeed the x seed
	 * @param ySeed the y seed
	 * @return a random DrawableAsset from this with a seed.
	 */
	public DrawableAsset getRandomDrawableAsset(int xSeed, int ySeed)
	{
		return getDrawableAsset(new Random(xSeed << 16 + ySeed).nextInt(size()));
	}
	
	/**
	 * @deprecated Cannot draw a DrawableAssetArray.
	 */
	@Override
	@Deprecated
	public void render(Texture renderTo, int x, int y)
	{
		
	}
	
	/**
	 * @deprecated Cannot draw a DrawableAssetArray.
	 */
	@Override
	@Deprecated
	public void render(Texture renderTo, Vector2DInt pos)
	{
		
	}
	
	@Override
	public DrawableAssetArray replaceColour(int from, int to)
	{
		for (DrawableAsset a : assets)
		{
			a.replaceColour(from, to);
		}
		
		return this;
	}
	
	/**
	 * @return the number of DrawableAssets in this.
	 */
	public int size()
	{
		return assets.length;
	}
}
