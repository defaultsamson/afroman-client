package ca.afroman.events;

import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.Tile;
import ca.afroman.level.api.Level;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ColourUtil;

public class PlateTrigger extends HitboxTrigger
{
	private static final double HITBOX_WIDTH = 10;
	private static final double HITBOX_HEIGHT = 1;
	private static final double HITBOX_X_OFF = 3;
	private static final double HITBOX_Y_OFF = 5;
	
	private Tile pressed;
	private Tile released;
	
	private boolean lastIsPressed = false;
	
	public PlateTrigger(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position, List<Integer> inTriggers, List<Integer> outTriggers, List<TriggerType> triggerTypes, int doorColour)
	{
		super(isServerSide, isMicromanaged, position, inTriggers, outTriggers, triggerTypes, new Hitbox(isServerSide, true, HITBOX_X_OFF, HITBOX_Y_OFF, HITBOX_WIDTH, HITBOX_HEIGHT));
		
		pressed = new Tile(Level.DEFAULT_DYNAMIC_TILE_LAYER_INDEX, true, position, isServerSide ? null : Assets.getDrawableAsset(AssetType.TILE_PLATE_DOWN).clone().replaceColour(ColourUtil.TILE_REPLACE_COLOUR, doorColour));
		released = new Tile(Level.DEFAULT_DYNAMIC_TILE_LAYER_INDEX, true, position, isServerSide ? null : Assets.getDrawableAsset(AssetType.TILE_PLATE_UP).clone().replaceColour(ColourUtil.TILE_REPLACE_COLOUR, doorColour));
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
				
				updateTile(true);
			}
			
			level.getEvents().add(this);
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		updateTile();
	}
	
	private void updateTile()
	{
		updateTile(false);
	}
	
	private void updateTile(boolean forceUpdate)
	{
		if (!isServerSide())
		{
			updateInput();
			
			boolean isPressed = input.isPressed();
			
			if (isPressed != lastIsPressed || forceUpdate)
			{
				if (isPressed)
				{
					pressed.addToLevel(level);
					released.removeFromLevel();
				}
				else
				{
					released.addToLevel(level);
					pressed.removeFromLevel();
				}
				
				lastIsPressed = isPressed;
			}
		}
	}
}
