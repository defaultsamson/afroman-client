package ca.afroman.entity;

import java.util.Comparator;

import ca.afroman.entity.api.Entity;

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
