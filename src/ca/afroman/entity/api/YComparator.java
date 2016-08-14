package ca.afroman.entity.api;

import java.util.Comparator;

/**
 * Sorts entities by their Y ordinate value from highest to lowest.
 */
public class YComparator implements Comparator<Entity>
{
	@Override
	public int compare(Entity e1, Entity e2)
	{
		if (e1.getPosition().getY() > e2.getPosition().getY())
		{
			return 1;
		}
		else if (e1.getPosition().getY() < e2.getPosition().getY())
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}
}
