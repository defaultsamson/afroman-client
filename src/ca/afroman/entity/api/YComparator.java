package ca.afroman.entity.api;

import java.util.Comparator;

/**
 * Sorts entities by their Y ordinate value from highest to lowest.
 */
public class YComparator implements Comparator<DrawableEntity>
{
	@Override
	public int compare(DrawableEntity e1, DrawableEntity e2)
	{
		int e1Y = (int) (e1.getAsset().getHeight() + e1.getPosition().getY());
		int e2Y = (int) (e2.getAsset().getHeight() + e2.getPosition().getY());
		
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
