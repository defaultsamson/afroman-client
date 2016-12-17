package ca.afroman.events;

import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.entity.Tile;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.level.api.Level;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ColourUtil;

public class SwitchTrigger extends HitboxTrigger
{
	private static final double HITBOX_WIDTH = 12;
	private static final double HITBOX_HEIGHT = 6;
	private static final double HITBOX_X_OFF = 2;
	private static final double HITBOX_Y_OFF = 6;
	private Hitbox box;
	private boolean flop = false;
	
	private Tile left;
	private Tile right;
	
	public SwitchTrigger(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position, List<Integer> inTriggers, List<Integer> outTriggers, List<TriggerType> triggerTypes, int leftColour, int rightColour)
	{
		super(isServerSide, isMicromanaged, position, inTriggers, outTriggers, triggerTypes, new Hitbox(isServerSide, true, HITBOX_X_OFF, HITBOX_Y_OFF, HITBOX_WIDTH, HITBOX_HEIGHT));
		box = new Hitbox(isServerSide, true, position.getX() + HITBOX_X_OFF + 2, position.getY() + HITBOX_Y_OFF + 2, HITBOX_WIDTH - 4, HITBOX_HEIGHT - 4);
		
		left = new Tile(Level.DEFAULT_DYNAMIC_TILE_LAYER_INDEX, true, position, Assets.getDrawableAsset(AssetType.TILE_SWITCH_LEFT).clone().replaceColour(ColourUtil.TILE_REPLACE_COLOUR, leftColour).replaceColour(ColourUtil.TILE_REPLACE_COLOUR_2, rightColour));
		right = new Tile(Level.DEFAULT_DYNAMIC_TILE_LAYER_INDEX, true, position, Assets.getDrawableAsset(AssetType.TILE_SWITCH_RIGHT).clone().replaceColour(ColourUtil.TILE_REPLACE_COLOUR, leftColour).replaceColour(ColourUtil.TILE_REPLACE_COLOUR_2, rightColour));
	}
	
	@Override
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		if (level != null)
		{
			if (!isServerSide())
			{
				left.removeFromLevel();
				right.removeFromLevel();
			}
			
			box.removeFromLevel();
			level.getEvents().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			if (!isServerSide())
			{
				left.setLayer(level.getDynamicLayer());
				right.setLayer(level.getDynamicLayer());
				
				updateTile();
			}
			
			box.addToLevel(level);
			level.getEvents().add(this);
		}
	}
	
	@Override
	public void trigger(Entity e)
	{
		updateTile();
		
		super.trigger(e);
	}
	
	private void updateTile()
	{
		if (!isServerSide())
		{
			if (flop)
			{
				left.addToLevel(level);
				right.removeFromLevel();
				flop = false;
			}
			else
			{
				right.addToLevel(level);
				left.removeFromLevel();
				flop = true;
			}
		}
	}
}
