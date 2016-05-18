package ca.afroman.assets;

import java.util.Random;

public class AssetArray extends Asset
{
	/** Holds the assets. */
	private Asset[] assets;
	
	public AssetArray(AssetType type, Asset... assets)
	{
		super(type, assets[0].getWidth(), assets[0].getHeight());
		
		this.assets = assets;
	}
	
	public Asset getAsset(int index)
	{
		return assets[index];
	}
	
	public Asset[] getAssets()
	{
		return assets;
	}
	
	public int length()
	{
		return assets.length;
	}
	
	public Asset getRandomAsset()
	{
		return getAsset(new Random().nextInt(length()));
	}
	
	public Asset getRandomAsset(int xSeed, int ySeed)
	{
		return getAsset(new Random(xSeed << 16 + ySeed).nextInt(length()));
	}
	
	@Override
	@Deprecated
	/**
	 * <b>WARNING: </b>do not use this, as this is really an array of assets, and cannot be drawn.
	 */
	public void render(Texture renderTo, int x, int y)
	{
		getRandomAsset().render(renderTo, x, y);
	}
	
	@Override
	public Asset clone()
	{
		return new AssetArray(type, assets);
	}
	
	/**
	 * Clones this AssetArray object, as well as all of the individual assets that it contains.
	 * Just a heads up that this is quite resource intensive if this AssetArray contains a lot of
	 * sub-assets, so there'd better be good reason to use this.
	 * 
	 * @return the cloned AssetArray.
	 */
	public AssetArray cloneWithAllSubAssets()
	{
		Asset[] newAssets = new Asset[assets.length];
		
		for (int i = 0; i < assets.length; i++)
		{
			newAssets[i] = assets[i].clone();
		}
		
		return new AssetArray(type, newAssets);
	}
}
