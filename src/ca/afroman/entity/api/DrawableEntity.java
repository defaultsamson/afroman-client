package ca.afroman.entity.api;

import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.Texture;
import ca.afroman.interfaces.ITickable;
import ca.afroman.resource.Vector2DDouble;

public abstract class DrawableEntity extends Entity
{
	protected DrawableAsset asset;
	
	/**
	 * Creates a new Entity with a hitbox.
	 * 
	 * @param isServerSide whether this is on the server instance or not
	 * @param isMicromanaged whether this is managed by an external manager (such as an Event), as opposed to directly being managed by the level
	 * @param position the position
	 * @param asset the DrawableAsset to render this as
	 * @param hitboxes the hitboxes, only relative to this, <i>not</i> the world
	 */
	public DrawableEntity(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position, DrawableAsset asset, Hitbox... hitboxes)
	{
		super(isServerSide, isMicromanaged, position, hitboxes);
		
		this.asset = asset;
	}
	
	/**
	 * @return the DrawableAsset that this is being drawn as.
	 */
	public DrawableAsset getDrawableAsset()
	{
		return asset;
	}
	
	/**
	 * Draws this's DrawableAsset to the provided Texture at this's position in on-screen coordinates.
	 * 
	 * @param renderTo the Texture to draw this to
	 */
	public void render(Texture renderTo)
	{
		if (asset != null && getLevel() != null)
		{
			asset.render(renderTo, getLevel().worldToScreen(position));
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (!isServerSide())
		{
			if (asset instanceof ITickable) ((ITickable) asset).tick();
		}
	}
}
