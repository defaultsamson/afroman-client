package ca.afroman.battle.animation;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;
import ca.afroman.light.LightMap;

public class BattleAnimationDisplayDeltaHealth extends BattleAnimation
{
	protected Font blackFont;
	protected Font whiteFont;
	
	private int damageTaken;
	private int travelFrames;
	private int idleFrames;
	private double xTarget;
	private double yTarget;
	
	public BattleAnimationDisplayDeltaHealth(boolean isServerSide, int damageTaken, int travelFrames, int idleFrames, double xTarget, double yTarget)
	{
		super(isServerSide);
		
		this.damageTaken = damageTaken;
		this.travelFrames = travelFrames;
		this.idleFrames = idleFrames;
		this.xTarget = xTarget;
		this.yTarget = yTarget;
		
		blackFont = Assets.getFont(AssetType.FONT_BLACK);
		whiteFont = Assets.getFont(AssetType.FONT_WHITE);
	}
	
	@Override
	public void render(Texture renderTo, LightMap lightmap)
	{
		
	}
	
	@Override
	public void renderPostLightmap(Texture renderTo)
	{
		String display = damageTaken > 0 ? "+" + damageTaken : "" + damageTaken;
		
		blackFont.renderCentered(renderTo, (int) xTarget + 1, (int) yTarget + travelFrames + 1, display);
		whiteFont.renderCentered(renderTo, (int) xTarget, (int) yTarget + travelFrames, display);
	}
	
	@Override
	public void tick()
	{
		if (travelFrames > 0)
		{
			travelFrames--;
		}
		else if (idleFrames > 0)
		{
			idleFrames--;
		}
		else
		{
			removeFromBattleEntity();
		}
	}
}
