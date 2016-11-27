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
		if (e1 == null || e2 == null || e1.getAsset() == null || e2.getAsset() == null || e1.getPosition() == null || e2.getPosition() == null) return 0;
		
		int e1Y = (int) (e1.getAsset().getHeight() + e1.getPosition().getY());
		int e2Y = (int) (e2.getAsset().getHeight() + e2.getPosition().getY());
		
		if (e1.getAsset() instanceof ITextureDrawable)
		{
			e1Y += ((ITextureDrawable) e1.getAsset()).getDisplayedTexture().getYComparatorOffset();
		}
		
		if (e1.getAsset() instanceof ITextureDrawable)
		{
			e2Y += ((ITextureDrawable) e2.getAsset()).getDisplayedTexture().getYComparatorOffset();
		}
		
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
