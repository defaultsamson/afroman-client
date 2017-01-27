package ca.afroman.battle;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.api.Entity;
import ca.afroman.game.Game;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.LightMap;
import ca.afroman.log.ALogType;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.battle.PacketSelectEntityClientServer;
import ca.afroman.packet.battle.PacketSelectEntityServerClient;
import ca.afroman.resource.ServerClientObject;
import ca.afroman.server.ServerGame;

public abstract class BattleEntity extends ServerClientObject implements ITickable
{
	protected int maxHealth;
	protected int health;
	private boolean isThisTurn;
	private boolean isThisSelected;
	private Entity levelEntity;
	private BattlePosition pos;
	
	// Client only
	protected Font blackFont;
	protected Font whiteFont;
	
	public BattleEntity(Entity levelEntity, BattlePosition pos)
	{
		super(levelEntity.isServerSide());
		
		if (!isServerSide())
		{
			blackFont = Assets.getFont(AssetType.FONT_BLACK);
			whiteFont = Assets.getFont(AssetType.FONT_WHITE);
		}
		
		maxHealth = 100;
		health = 100;
		isThisTurn = false;
		isThisSelected = false;
		this.levelEntity = levelEntity;
		this.pos = pos;
	}
	
	/**
	 * @param deltaHealth
	 * @return the allowed deltaHealth
	 */
	public int addHealth(int deltaHealth)
	{
		int prev = health;
		health = Math.min(Math.max(0, health + deltaHealth), maxHealth);
		return health - prev;
	}
	
	public void executeBattle(int battleID, int deltaHealth)
	{
		getBattle().setIsSelectingAttack(false);
	}
	
	public void finishTurn()
	{
		if (levelEntity != null)
		{
			getBattle().progressTurn();
		}
		else
		{
			Game.instance(isServerSide()).logger().log(ALogType.CRITICAL, "Couldn't finish turn because BattleScene object could not be found for the level entity because the level entity is null");
		}
	}
	
	protected BattleScene getBattle()
	{
		if (getLevelEntity() == null)
		{
			Game.instance(isServerSide()).logger().log(ALogType.CRITICAL, "BattleScene is null for " + this);
			return null;
		}
		
		return getLevelEntity().getBattle();
	}
	
	public BattlePosition getBattlePosition()
	{
		return pos;
	}
	
	public Entity getLevelEntity()
	{
		return levelEntity;
	}
	
	public boolean isAlive()
	{
		System.out.println("is alive: " + health);
		return health > 0;
	}
	
	public boolean isThisSelected()
	{
		return isThisSelected;
	}
	
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
	public final void setIsSelected(boolean isThisSelected)
	{
		setIsSelected(isThisSelected, null);
	}
	
	/**
	 * <b>WARNING:</b> Only designed to be used by BattleScene.
	 * 
	 * @param isThisTurn
	 */
	public final void setIsSelected(boolean isThisSelected, IPConnection sender)
	{
		this.isThisSelected = isThisSelected;
		
		if (isThisSelected)
		{
			if (isServerSide())
			{
				ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketSelectEntityServerClient(getLevelEntity().getBattle().getID(), pos), sender);
			}
			else if (sender == null)
			{
				ClientGame.instance().sockets().sender().sendPacket(new PacketSelectEntityClientServer(pos));
			}
		}
	}
	
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
