package ca.afroman.entity;

import ca.afroman.assets.AssetType;
import ca.afroman.level.ClientLevel;

public class ClientEntity extends Entity
{
	protected boolean cameraFollow;
	
	public ClientEntity(int id, ClientLevel level, AssetType asset, double x, double y, double width, double height, Hitbox hitbox)
	{
		super(id, level, asset, x, y, width, height, hitbox);
		
		cameraFollow = false;
	}
	
	public ClientEntity(int id, ClientLevel level, AssetType asset, double x, double y, double width, double height, Hitbox... hitboxes)
	{
		super(id, level, asset, x, y, width, height, hitboxes);
		
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
