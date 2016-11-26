package ca.afroman.entity.api;

import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.Texture;
import ca.afroman.interfaces.IRenderable;
import ca.afroman.interfaces.ITickable;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.resource.Vector2DInt;

public class DrawableEntity extends Entity implements IRenderable
{
	protected boolean cameraFollow;
	protected DrawableAsset asset;
	
	public DrawableEntity(boolean isServerSide, int id, DrawableAsset asset, Vector2DDouble pos, Hitbox... hitboxes)
	{
		super(isServerSide, id, pos, hitboxes);
		
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
			if (asset instanceof IRenderable)
			{
				asset.render(renderTo, getLevel().worldToScreen(position));
			}
		}
	}
	
	/**
	 * @deprecated Use the other render method to properly render this in the world.
	 */
	@Override
	@Deprecated
	public void render(Texture renderTo, Vector2DInt pos)
	{
		asset.render(renderTo, pos);
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
