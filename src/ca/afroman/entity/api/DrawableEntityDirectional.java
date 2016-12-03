package ca.afroman.entity.api;

import ca.afroman.assets.DrawableAsset;
import ca.afroman.resource.Vector2DDouble;

public class DrawableEntityDirectional extends DrawableEntity
{
	private DrawableAsset up;
	private DrawableAsset down;
	private DrawableAsset left;
	private DrawableAsset right;
	private DrawableAsset idleUp;
	private DrawableAsset idleDown;
	private DrawableAsset idleLeft;
	private DrawableAsset idleRight;
	
	/**
	 * Creates a new Entity with a hitbox.
	 * 
	 * @param isServerSide whether this is on the server instance or not
	 * @param isMicromanaged whether this is managed by an external manager (such as an Event), as opposed to directly being managed by the level
	 * @param position the position
	 * @param up the DrawableAsset to render this as when moving up
	 * @param down the DrawableAsset to render this as when moving down
	 * @param left the DrawableAsset to render this as when moving left
	 * @param right the DrawableAsset to render this as when moving right
	 * @param idleUp the DrawableAsset to render this as when idle and facing up
	 * @param idleDown the DrawableAsset to render this as when idle and facing down
	 * @param idleLeft the DrawableAsset to render this as when idle and facing left
	 * @param idleRight the DrawableAsset to render this as when idle and facing right
	 * @param hitboxes the hitboxes, only relative to this, <i>not</i> the world
	 */
	public DrawableEntityDirectional(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position, DrawableAsset up, DrawableAsset down, DrawableAsset left, DrawableAsset right, DrawableAsset idleUp, DrawableAsset idleDown, DrawableAsset idleLeft, DrawableAsset idleRight, Hitbox... hitboxes)
	{
		super(isServerSide, isMicromanaged, position, null, hitboxes);
		
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
