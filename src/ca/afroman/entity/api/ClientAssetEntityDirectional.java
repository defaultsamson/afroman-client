package ca.afroman.entity.api;

import ca.afroman.assets.Asset;
import ca.afroman.assets.AssetType;
import ca.afroman.resource.Vector2DDouble;

public class ClientAssetEntityDirectional extends ClientAssetEntity
{
	protected Asset up;
	protected Asset down;
	protected Asset left;
	protected Asset right;
	protected Asset idleUp;
	protected Asset idleDown;
	protected Asset idleLeft;
	protected Asset idleRight;
	
	public ClientAssetEntityDirectional(boolean isServerSide, int id, AssetType assetType, Asset up, Asset down, Asset left, Asset right, Asset idleUp, Asset idleDown, Asset idleLeft, Asset idleRight, Vector2DDouble pos, Hitbox... hitboxes)
	{
		super(isServerSide, id, assetType, pos, hitboxes);
		
		this.up = up;
		this.down = down;
		this.left = left;
		this.right = right;
		this.idleUp = idleUp;
		this.idleDown = idleDown;
		this.idleLeft = idleLeft;
		this.idleRight = idleRight;
	}
	
	@Override
	public void tick()
	{
		if (!isServerSide())
		{
			// If player is moving sideways, determine asset by x
			if (direction.getXAmplitude() != 0)
			{
				switch (direction.getXAmplitude())
				{
					default:
						break;
					case 1:
						asset = right;
						break;
					case -1:
						asset = left;
						break;
				}
				
			}
			// else determine it by y
			else if (direction.getYAmplitude() != 0)
			{
				switch (direction.getYAmplitude())
				{
					default:
						break;
					case 1:
						asset = down;
						break;
					case -1:
						asset = up;
						break;
				}
			}
			else // Else if not moving at all
			{
				if (lastDirection.getXAmplitude() != 0)
				{
					switch (lastDirection.getXAmplitude())
					{
						default:
							break;
						case 1:
							asset = idleRight;
							break;
						case -1:
							asset = idleLeft;
							break;
					}
					
				}
				else if (lastDirection.getYAmplitude() != 0)
				{
					switch (lastDirection.getYAmplitude())
					{
						default:
							break;
						case 1:
							asset = idleDown;
							break;
						case -1:
							asset = idleUp;
							break;
					}
				}
				// Absolute default is idle down
				else
				{
					asset = idleDown;
				}
			}
		}
		
		super.tick();
	}
}
