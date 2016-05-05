package ca.afroman.entity;

import java.awt.geom.Rectangle2D;
import java.util.List;

import ca.afroman.level.ClientLevel;
import ca.afroman.server.AssetType;

public class ClientEntity extends Entity
{
	protected boolean cameraFollow;
	
	public ClientEntity(ClientLevel level, AssetType asset, double x, double y, double width, double height, Rectangle2D.Double hitbox)
	{
		super(level, asset, x, y, width, height, hitbox);
		
		cameraFollow = false;
	}
	
	public ClientEntity(ClientLevel level, AssetType asset, double x, double y, double width, double height, List<Rectangle2D.Double> hitboxes)
	{
		super(level, asset, x, y, width, height, hitboxes);
		
		cameraFollow = false;
	}
	
	public ClientEntity(ClientLevel level, AssetType asset, double x, double y, double width, double height, Rectangle2D.Double... hitboxes)
	{
		super(level, asset, x, y, width, height, hitboxes);
		
		cameraFollow = false;
	}
	
	/**
	 * Makes the level camera follow this Entity or not.
	 * 
	 * @param follow whether or not to follow
	 */
	public void setCameraToFollow(boolean follow)
	{
		cameraFollow = follow;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (cameraFollow)
		{
			getLevel().setCameraCenterInWorld(x + (width / 2), y + (height / 2));
		}
	}
	
	@Override
	public ClientLevel getLevel()
	{
		return (ClientLevel) super.getLevel();
	}
}
