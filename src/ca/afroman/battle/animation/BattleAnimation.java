package ca.afroman.battle.animation;

import ca.afroman.assets.Texture;
import ca.afroman.battle.BattleEntity;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.LightMap;
import ca.afroman.resource.ServerClientObject;

public abstract class BattleAnimation extends ServerClientObject implements ITickable
{
	protected BattleEntity entity;
	
	public BattleAnimation(boolean isServerSide)
	{
		super(isServerSide);
	}
	
	public void addToBattleEntity(BattleEntity entity)
	{
		if (entity == null) return;
		
		entity.getAnimations().add(this);
		
		this.entity = entity;
	}
	
	public void removeFromBattleEntity()
	{
		entity.removeBattleAnimation(this);
	}
	
	public abstract void render(Texture renderTo, LightMap lightmap);
	
	public abstract void renderPostLightmap(Texture renderTo);
}
