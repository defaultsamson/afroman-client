package ca.afroman.assets;

public abstract class Asset implements Cloneable
{
	// This simply acts as a class to relate all the assets together for the Assets class
	
	private AssetType type;
	
	/**
	 * An object with a corresponding AssetType.
	 * 
	 * @param type the AssetType that corresponds with this
	 */
	public Asset(AssetType type)
	{
		this.type = type;
	}
	
	@Override
	public abstract Asset clone();
	
	/**
	 * Disposes of any resources loaded by this Asset, rendering it unusable.
	 */
	public abstract void dispose();
	
	public AssetType getAssetType()
	{
		return type;
	}
}
