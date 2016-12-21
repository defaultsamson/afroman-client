package ca.afroman.entity;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.Item;
import ca.afroman.inventory.ItemType;
import ca.afroman.resource.Vector2DDouble;

public class BrassKnuckles extends Item
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
	public BrassKnuckles(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position)
	{
		super(isServerSide, isMicromanaged, position, Assets.getDrawableAsset(AssetType.ITEM_KNUCKLES_SMALL), new Hitbox(isServerSide, true, 2D, 2D, 4D, 4D), ItemType.BRASS_KNUCKLES, Assets.getDrawableAsset(AssetType.ITEM_KNUCKLES_LARGE));
	}
}
