package ca.afroman.battle;

import java.awt.Color;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.game.Game;
import ca.afroman.game.Role;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.LightMap;
import ca.afroman.log.ALogType;
import ca.afroman.packet.battle.PacketUpdateTurn;
import ca.afroman.resource.IDCounter;
import ca.afroman.resource.ServerClientObject;
import ca.afroman.server.ServerGame;

public class BattleScene extends ServerClientObject implements ITickable
{
	private static IDCounter serverIdCounter = null;
	private static IDCounter clientIdCounter = null;
	
	private static final Texture bg = Assets.getTexture(AssetType.BATTLE_RUINS_BG);
	
	/**
	 * The ID counter that keeps track of Entity ID's.
	 * 
	 * @param isServerSide whether this is being counted from the server or the client
	 * @return the ID counter for the specified game instance.
	 */
	private static IDCounter getIDCounter(boolean isServerSide)
	{
		if (isServerSide)
		{
			if (serverIdCounter == null) serverIdCounter = new IDCounter();
			return serverIdCounter;
		}
		else
		{
			if (clientIdCounter == null) clientIdCounter = new IDCounter();
			return clientIdCounter;
		}
	}
	
	private int id;
	private BattlingEntityWrapper entity;
	private BattlingPlayerWrapper player1;
	private BattlingPlayerWrapper player2;
	
	private LightMap lightmap;
	
	public BattleScene(boolean isServerSide)
	{
		super(isServerSide);
		
		id = getIDCounter(isServerSide).getNext();
		
		if (isServerSide())
		{
			
		}
		else
		{
			ClientGame.instance().playMusic(Assets.getAudioClip(AssetType.AUDIO_BATTLE_MUSIC), true);
			lightmap = new LightMap(ClientGame.WIDTH, ClientGame.HEIGHT, new Color(0F, 0F, 0F, 0.5F));
		}
	}
	
	public boolean addEntityBattleWrapper(BattlingEntityWrapper w)
	{
		if (w instanceof BattlingPlayerWrapper)
		{
			BattlingPlayerWrapper p = (BattlingPlayerWrapper) w;
			
			switch (p.getFightingEnemy().getRole())
			{
				default:
					Game.instance(isServerSide()).logger().log(ALogType.CRITICAL, "A BattlingPlayerWrapper has a PlayerEntity attatched to it that is not the role of player1 or player2");
					return false;
				case PLAYER1:
					if (player1 == null)
					{
						player1 = p;
						return true;
					}
					break;
				case PLAYER2:
					if (player2 == null)
					{
						player2 = p;
						return true;
					}
					break;
			}
		}
		else
		{
			if (entity == null)
			{
				entity = w;
				return true;
			}
		}
		
		return false;
	}
	
	public BattlingEntityWrapper getEntityWhosTurnItIs()
	{
		return player1 != null && player1.isThisTurn() ? player1 : player2 != null && player2.isThisTurn() ? player2 : entity;
	}
	
	public int getID()
	{
		return id;
	}
	
	public BattlingPlayerWrapper getPlayer(Role role)
	{
		switch (role)
		{
			default:
				return null;
			case PLAYER1:
				return player1;
			case PLAYER2:
				return player2;
		}
	}
	
	@Deprecated
	public BattlingPlayerWrapper getPlayerWhosTurnItIs()
	{
		return player1 != null && player1.isThisTurn() ? player1 : player2 != null && player2.isThisTurn() ? player2 : null;
	}
	
	public void passTurn()
	{
		if (player1 != null && player1.isThisTurn())
		{
			setWhosTurnItIs(Role.PLAYER2);
		}
		else if (player2 != null && player2.isThisTurn())
		{
			setWhosTurnItIs(Role.SPECTATOR);
		}
		else if (entity.isThisTurn())
		{
			setWhosTurnItIs(Role.PLAYER1);
		}
	}
	
	public void render(Texture renderTo)
	{
		bg.render(renderTo, 0, 0);
		
		lightmap.clear();
		
		entity.render(renderTo, lightmap);
		if (player1 != null) player1.render(renderTo, lightmap);
		if (player2 != null) player2.render(renderTo, lightmap);
		
		lightmap.patch();
		lightmap.render(renderTo, 0, 0);
	}
	
	public void setWhosTurnItIs(Role role)
	{
		if (isServerSide())
		{
			ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketUpdateTurn(getID(), role));
		}
		
		if (player1 != null) player1.setIsThisTurn(false);
		if (player2 != null) player2.setIsThisTurn(false);
		entity.setIsThisTurn(false);
		
		switch (role)
		{
			default:
				entity.setIsThisTurn(true);
				break;
			case PLAYER1:
				if (player1 != null)
				{
					player1.setIsThisTurn(true);
				}
				else
				{
					Game.instance(isServerSide()).logger().log(ALogType.CRITICAL, "BattleScene is trying to set the turn to player 1, but player1 is null");
				}
				break;
			case PLAYER2:
				if (player2 != null)
				{
					player2.setIsThisTurn(true);
				}
				else
				{
					Game.instance(isServerSide()).logger().log(ALogType.CRITICAL, "BattleScene is trying to set the turn to player 2, but player2 is null");
				}
				break;
		}
	}
	
	@Override
	public void tick()
	{
		entity.tick();
		if (player1 != null) player1.tick();
		if (player2 != null) player2.tick();
	}
}
