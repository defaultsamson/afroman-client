package ca.afroman.entity.api;

import ca.afroman.assets.Asset;
import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.interfaces.IRenderable;
import ca.afroman.interfaces.ITickable;
import ca.afroman.level.ClientLevel;

public class ClientAssetEntity extends ClientEntity implements IRenderable
{
	protected Asset asset;
	
	public ClientAssetEntity(int id, ClientLevel level, AssetType assetType, double x, double y, double width, double height, Hitbox... hitboxes)
	{
		this(id, level, (Assets.getAsset(assetType) != null ? Assets.getAsset(assetType).clone() : null), x, y, width, height, hitboxes);
	}
	
	public ClientAssetEntity(int id, ClientLevel level, Asset asset, double x, double y, double width, double height, Hitbox... hitboxes)
	{
		super(id, level, (asset != null ? asset.getAssetType() : AssetType.INVALID), x, y, width, height, hitboxes);
		
		this.asset = asset;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (asset instanceof ITickable) ((ITickable) asset).tick();
	}
	
	public void render(Texture renderTo)
	{
		if (asset != null && getLevel() != null)
		{
			if (asset instanceof IRenderable) ((IRenderable) asset).render(renderTo, getLevel().worldToScreenX(x), getLevel().worldToScreenY(y));
		}
	}
	
	/**
	 * @deprecated Use the other render method to properly render this in the world.
	 */
	@Override
	@Deprecated
	public void render(Texture renderTo, int x, int y)
	{
		if (asset instanceof IRenderable) ((IRenderable) asset).render(renderTo, x, y);
	}
	
	public Asset getAsset()
	{
		return asset;
	}
}
