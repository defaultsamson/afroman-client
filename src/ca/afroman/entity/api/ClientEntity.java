package ca.afroman.entity.api;

import ca.afroman.assets.AssetType;
import ca.afroman.level.ClientLevel;
import ca.afroman.resource.Vector2DDouble;

public class ClientEntity extends Entity
{
	protected boolean cameraFollow;
	
	public ClientEntity(int id, AssetType assetType, Vector2DDouble pos, Hitbox... hitboxes)
	{
		super(false, id, assetType, pos, hitboxes);
		
		cameraFollow = false;
	}
	
	@Override
	public ClientLevel getLevel()
	{
		return (ClientLevel) super.getLevel();
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
			getLevel().setCameraCenterInWorld(new Vector2DDouble(position.getX() + (16 / 2), position.getY() + (16 / 2)));
		}
	}
}
