package ca.afroman.entity.api;

import ca.afroman.assets.AssetType;
import ca.afroman.level.ClientLevel;

public class ClientEntity extends Entity
{
	protected boolean cameraFollow;
	
	public ClientEntity(int id, AssetType assetType, double x, double y, Hitbox... hitboxes)
	{
		super(id, assetType, x, y, hitboxes);
		
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
			getLevel().setCameraCenterInWorld(x + (16 / 2), y + (16 / 2));
		}
	}
	
	@Override
	public ClientLevel getLevel()
	{
		return (ClientLevel) super.getLevel();
	}
}
