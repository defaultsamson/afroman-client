package ca.afroman.assets;

public class Asset implements Cloneable
{
	// This simply acts as a class to relate all the assets together for the Assets class
	
	private AssetType type;
	
	public Asset(AssetType type)
	{
		this.type = type;
	}
	
	@Override
	public Asset clone()
	{
		return new Asset(type);
	}
	
	public void dispose()
	{
		
	}
	
	public AssetType getAssetType()
	{
		return type;
	}
}
