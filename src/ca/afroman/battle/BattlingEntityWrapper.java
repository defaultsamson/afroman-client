package ca.afroman.battle;

import ca.afroman.assets.Texture;
import ca.afroman.entity.api.Entity;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.LightMap;
import ca.afroman.resource.ServerClientObject;

public abstract class BattlingEntityWrapper extends ServerClientObject implements ITickable
{
	private boolean isThisTurn;
	private Entity fighting;
	
	public BattlingEntityWrapper(Entity fighting)
	{
		super(fighting.isServerSide());
		
		isThisTurn = false;
		this.fighting = fighting;
	}
	
	public abstract void executeBattle(int battleID);
	
	public Entity getFightingEnemy()
	{
		return fighting;
	}
	
	public boolean isThisTurn()
	{
		return isThisTurn;
	}
	
	public abstract void render(Texture renderTo, LightMap lightmap);
	
	public void setIsThisTurn(boolean isThisTurn)
	{
		this.isThisTurn = isThisTurn;
	}
}
