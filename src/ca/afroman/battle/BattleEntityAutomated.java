package ca.afroman.battle;

import ca.afroman.entity.api.Entity;

public abstract class BattleEntityAutomated extends BattleEntity
{
	
	public BattleEntityAutomated(Entity levelEntity)
	{
		super(levelEntity);
	}
	
	@Override
	public void tick()
	{
		// TODO framework for automated turns
	}
}
