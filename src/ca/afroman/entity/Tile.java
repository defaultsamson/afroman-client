package ca.afroman.entity;

import java.util.ArrayList;

import ca.afroman.assets.DrawableAsset;
import ca.afroman.entity.api.DrawableEntity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.level.api.Level;
import ca.afroman.resource.Vector2DDouble;

public class Tile extends DrawableEntity
{
	private int layer;
	
	/**
	 * @param layer the layer that this is on in the level
	 * @param isMicromanaged whether this is managed by an external manager (such as an Event), as opposed to directly being managed by the level
	 * @param position the position
	 * @param asset the DrawableAsset to render this as
	 * @param hitboxes the hitboxes, only relative to this, <i>not</i> the world
	 */
	public Tile(int layer, boolean isMicromanaged, Vector2DDouble position, DrawableAsset asset, Hitbox... hitboxes)
	{
		super(false, isMicromanaged, position, asset, hitboxes);
		
		this.layer = layer;
	}
	
	@Override
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		if (level != null)
		{
			synchronized (level.getTileLayers())
			{
				// If it can't remove this from the layer that this is supposed to be in
				if (!level.getTiles(this.layer).remove(this))
				{
					// Searches all the old layers in case the old tile isn't on the same layer as the new one being specified
					for (ArrayList<Tile> tiles : level.getTileLayers())
					{
						if (tiles.contains(this))
						{
							tiles.remove(this);
						}
					}
				}
			}
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			synchronized (level.getTiles(layer))
			{
				level.getTiles(layer).add(this);
			}
		}
	}
	
	/**
	 * @return the current layer that this is on in the current level that it is in.
	 */
	public int getLayer()
	{
		return layer;
	}
	
	/**
	 * Proper usage is to make sure that this is not in a
	 * Level object before setting the layer.
	 * <p>
	 * e.g.
	 * <p>
	 * <code>tile.removeFromLevel();</code>
	 * <p>
	 * <code>tile.setLayer(2);</code>
	 * <p>
	 * <code>tile.addToLevel(level);</code>
	 * 
	 * @param layer
	 */
	public void setLayer(int layer)
	{
		this.layer = layer;
	}
}
