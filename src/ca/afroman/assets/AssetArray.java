package ca.afroman.assets;

import java.util.Random;

public class AssetArray extends Asset
{
	/** Holds the assets. */
	private Asset[] assets;
	
	public AssetArray(Asset... assets)
	{
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
		Asset[] newAssets = new Asset[assets.length];
		
		for (int i = 0; i < assets.length; i++)
		{
			newAssets[i] = assets[i].clone();
		}
		
		return new AssetArray(newAssets);
	}
}
