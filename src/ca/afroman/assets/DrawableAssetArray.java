package ca.afroman.assets;

import java.util.Random;

public class DrawableAssetArray extends DrawableAsset
{
	/** Holds the assets. */
	private DrawableAsset[] assets;
	
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
	
	public Asset getAsset(int index)
	{
		return assets[index];
	}
	
	public Asset[] getAssets()
	{
		return assets;
	}
	
	public Asset getRandomAsset()
	{
		return getAsset(new Random().nextInt(length()));
	}
	
	public Asset getRandomAsset(int xSeed, int ySeed)
	{
		return getAsset(new Random(xSeed << 16 + ySeed).nextInt(length()));
	}
	
	public int length()
	{
		return assets.length;
	}
}
