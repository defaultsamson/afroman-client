package ca.afroman.entity.api;

import ca.afroman.assets.Asset;
import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.interfaces.IRenderable;
import ca.afroman.interfaces.ITickable;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.resource.Vector2DInt;

public class ClientAssetEntity extends ClientEntity implements IRenderable
{
	protected Asset asset;
	
	public ClientAssetEntity(int id, AssetType assetType, Vector2DDouble pos, Hitbox... hitboxes)
	{
		this(id, (Assets.getAsset(assetType) != null ? Assets.getAsset(assetType).clone() : null), pos, hitboxes);
	}
	
	public ClientAssetEntity(int id, Asset asset, Vector2DDouble pos, Hitbox... hitboxes)
	{
		super(id, (asset != null ? asset.getAssetType() : AssetType.INVALID), pos, hitboxes);
		
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
			if (asset instanceof IRenderable) ((IRenderable) asset).render(renderTo, getLevel().worldToScreen(position));
		}
	}
	
	/**
	 * @deprecated Use the other render method to properly render this in the world.
	 */
	@Override
	@Deprecated
	public void render(Texture renderTo, Vector2DInt pos)
	{
		if (asset instanceof IRenderable) ((IRenderable) asset).render(renderTo, pos);
	}
	
	public Asset getAsset()
	{
		return asset;
	}
}
