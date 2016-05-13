package ca.afroman.assets;

import java.util.Random;

public class AssetArray
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
	
	public Asset[] getAsset()
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
}
