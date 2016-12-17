package ca.afroman.events;

import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.DrawableAsset;
import ca.afroman.entity.Tile;
import ca.afroman.entity.api.Direction;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.level.api.Level;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ColourUtil;

public class DoorEvent extends HitboxToggle
{
	private static Hitbox getDefaultHitbox(boolean isServerSide, Direction dir)
	{
		switch (dir)
		{
			default:
			case UP:
				return new Hitbox(isServerSide, true, 8, 6, 16, 4);
			case DOWN:
				return new Hitbox(isServerSide, true, 8, 2, 16, 12);
			case LEFT:
			case RIGHT:
				return new Hitbox(isServerSide, true, 2, 4, 12, 22);
		}
		
	}
	
	Tile open;
	Tile openBottom;
	Tile closed;
	Tile closedBottom;
	
	public DoorEvent(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position, List<Integer> inTriggers, List<Integer> outTriggers, Direction doorLooking, int doorColour)
	{
		super(isServerSide, isMicromanaged, position, inTriggers, outTriggers, getDefaultHitbox(isServerSide, doorLooking));
		
		if (!isServerSide)
		{
			DrawableAsset open = getOpen(doorLooking);
			DrawableAsset closed = getClosed(doorLooking);
			
			// Changes the colours of the door to that of which is specified
			open = open.clone().replaceColour(ColourUtil.TILE_REPLACE_COLOUR, doorColour);
			closed = closed.clone().replaceColour(ColourUtil.TILE_REPLACE_COLOUR, doorColour);
			
			// TODO cannot move the Tile with this
			this.open = new Tile(Level.DEFAULT_DYNAMIC_TILE_LAYER_INDEX, true, position, open);
			this.closed = new Tile(Level.DEFAULT_DYNAMIC_TILE_LAYER_INDEX, true, position, closed);
			
			DrawableAsset openBottom = getOpenBottom(doorLooking);
			
			// TODO having Tiles and entities able to use a Texture array for drawing would fix this, as it needs 2 textures with two different yComparatorOffsets
			// Must be a direction that supports having two tiles
			if (openBottom != null)
			{
				DrawableAsset closedBottom = getClosedBottom(doorLooking);
				
				// Changes the colours of the door to that of which is specified
				openBottom = openBottom.clone().replaceColour(ColourUtil.TILE_REPLACE_COLOUR, doorColour);
				closedBottom = closedBottom.clone().replaceColour(ColourUtil.TILE_REPLACE_COLOUR, doorColour);
				
				// TODO cannot move the Tile with this
				this.openBottom = new Tile(Level.DEFAULT_DYNAMIC_TILE_LAYER_INDEX, true, position.clone().add(0, 16), openBottom);
				this.closedBottom = new Tile(Level.DEFAULT_DYNAMIC_TILE_LAYER_INDEX, true, position.clone().add(0, 16), closedBottom);
			}
			else
			{
				this.openBottom = null;
				this.closedBottom = null;
			}
		}
	}
	
	@Override
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		boolean wasEnabled = enabled;
		
		if (level != null)
		{
			setEnabled(false);
			
			if (!isServerSide())
			{
				// Removes any tiles from their levels (If any are active)
				open.removeFromLevel();
				closed.removeFromLevel();
				
				if (openBottom != null)
				{
					openBottom.removeFromLevel();
					closedBottom.removeFromLevel();
				}
			}
			
			level.getEvents().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			if (!isServerSide())
			{
				// Puts any tiles back to the dynamic ayer of the new level
				open.setLayer(level.getDynamicLayer());
				closed.setLayer(level.getDynamicLayer());
				
				if (openBottom != null)
				{
					openBottom.setLayer(level.getDynamicLayer());
					closedBottom.setLayer(level.getDynamicLayer());
				}
			}
			
			setEnabled(wasEnabled);
			level.getEvents().add(this);
		}
	}
	
	private DrawableAsset getClosed(Direction dir)
	{
		switch (dir)
		{
			default:
			case UP:
				return Assets.getDrawableAsset(AssetType.TILE_DOOR_UP_CLOSED);
			case DOWN:
				return Assets.getDrawableAsset(AssetType.TILE_DOOR_DOWN_CLOSED);
			case LEFT:
				return Assets.getDrawableAsset(AssetType.TILE_DOOR_LEFT_TOP_CLOSED);
			case RIGHT:
				return Assets.getDrawableAsset(AssetType.TILE_DOOR_RIGHT_TOP_CLOSED);
		}
	}
	
	private DrawableAsset getClosedBottom(Direction dir)
	{
		switch (dir)
		{
			default:
				return null;
			case LEFT:
				return Assets.getDrawableAsset(AssetType.TILE_DOOR_LEFT_BOTTOM_CLOSED);
			case RIGHT:
				return Assets.getDrawableAsset(AssetType.TILE_DOOR_RIGHT_BOTTOM_CLOSED);
		}
	}
	
	private DrawableAsset getOpen(Direction dir)
	{
		switch (dir)
		{
			default:
			case UP:
				return Assets.getDrawableAsset(AssetType.TILE_DOOR_UP_OPEN);
			case DOWN:
				return Assets.getDrawableAsset(AssetType.TILE_DOOR_DOWN_OPEN);
			case LEFT:
				return Assets.getDrawableAsset(AssetType.TILE_DOOR_LEFT_TOP_OPEN);
			case RIGHT:
				return Assets.getDrawableAsset(AssetType.TILE_DOOR_RIGHT_TOP_OPEN);
		}
	}
	
	private DrawableAsset getOpenBottom(Direction dir)
	{
		switch (dir)
		{
			default:
				return null;
			case LEFT:
				return Assets.getDrawableAsset(AssetType.TILE_DOOR_LEFT_BOTTOM_OPEN);
			case RIGHT:
				return Assets.getDrawableAsset(AssetType.TILE_DOOR_RIGHT_BOTTOM_OPEN);
		}
	}
	
	@Override
	public void setEnabled(boolean isActive)
	{
		super.setEnabled(isActive);
		
		if (!isServerSide())
		{
			if (isActive)
			{
				open.removeFromLevel();
				closed.addToLevel(level);
				
				if (openBottom != null)
				{
					openBottom.removeFromLevel();
					closedBottom.addToLevel(level);
				}
			}
			else
			{
				closed.removeFromLevel();
				open.addToLevel(level);
				
				if (openBottom != null)
				{
					closedBottom.removeFromLevel();
					openBottom.addToLevel(level);
				}
			}
		}
	}
}
