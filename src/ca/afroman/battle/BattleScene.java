package ca.afroman.battle;

import java.awt.Color;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.game.Game;
import ca.afroman.game.Role;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.LightMap;
import ca.afroman.log.ALogType;
import ca.afroman.network.IPConnection;
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
	private LightMap lightmap;
	/** Left bottom **/
	private BattleEntity enemy1;
	/** Left middle **/
	private BattleEntity enemy2;
	/** Left top **/
	private BattleEntity enemy3;
	/** Right top **/
	private BattlePlayerEntity player1;
	/** Right bottom **/
	private BattlePlayerEntity player2;
	private BattleEntity[] fighters = { enemy1, enemy2, enemy3, player1, player2 };
	
	private boolean isSelectingAttack = false;
	
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
	
	/**
	 * Adds a player to a battle.
	 * 
	 * @param player
	 */
	public void addEntityToBattle(Entity entity)
	{
		if (entity instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity) entity;
			
			BattlePlayerEntity battleEntity = (BattlePlayerEntity) entity.getBattleEntity1();
			
			if (player.getRole() == Role.PLAYER1)
			{
				if (player1 == null)
				{
					player1 = battleEntity;
					updateFightersArray();
				}
				else
				{
					Game.instance(isServerSide()).logger().log(ALogType.WARNING, "Player 1 is already in this BattleScene: " + getID());
				}
			}
			else if (player.getRole() == Role.PLAYER2)
			{
				if (player2 == null)
				{
					player2 = battleEntity;
					updateFightersArray();
				}
				else
				{
					Game.instance(isServerSide()).logger().log(ALogType.WARNING, "Player 2 is already in this BattleScene: " + getID());
				}
			}
			else
			{
				Game.instance(isServerSide()).logger().log(ALogType.WARNING, "BattlePlayerEntity with role " + player.getRole() + "is trying to be added to BattleScene: " + getID());
			}
		}
		else
		{
			if (enemy1 == null && enemy2 == null && enemy3 == null)
			{
				enemy1 = entity.getBattleEntity1();
				enemy2 = entity.getBattleEntity2();
				enemy3 = entity.getBattleEntity3();
				
				updateFightersArray();
			}
			else
			{
				Game.instance(isServerSide()).logger().log(ALogType.WARNING, "BattleScene already has an enemy in it: " + getID());
			}
		}
	}
	
	public void executeBattle(int battleID, int deltaHealth)
	{
		for (BattleEntity e : fighters)
		{
			if (e != null && e.isThisTurn())
			{
				e.executeBattle(battleID, deltaHealth);
				break;
			}
		}
	}
	
	public BattleEntity getBottomEnemy()
	{
		return enemy1;
	}
	
	public BattleEntity getEnemySelected()
	{
		return enemy1.isThisSelected() ? enemy1 : enemy2.isThisSelected() ? enemy2 : enemy3.isThisSelected() ? enemy3 : player1.isThisSelected() ? player1 : player2.isThisSelected() ? player2 : null;
	}
	
	public int getID()
	{
		return id;
	}
	
	public BattleEntity getMiddleEnemy()
	{
		return enemy2;
	}
	
	public BattlePlayerEntity getPlayer(Role role)
	{
		return role == Role.PLAYER1 ? player1 : role == Role.PLAYER2 ? player2 : null;
	}
	
	public BattlePlayerEntity getPlayer1()
	{
		return player1;
	}
	
	public BattlePlayerEntity getPlayer2()
	{
		return player2;
	}
	
	public BattleEntity getTopEnemy()
	{
		return enemy3;
	}
	
	public boolean isSelectingAttack()
	{
		return isSelectingAttack;
	}
	
	public void progressTurn()
	{
		int lastIsTurnIndex = -1;
		for (int i = 0; i < fighters.length; i++)
		{
			if (fighters[i] != null && fighters[i].isThisTurn())
			{
				lastIsTurnIndex = i;
				break;
			}
		}
		
		// Couldn't find the fighter who's turn it is
		if (lastIsTurnIndex == -1)
		{
			Game.instance(isServerSide()).logger().log(ALogType.CRITICAL, "Couldn't find the index of the entity who's turn it is while progressing turns");
		}
		else
		{
			// Go through all the fighters past lastIsTurnIndex
			for (int i = 1; i < fighters.length; i++)
			{
				// Normalize the checkingID so that it is past lastIsTurnIndex and loops back around the array
				int checkingID = lastIsTurnIndex + i;
				if (checkingID >= fighters.length) checkingID -= fighters.length;
				
				// If the fighter isn't null, then set it to be their turn now
				if (fighters[checkingID] != null)
				{
					setTurn(checkingID);
					break;
				}
			}
		}
	}
	
	public void removeEntityFromBattle(Entity entity)
	{
		if (entity instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity) entity;
			
			if (player.getRole() == Role.PLAYER1)
			{
				if (player1 != null)
				{
					player1 = null;
					updateFightersArray();
				}
				else
				{
					Game.instance(isServerSide()).logger().log(ALogType.WARNING, "Player 1 has already left from BattleScene: " + getID());
				}
			}
			else if (player.getRole() == Role.PLAYER2)
			{
				if (player2 != null)
				{
					player2 = null;
					updateFightersArray();
				}
				else
				{
					Game.instance(isServerSide()).logger().log(ALogType.WARNING, "Player 2 has already left from BattleScene: " + getID());
				}
			}
			else
			{
				Game.instance(isServerSide()).logger().log(ALogType.WARNING, "BattlePlayerEntity with role " + player.getRole() + "is trying to be removed from BattleScene: " + getID());
			}
		}
		else
		{
			Game.instance(isServerSide()).logger().log(ALogType.WARNING, "non-player Entities may not leave battles: " + getID());
		}
	}
	
	public void render(Texture renderTo)
	{
		bg.render(renderTo, 0, 0);
		
		lightmap.clear();
		
		for (BattleEntity e : fighters)
			if (e != null) e.render(renderTo, lightmap);
		
		lightmap.patch();
		lightmap.render(renderTo, 0, 0);
		
		for (BattleEntity e : fighters)
			if (e != null) e.renderPostLightmap(renderTo);
	}
	
	/**
	 * Only to be used by client.
	 */
	public void selectEnemyInit()
	{
		for (BattleEntity e : fighters)
			if (e != null) e.setIsSelected(false);
		
		if (enemy2 != null) enemy2.setIsSelected(true);
		else if (enemy1 != null) enemy1.setIsSelected(true);
		else if (enemy3 != null) enemy3.setIsSelected(true);
		else
			Game.instance(isServerSide()).logger().log(ALogType.CRITICAL, "BattleScene " + getID() + " has no enemies to select");
	}
	//
	// public boolean clearSelectedEnemy()
	// {
	//
	// }
	
	/**
	 * Only to be used by client.
	 */
	public void selectEnemyNext()
	{
		if (enemy1 != null && enemy1.isThisSelected())
		{
			if (enemy2 != null)
			{
				enemy1.setIsSelected(false);
				enemy2.setIsSelected(true);
			}
			else if (enemy3 != null)
			{
				enemy1.setIsSelected(false);
				enemy3.setIsSelected(true);
			}
		}
		else if (enemy2 != null && enemy2.isThisSelected())
		{
			if (enemy3 != null)
			{
				enemy2.setIsSelected(false);
				enemy3.setIsSelected(true);
			}
			else if (enemy1 != null)
			{
				enemy2.setIsSelected(false);
				enemy1.setIsSelected(true);
			}
		}
		else if (enemy3 != null && enemy3.isThisSelected())
		{
			if (enemy1 != null)
			{
				enemy3.setIsSelected(false);
				enemy1.setIsSelected(true);
			}
			else if (enemy2 != null)
			{
				enemy3.setIsSelected(false);
				enemy2.setIsSelected(true);
			}
		}
		else
		{
			Game.instance(isServerSide()).logger().log(ALogType.CRITICAL, "BattleScene " + getID() + " has no enemies to select next");
		}
	}
	
	/**
	 * Only to be used by client.
	 */
	public void selectEnemyPrev()
	{
		if (enemy1 != null && enemy1.isThisSelected())
		{
			if (enemy3 != null)
			{
				enemy1.setIsSelected(false);
				enemy3.setIsSelected(true);
			}
			else if (enemy2 != null)
			{
				enemy1.setIsSelected(false);
				enemy2.setIsSelected(true);
			}
		}
		else if (enemy2 != null && enemy2.isThisSelected())
		{
			if (enemy1 != null)
			{
				enemy2.setIsSelected(false);
				enemy1.setIsSelected(true);
			}
			else if (enemy3 != null)
			{
				enemy2.setIsSelected(false);
				enemy3.setIsSelected(true);
			}
		}
		else if (enemy3 != null && enemy3.isThisSelected())
		{
			if (enemy2 != null)
			{
				enemy3.setIsSelected(false);
				enemy2.setIsSelected(true);
			}
			else if (enemy1 != null)
			{
				enemy3.setIsSelected(false);
				enemy1.setIsSelected(true);
			}
		}
		else
		{
			ClientGame.instance().logger().log(ALogType.CRITICAL, "BattleScene " + getID() + " has no enemies to select next");
		}
	}
	
	/**
	 * To be used by the server only.
	 * 
	 * @param pos
	 * @param sender
	 */
	public void setEnemySelected(BattlePosition pos, IPConnection sender)
	{
		enemy1.setIsSelected(pos == BattlePosition.LEFT_BOTTOM, sender);
		enemy2.setIsSelected(pos == BattlePosition.LEFT_MIDDLE, sender);
		enemy3.setIsSelected(pos == BattlePosition.LEFT_TOP, sender);
		player1.setIsSelected(pos == BattlePosition.RIGHT_TOP, sender);
		player2.setIsSelected(pos == BattlePosition.RIGHT_BOTTOM, sender);
	}
	
	/**
	 * Tells if the player is selecting an enemy to attack.
	 * 
	 * @param isSelecting
	 */
	public void setIsSelectingAttack(boolean isSelecting)
	{
		isSelectingAttack = isSelecting;
	}
	
	public void setTurn(int fighterOrd)
	{
		if (fighterOrd < 0 || fighterOrd >= fighters.length)
		{
			Game.instance(isServerSide()).logger().log(ALogType.WARNING, "fighterOrd is not within range");
			return;
		}
		
		if (isServerSide()) ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketUpdateTurn(getID(), fighterOrd));
		
		for (int i = 0; i < fighters.length; i++)
			fighters[i].setIsTurn(i == fighterOrd);
	}
	
	public void setTurn(Role role)
	{
		if (isServerSide()) ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketUpdateTurn(getID(), role));
		
		if (enemy1 != null) enemy1.setIsTurn(role != Role.PLAYER1 && role != Role.PLAYER2);
		if (enemy2 != null) enemy2.setIsTurn(false);
		if (enemy3 != null) enemy3.setIsTurn(false);
		if (player1 != null) player1.setIsTurn(role == Role.PLAYER1);
		if (player2 != null) player2.setIsTurn(role == Role.PLAYER2);
	}
	
	@Override
	public void tick()
	{
		for (BattleEntity e : fighters)
			if (e != null) e.tick();
	}
	
	private void updateFightersArray()
	{
		fighters[0] = enemy1;
		fighters[1] = enemy2;
		fighters[2] = enemy3;
		fighters[3] = player1;
		fighters[4] = player2;
	}
}
