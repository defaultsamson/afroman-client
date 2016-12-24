package ca.afroman.entity;

import java.util.Random;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.entity.api.DrawableEntityDirectional;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.resource.ModulusCounter;
import ca.afroman.resource.Vector2DDouble;

public class Crab extends DrawableEntityDirectional
{
	private ModulusCounter moveWaiter;
	
	public Crab(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position)
	{
		super(isServerSide, isMicromanaged, position, isServerSide ? null : Assets.getSpriteAnimation(AssetType.CRAB_UP), isServerSide ? null : Assets.getSpriteAnimation(AssetType.CRAB_DOWN), isServerSide ? null : Assets.getSpriteAnimation(AssetType.CRAB_LEFT), isServerSide ? null : Assets.getSpriteAnimation(AssetType.CRAB_RIGHT), isServerSide ? null : Assets.getSpriteAnimation(AssetType.CRAB_IDLE_UP), isServerSide ? null : Assets.getSpriteAnimation(AssetType.CRAB_IDLE_DOWN), isServerSide ? null : Assets.getSpriteAnimation(AssetType.CRAB_IDLE_LEFT), isServerSide ? null : Assets.getSpriteAnimation(AssetType.CRAB_IDLE_RIGHT), new Hitbox(isServerSide, true, 3, 5, 10, 11));
		
		moveWaiter = isServerSide ? new ModulusCounter(60 * 9) : null;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (isServerSide())
		{
			if (moveWaiter.isAtInterval())
			{
				// do an AI idle move
				
				Random rand = new Random();
				Random rand2 = new Random();
				
				boolean x = rand.nextBoolean();
				
				if (x)
				{
					autoMove(rand2.nextBoolean() ? (byte) 16 : -16, (byte) 0);
				}
				else
				{
					autoMove((byte) 0, rand2.nextBoolean() ? (byte) 16 : -16);
				}
			}
		}
	}
	
	@Override
	public void tryInteract(PlayerEntity triggerer)
	{
		
	}
}
