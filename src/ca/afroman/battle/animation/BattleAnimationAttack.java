package ca.afroman.battle.animation;

import ca.afroman.assets.Texture;
import ca.afroman.battle.BattlePosition;
import ca.afroman.light.LightMap;
import ca.afroman.resource.Vector2DDouble;

public class BattleAnimationAttack extends BattleAnimation
{
	private double xInterpolation;
	private double yInterpolation;
	private Vector2DDouble position;
	private double originX;
	private double originY;
	
	private int ticksUntilPass;
	private int upperFrameBound;
	private int lowerFrameBound;
	
	/**
	 * Interpolates <b>position</b> from <b>from</b> to <b>to</b> over <b>frames</b> amount of ticks.
	 * 
	 * @param from
	 * @param to
	 * @param frames
	 * @param position
	 */
	public BattleAnimationAttack(boolean isServerSide, BattlePosition from, BattlePosition to, int travelFrames, int idleFrames, Vector2DDouble position)
	{
		super(isServerSide);
		
		if (!isServerSide)
		{
			xInterpolation = (from.getReferenceX() - to.getReferenceX()) / (double) travelFrames;
			yInterpolation = (from.getReferenceY() - to.getReferenceY()) / (double) travelFrames;
			this.position = position;
			originX = position.getX();
			originY = position.getY();
			
			lowerFrameBound = travelFrames;
			upperFrameBound = travelFrames + idleFrames;
		}
		
		ticksUntilPass = travelFrames + idleFrames + travelFrames;
	}
	
	@Override
	public void render(Texture renderTo, LightMap lightmap)
	{
		
	}
	
	@Override
	public void renderPostLightmap(Texture renderTo)
	{
		
	}
	
	@Override
	public void tick()
	{
		ticksUntilPass--;
		
		if (!isServerSide())
		{
			if (ticksUntilPass > upperFrameBound)
			{
				position.add(-xInterpolation, -yInterpolation);
			}
			else if (ticksUntilPass < lowerFrameBound)
			{
				position.add(xInterpolation, yInterpolation);
			}
		}
		
		if (ticksUntilPass == 0)
		{
			if (isServerSide())
			{
				entity.finishTurn();
			}
			else
			{
				position.setVector(originX, originY);
				removeFromBattleEntity();
			}
		}
	}
}
