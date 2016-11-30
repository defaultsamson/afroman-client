package ca.afroman.events;

import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.entity.Tile;
import ca.afroman.entity.api.Entity;
import ca.afroman.level.api.Level;
import ca.afroman.resource.Vector2DDouble;

public class PlateTrigger extends HitboxTrigger
{
	private Tile pressed;
	private Tile released;
	
	public PlateTrigger(boolean isServerSide, double x, double y, List<TriggerType> triggerTypes, List<Integer> inTriggers, List<Integer> outTriggers)
	{
		super(isServerSide, x, y, 16, 16, triggerTypes, inTriggers, outTriggers);
		
		pressed = new Tile(Level.DEFAULT_DYNAMIC_TILE_LAYER_INDEX, true, Assets.getDrawableAsset(AssetType.TILE_PLATE_DOWN), new Vector2DDouble(x, y));
		released = new Tile(Level.DEFAULT_DYNAMIC_TILE_LAYER_INDEX, true, Assets.getDrawableAsset(AssetType.TILE_PLATE_UP), new Vector2DDouble(x, y));
	}
	
	@Override
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		if (level != null)
		{
			if (!isServerSide())
			{
				pressed.removeFromLevel();
				released.removeFromLevel();
			}
			
			level.getEvents().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			if (!isServerSide())
			{
				pressed.setLayer(level.getDynamicLayer() - 1);
				released.setLayer(level.getDynamicLayer() - 1);
				
				updateTile();
			}
			
			level.getEvents().add(this);
		}
	}
	
	@Override
	public void onTrigger(Entity triggerer)
	{
		super.onTrigger(triggerer);
		
		// if (!isServerSide())
		// {
		// System.out.println("Succ: " + ((PlayerEntity) triggerer).getRole());
		// }
		
		updateTile();
	}
	
	private void updateTile()
	{
		if (!isServerSide())
		{
			updateInput();
			
			if (input.isPressed())
			{
				pressed.addToLevel(level);
				released.removeFromLevel();
			}
			else
			{
				released.addToLevel(level);
				pressed.removeFromLevel();
			}
		}
	}
}
