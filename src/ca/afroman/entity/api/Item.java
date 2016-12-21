package ca.afroman.entity.api;

import ca.afroman.assets.DrawableAsset;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.inventory.ItemType;
import ca.afroman.resource.Vector2DDouble;

public abstract class Item extends DrawableEntity
{
	private DrawableAsset inventoryView;
	private ItemType type;
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
	public Item(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position, DrawableAsset asset, Hitbox detectionBox, ItemType type, DrawableAsset inventoryView)
	{
		super(isServerSide, isMicromanaged, position, asset);
		
		this.type = type;
		
		this.box = detectionBox;
		updateHitboxInLevel(box);
		this.inventoryView = inventoryView;
	}
	
	public DrawableAsset getInventoryView()
	{
		return inventoryView;
	}
	
	public ItemType getItemType()
	{
		return type;
	}
	
	public Hitbox getMathHitbox()
	{
		return box;
	}
	
	public void onInteract(PlayerEntity triggerer)
	{
		if (isServerSide())
		{
			triggerer.getInventory().addItem(this);
		}
		else // TODO maybe do client prediction for pickup ?
		{
			
		}
	}
	
	@Override
	public void removeFromLevel()
	{
		removeFromLevel(true);
	}
	
	public void removeFromLevel(boolean force)
	{
		if (!force)
		{
			if (level != null)
			{
				level.removeEntity(this);
			}
		}
		else
		{
			super.removeFromLevel();
		}
	}
	
	@Override
	public void tryInteract(PlayerEntity triggerer)
	{
		if (triggerer.isColliding(box))
		{
			onInteract(triggerer);
		}
	}
	
	@Override
	protected void updateHitboxInLevel()
	{
		super.updateHitboxInLevel();
		updateHitboxInLevel(box);
	}
}
