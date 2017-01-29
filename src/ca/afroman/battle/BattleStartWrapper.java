package ca.afroman.battle;

import ca.afroman.entity.PlayerEntity;
import ca.afroman.entity.api.Entity;

public class BattleStartWrapper
{
	private Entity e;
	private PlayerEntity p;
	
	public BattleStartWrapper(Entity e, PlayerEntity p)
	{
		this.e = e;
		this.p = p;
	}
	
	public Entity getEntity()
	{
		return e;
	}
	
	public PlayerEntity getPlayerEntity()
	{
		return p;
	}
}
