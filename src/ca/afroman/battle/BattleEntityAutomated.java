package ca.afroman.battle;

import ca.afroman.entity.api.Entity;

public abstract class BattleEntityAutomated extends BattleEntity
{
	private boolean isTurnLast;
	
	public BattleEntityAutomated(Entity levelEntity, BattlePosition pos)
	{
		super(levelEntity, pos);
		
		if (isServerSide())
		{
			isTurnLast = false;
		}
	}
	
	protected abstract void aiAttack(BattlePlayerEntity pl1, BattlePlayerEntity pl2);
	
	@Override
	public void tick()
	{
		// framework for automated turns that only triggers when it first becomes this entity's turn
		if (isServerSide())
		{
			if (isThisTurn() && !isTurnLast)
			{
				isTurnLast = isThisTurn();
				aiAttack(getLevelEntity().getBattle().getPlayer1(), getLevelEntity().getBattle().getPlayer2());
			}
			else
			{
				isTurnLast = isThisTurn();
			}
		}
	}
}
