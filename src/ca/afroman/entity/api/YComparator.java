package ca.afroman.entity.api;

import java.util.Comparator;

import ca.afroman.assets.ITextureDrawable;

/**
 * Sorts entities by their Y ordinate value from highest to lowest.
 */
public class YComparator implements Comparator<DrawableEntity>
{
	@Override
	public int compare(DrawableEntity e1, DrawableEntity e2)
	{
		// If one of the entities is null, or doesn't have an asset or position, then simply return that they are equals
		if (e1 == null || e2 == null || e1.getDrawableAsset() == null || e2.getDrawableAsset() == null || e1.getPosition() == null || e2.getPosition() == null) return 0;
		
		// Finds the y position to compare for each entity
		int e1Y = (int) (e1.getDrawableAsset().getHeight() + e1.getPosition().getY());
		int e2Y = (int) (e2.getDrawableAsset().getHeight() + e2.getPosition().getY());
		
		// Also adds any YComparatorOffset if any exists
		if (e1.getDrawableAsset() instanceof ITextureDrawable)
		{
			e1Y += ((ITextureDrawable) e1.getDrawableAsset()).getDisplayedTexture().getYComparatorOffset();
		}
		if (e2.getDrawableAsset() instanceof ITextureDrawable)
		{
			e2Y += ((ITextureDrawable) e2.getDrawableAsset()).getDisplayedTexture().getYComparatorOffset();
		}
		
		// End equation for each Y value is
		// yPosition = position + drawHeight + yComparatorOffset
		
		// Then returns results based on which Entity appears farther back (higher) than the other
		if (e1Y > e2Y)
		{
			return 1;
		}
		else if (e1Y < e2Y)
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}
}
