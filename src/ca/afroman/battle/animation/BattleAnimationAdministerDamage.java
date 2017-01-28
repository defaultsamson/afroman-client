package ca.afroman.battle.animation;

import ca.afroman.assets.Texture;
import ca.afroman.battle.BattleEntity;
import ca.afroman.light.LightMap;

public class BattleAnimationAdministerDamage extends BattleAnimation
{
	private BattleEntity toDamage;
	private int ticksToWait;
	private int damage;
	
	public BattleAnimationAdministerDamage(boolean isServerSide, BattleEntity toDamage, int ticksToWait, int damage)
	{
		super(isServerSide);
		
		this.toDamage = toDamage;
		this.ticksToWait = ticksToWait;
		this.damage = damage;
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
		ticksToWait--;
		
		if (ticksToWait == 0)
		{
			toDamage.addHealth(damage);
			removeFromBattleEntity();
		}
	}
}
