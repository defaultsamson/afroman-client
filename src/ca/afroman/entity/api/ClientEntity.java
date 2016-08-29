package ca.afroman.entity.api;

import ca.afroman.assets.AssetType;
import ca.afroman.level.ClientLevel;
import ca.afroman.resource.Vector2DDouble;

public class ClientEntity extends Entity
{
	protected boolean cameraFollow;
	
	// TODO remove ClientEntity. Unify server and client entities
	public ClientEntity(boolean isServerSide, int id, AssetType assetType, Vector2DDouble pos, Hitbox... hitboxes)
	{
		super(isServerSide, id, assetType, pos, hitboxes);
		
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
			if (getLevel() instanceof ClientLevel)
			{
				((ClientLevel) getLevel()).setCameraCenterInWorld(new Vector2DDouble(position.getX() + (16 / 2), position.getY() + (16 / 2)));
			}
		}
	}
}
