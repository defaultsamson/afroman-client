package ca.afroman.battle;

import ca.afroman.assets.Texture;
import ca.afroman.entity.api.Entity;
import ca.afroman.game.Game;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.LightMap;
import ca.afroman.log.ALogType;
import ca.afroman.resource.ServerClientObject;

public abstract class BattleEntity extends ServerClientObject implements ITickable
{
	private Entity levelEntity;
	private boolean isThisTurn = false;
	
	public BattleEntity(Entity levelEntity)
	{
		super(levelEntity.isServerSide());
		
		this.levelEntity = levelEntity;
	}
	
	public abstract void executeBattle(int battleID);
	
	public void finishTurn()
	{
		if (levelEntity != null)
		{
			levelEntity.getBattle().progressTurn();
		}
		else
		{
			Game.instance(isServerSide()).logger().log(ALogType.CRITICAL, "Couldn't finish turn because BattleScene object could not be found for the level entity because the level entity is null");
		}
	}
	
	public Entity getLevelEntity()
	{
		return levelEntity;
	}
	
	public abstract boolean isAlive();
	
	public boolean isThisTurn()
	{
		return isThisTurn;
	}
	
	public abstract void render(Texture renderTo, LightMap lightmap);
	
	public abstract void renderPostLightmap(Texture renderTo);
	
	/**
	 * <b>WARNING:</b> Only designed to be used by BattleScene.
	 * 
	 * @param isThisTurn
	 */
	public void setIsTurn(boolean isThisTurn)
	{
		this.isThisTurn = isThisTurn;
	}
}
