package ca.afroman.entity.api;

import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.Texture;
import ca.afroman.interfaces.ITickable;
import ca.afroman.resource.Vector2DDouble;

public class DrawableEntity extends Entity
{
	protected boolean cameraFollow;
	protected DrawableAsset asset;
	
	public DrawableEntity(boolean isServerSide, boolean isMicromanaged, int id, DrawableAsset asset, Vector2DDouble pos, Hitbox... hitboxes)
	{
		super(isServerSide, isMicromanaged, id, pos, hitboxes);
		
		this.asset = asset;
		cameraFollow = false;
	}
	
	public DrawableAsset getAsset()
	{
		return asset;
	}
	
	public void render(Texture renderTo)
	{
		if (asset != null && getLevel() != null)
		{
			asset.render(renderTo, getLevel().worldToScreen(position));
		}
	}
	
	/**
	 * Makes the level camera follow this Entity or not.
	 * 
	 * @param follow whether or not to follow
	 */
	public void setCameraToFollow(boolean follow)
	{
		cameraFollow = follow;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (!isServerSide())
		{
			if (asset instanceof ITickable) ((ITickable) asset).tick();
			
			if (cameraFollow)
			{
				getLevel().setCameraCenterInWorld(new Vector2DDouble(position.getX() + (16 / 2), position.getY() + (16 / 2)));
			}
		}
	}
}
