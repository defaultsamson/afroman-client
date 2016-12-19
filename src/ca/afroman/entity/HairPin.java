package ca.afroman.entity;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.entity.api.GroundItem;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.resource.Vector2DDouble;

public class HairPin extends GroundItem
{
	/**
	 * Creates a new Entity with a hitbox.
	 * 
	 * @param isServerSide whether this is on the server instance or not
	 * @param isMicromanaged whether this is managed by an external manager (such as an Event), as opposed to directly being managed by the level
	 * @param position the position
	 * @param asset the DrawableAsset to render this as
	 * @param hitboxes the hitboxes, only relative to this, <i>not</i> the world
	 */
	public HairPin(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position)
	{
		super(isServerSide, isMicromanaged, position, Assets.getDrawableAsset(AssetType.ICON_UPDATE), new Hitbox(isServerSide, true, 2D, 2D, 5D, 5D)); // TODO hitbox
	}
	
	@Override
	public void onInteract()
	{
		System.out.println("Dong");
	}
}
