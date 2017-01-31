package ca.afroman.battle.animation;

import ca.afroman.assets.Texture;
import ca.afroman.battle.BattlePosition;
import ca.afroman.game.Game;
import ca.afroman.light.LightMap;
import ca.afroman.log.ALogType;
import ca.afroman.resource.Vector2DDouble;

public class BattleAnimationEnter extends BattleAnimation
{
	private static final double PIXELS_PER_METER = 16;
	private static final double ACCELERATION = (PIXELS_PER_METER * 9.81) / (60D * 60D); // 9.81 m/s^2 translated to pixels/tick^2
	private double velocity = 4D;
	
	private Vector2DDouble position;
	private double originY;
	
	/**
	 * Interpolates <b>position</b> from <b>from</b> to <b>to</b> over <b>frames</b> amount of ticks.
	 * 
	 * @param from
	 * @param to
	 * @param frames
	 * @param position
	 */
	public BattleAnimationEnter(boolean isServerSide, BattlePosition pos, Vector2DDouble position)
	{
		super(isServerSide);
		
		if (!isServerSide)
		{
			this.position = position;
			originY = position.getY();
			position.setY(-40);
		}
		else
		{
			Game.instance(isServerSide()).logger().log(ALogType.CRITICAL, "BattleAnimationEnter should never be on the server-side. It's only here to look cool for the client.");
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
		if (!isServerSide())
		{
			position.setY(position.getY() + velocity);
			
			if (position.getY() > originY)
			{
				position.setY(originY);
				removeFromBattleEntity();
			}
			else
			{
				velocity += ACCELERATION;
			}
		}
		else
		{
			removeFromBattleEntity();
		}
	}
}
