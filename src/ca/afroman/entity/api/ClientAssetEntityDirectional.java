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
	
	public ClientAssetEntityDirectional(int id, AssetType assetType, Asset up, Asset down, Asset left, Asset right, Asset idleUp, Asset idleDown, Asset idleLeft, Asset idleRight, Vector2DDouble pos, Hitbox... hitboxes)
	{
		super(id, assetType, pos, hitboxes);
		
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
		switch (direction)
		{
			case UP:
				asset = up;
				break;
			default:
			case DOWN:
				asset = down;
				break;
			case LEFT:
				asset = left;
				break;
			case RIGHT:
				asset = right;
				break;
			case NONE:
				switch (lastDirection)
				{
					case UP:
						asset = idleUp;
						break;
					default:
					case DOWN:
						asset = idleDown;
						break;
					case LEFT:
						asset = idleLeft;
						break;
					case RIGHT:
						asset = idleRight;
						break;
				}
				break;
		}
		
		super.tick();
	}
}
