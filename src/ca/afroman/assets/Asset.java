package ca.afroman.assets;

public abstract class Asset
{
	// This simply acts as a class to relate all the assets together for the Assets class
	
	private AssetType type;
	
	public Asset(AssetType type)
	{
		this.type = type;
	}
	
	@Override
	public abstract Asset clone();
	
	public abstract void dispose();
	
	public AssetType getAssetType()
	{
		return type;
	}
}
