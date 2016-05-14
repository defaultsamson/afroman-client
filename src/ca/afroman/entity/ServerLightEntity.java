package ca.afroman.entity;

import ca.afroman.assets.AssetType;
import ca.afroman.entity.api.Entity;
import ca.afroman.level.Level;

public class ServerLightEntity extends Entity
{
	public ServerLightEntity(int id, Level level, double x, double y, double radius)
	{
		super(id, level, AssetType.INVALID, x, y, radius, radius);
	}
	
	public double getRadius()
	{
		return width / 2;
	}
}
