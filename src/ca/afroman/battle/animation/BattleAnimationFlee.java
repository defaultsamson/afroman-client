package ca.afroman.battle.animation;

import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.light.LightMap;
import ca.afroman.resource.Vector2DDouble;

public class BattleAnimationFlee extends BattleAnimation
{
	// private static final double PIXELS_PER_METER = 16;
	// private double velocity = 1.4 * PIXELS_PER_METER / 60D; // pixels per tick
	
	private int ticksUntilFinished;
	private double xInterpolation;
	private Vector2DDouble position;
	
	/**
	 * Interpolates <b>position</b> from <b>from</b> to <b>to</b> over <b>frames</b> amount of ticks.
	 * 
	 * @param from
	 * @param to
	 * @param frames
	 * @param position
	 */
	public BattleAnimationFlee(boolean isServerSide, Vector2DDouble position, int frameCount)
	{
		super(isServerSide);
		
		this.ticksUntilFinished = frameCount;
		
		if (!isServerSide)
		{
			this.position = position;
			xInterpolation = (ClientGame.WIDTH - position.getX()) / frameCount;
		}
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
		ticksUntilFinished--;
		
		if (!isServerSide())
		{
			position.add(xInterpolation, 0);
		}
		
		if (ticksUntilFinished <= 0)
		{
			entity.finishTurn();
			entity.getLevelEntity().setBattle(null);
			removeFromBattleEntity();
		}
	}
}
