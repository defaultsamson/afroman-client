package ca.afroman.entity.api;

import ca.afroman.assets.DrawableAsset;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.resource.Vector2DDouble;

public abstract class GroundItem extends DrawableEntity
{
	private Hitbox box;
	
	/**
	 * Creates a new Entity with a hitbox.
	 * 
	 * @param isServerSide whether this is on the server instance or not
	 * @param isMicromanaged whether this is managed by an external manager (such as an Event), as opposed to directly being managed by the level
	 * @param position the position
	 * @param asset the DrawableAsset to render this as
	 * @param hitboxes the hitboxes, only relative to this, <i>not</i> the world
	 */
	public GroundItem(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position, DrawableAsset asset, Hitbox detectionBox)
	{
		super(isServerSide, isMicromanaged, position, asset);
		
		this.box = detectionBox;
		updateHitboxInLevel(box);
	}
	
	public Hitbox getMathHitbox()
	{
		return box;
	}
	
	public abstract void onInteract();
	
	@Override
	public void tryInteract(PlayerEntity triggerer)
	{
		if (triggerer.isColliding(box))
		{
			onInteract();
		}
	}
	
	@Override
	protected void updateHitboxInLevel()
	{
		super.updateHitboxInLevel();
		updateHitboxInLevel(box);
	}
}
