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
	
	public Tile(int layer, boolean isMicromanaged, DrawableAsset asset, Vector2DDouble pos, Hitbox... hitboxes)
	{
		super(false, isMicromanaged, MICROMANAGED_ID, asset, pos, hitboxes);
		
		this.layer = layer;
	}
	
	/**
	 * Removes a tile from their current level and puts them in another level.
	 * 
	 * @param level the new level
	 * @param layer the new layer
	 */
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
	
	public int getLayer()
	{
		return layer;
	}
	
	/**
	 * Removes a tile from their current level.
	 */
	@Override
	public void removeFromLevel()
	{
		addToLevel(null);
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
