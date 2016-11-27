package ca.afroman.entity.api;

import ca.afroman.assets.DrawableAsset;
import ca.afroman.resource.Vector2DDouble;

public class DrawableEntityDirectional extends DrawableEntity
{
	protected DrawableAsset up;
	protected DrawableAsset down;
	protected DrawableAsset left;
	protected DrawableAsset right;
	protected DrawableAsset idleUp;
	protected DrawableAsset idleDown;
	protected DrawableAsset idleLeft;
	protected DrawableAsset idleRight;
	
	public DrawableEntityDirectional(boolean isServerSide, boolean isMicromanaged, int id, DrawableAsset up, DrawableAsset down, DrawableAsset left, DrawableAsset right, DrawableAsset idleUp, DrawableAsset idleDown, DrawableAsset idleLeft, DrawableAsset idleRight, Vector2DDouble pos, Hitbox... hitboxes)
	{
		super(isServerSide, isMicromanaged, id, null, pos, hitboxes);
		
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
