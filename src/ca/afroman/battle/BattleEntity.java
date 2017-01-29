package ca.afroman.battle;

import java.util.ArrayList;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;
import ca.afroman.battle.animation.BattleAnimation;
import ca.afroman.battle.animation.BattleAnimationDisplayDeltaHealth;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.api.Entity;
import ca.afroman.events.BattleScene;
import ca.afroman.game.Game;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.LightMap;
import ca.afroman.log.ALogType;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.battle.PacketSelectEntityClientServer;
import ca.afroman.packet.battle.PacketSelectEntityServerClient;
import ca.afroman.resource.ServerClientObject;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.server.ServerGame;

public abstract class BattleEntity extends ServerClientObject implements ITickable
{
	protected int maxHealth;
	protected int health;
	private boolean isThisTurn;
	private boolean isThisSelected;
	private Entity levelEntity;
	private BattlePosition pos;
	
	private ArrayList<BattleAnimation> animations;
	private ArrayList<BattleAnimation> animationsToRemove;
	
	// Client only
	protected Font blackFont;
	protected Font whiteFont;
	protected Vector2DDouble fightPos;
	protected Vector2DDouble originPos;
	
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
		
		animations = new ArrayList<BattleAnimation>();
		animationsToRemove = new ArrayList<BattleAnimation>();
	}
	
	/**
	 * @param deltaHealth
	 * @return the allowed deltaHealth
	 */
	public int addHealth(int deltaHealth)
	{
		return addHealth(deltaHealth, 0);
	}
	
	/**
	 * @param deltaHealth
	 * @return the allowed deltaHealth
	 */
	public int addHealth(int deltaHealth, int clientAssetWidth)
	{
		int prev = health;
		health = Math.min(Math.max(0, health + deltaHealth), maxHealth);
		
		int trueDelta = health - prev;
		
		if (!isServerSide())
		{
			new BattleAnimationDisplayDeltaHealth(isServerSide(), trueDelta, 14, 60, fightPos.getX() + (clientAssetWidth / 2), fightPos.getY() - 10).addToBattleEntity(this);
		}
		
		return trueDelta;
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
	
	public ArrayList<BattleAnimation> getAnimations()
	{
		return animations;
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
	
	public Vector2DDouble getFightPosition()
	{
		return fightPos;
	}
	
	public Entity getLevelEntity()
	{
		return levelEntity;
	}
	
	public Vector2DDouble getOriginPosition()
	{
		return originPos;
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
	
	public void removeBattleAnimation(BattleAnimation b)
	{
		animationsToRemove.add(b);
	}
	
	public void render(Texture renderTo, LightMap lightmap)
	{
		for (BattleAnimation b : animations)
		{
			b.render(renderTo, lightmap);
		}
	}
	
	public void renderPostLightmap(Texture renderTo)
	{
		for (BattleAnimation b : animations)
		{
			b.renderPostLightmap(renderTo);
		}
	}
	
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
	
	@Override
	public void tick()
	{
		for (BattleAnimation b : animations)
		{
			b.tick();
		}
		
		for (BattleAnimation b : animationsToRemove)
		{
			animations.remove(b);
		}
	}
}
