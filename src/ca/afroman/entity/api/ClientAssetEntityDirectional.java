package ca.afroman.entity.api;

import ca.afroman.assets.Asset;
import ca.afroman.assets.AssetType;
import ca.afroman.level.ClientLevel;

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
	
	public ClientAssetEntityDirectional(int id, ClientLevel level, AssetType assetType, Asset up, Asset down, Asset left, Asset right, Asset idleUp, Asset idleDown, Asset idleLeft, Asset idleRight, double x, double y, double width, double height, Hitbox... hitboxes)
	{
		super(id, level, assetType, x, y, width, height, hitboxes);
		
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
