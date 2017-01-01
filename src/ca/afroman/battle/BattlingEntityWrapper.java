package ca.afroman.battle;

import ca.afroman.assets.Texture;
import ca.afroman.entity.api.Entity;
import ca.afroman.interfaces.ITickable;
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
	
	public Entity getFightingEnemy()
	{
		return fighting;
	}
	
	public boolean isThisTurn()
	{
		return isThisTurn;
	}
	
	public void setIsThisTurn(boolean isThisTurn)
	{
		this.isThisTurn = isThisTurn;
	}
	
	public abstract void tick();
	
	public abstract void render(Texture renderTo);
}
