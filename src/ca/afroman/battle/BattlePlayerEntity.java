package ca.afroman.battle;

import java.util.Random;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.battle.animation.BattleAnimationAdministerDamage;
import ca.afroman.battle.animation.BattleAnimationAttack;
import ca.afroman.battle.animation.BattleAnimationFlee;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.game.Role;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.FlickeringLight;
import ca.afroman.light.LightMap;
import ca.afroman.log.ALogType;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.packet.battle.PacketExecuteBattleIDClientServer;
import ca.afroman.packet.battle.PacketExecuteBattleIDServerClient;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.server.ServerGame;

public class BattlePlayerEntity extends BattleEntity
{
	// executeBattle offset for updating display of the option
	private static final int IS_SELECTING_ENEMY = 1;
	private static final int IS_NOT_SELECTING_ENEMY = 2;
	private static final int UPDATE_OPTION_OFFSET = 5000;
	private static final int EXECUTE_OPTION_OFFSET = 6000;
	
	// Client only
	private FlickeringLight light;
	private Texture shadow;
	private DrawableAsset asset;
	
	@SuppressWarnings("unused")
	private SpriteAnimation idleAsset;
	private BattleOption selectedOption;
	
	private boolean isBattling;
	
	// For fighting animation
	public BattlePlayerEntity(PlayerEntity levelEntity, BattlePosition pos)
	{
		super(levelEntity, pos);
		
		if (!isServerSide())
		{
			fightPos = new Vector2DDouble(pos.getReferenceX(), pos.getReferenceY() - 30);
			originPos = fightPos.clone();
			
			light = new FlickeringLight(true, fightPos.clone(), 60, 70, 5);
			shadow = Assets.getTexture(AssetType.BATTLE_SHADOW);
			asset = idleAsset = levelEntity.getRole() == Role.PLAYER1 ? Assets.getSpriteAnimation(AssetType.BATTLE_PLAYER_ONE) : Assets.getSpriteAnimation(AssetType.BATTLE_PLAYER_TWO);
			
			selectedOption = BattleOption.ATTACK;
			
			isBattling = false;
		}
	}
	
	@Override
	public int addHealth(int deltaHealth)
	{
		if (!isServerSide())
		{
			return super.addHealth(deltaHealth, asset.getWidth());
		}
		else
		{
			return super.addHealth(deltaHealth);
		}
	}
	
	@Override
	public void executeBattle(int battleID, int deltaHealth)
	{
		super.executeBattle(battleID, deltaHealth);
		
		boolean sendPacketToThisPlayer = false;
		
		if (battleID == IS_SELECTING_ENEMY)
		{
			getBattle().setIsSelectingAttack(true);
			getBattle().selectEnemyInit();
		}
		else if (battleID == IS_NOT_SELECTING_ENEMY)
		{
			getBattle().setIsSelectingAttack(false);
		}
		else if (battleID >= EXECUTE_OPTION_OFFSET)
		{
			int ord = battleID - EXECUTE_OPTION_OFFSET;
			
			selectedOption = BattleOption.fromOrdinal(ord);
			
			switch (selectedOption)
			{
				default:
					if (isServerSide()) finishTurn();
					break;
				case ATTACK:
					BattleEntity selected = getBattle().getEnemySelected();
					int travelTicks = 50;
					
					if (isServerSide()) deltaHealth = -8 - new Random().nextInt(4);
					
					new BattleAnimationAdministerDamage(isServerSide(), selected, travelTicks, deltaHealth).addToBattleEntity(this);
					new BattleAnimationAttack(isServerSide(), getBattlePosition(), selected.getBattlePosition(), travelTicks, 10, fightPos).addToBattleEntity(this);
					break;
				case FLEE:
					new BattleAnimationFlee(isServerSide(), fightPos, 30).addToBattleEntity(this);
					break;
			}
			
			if (isServerSide())
			{
				sendPacketToThisPlayer = true;
			}
			else
			{
				isBattling = true;
			}
		}
		else if (battleID >= UPDATE_OPTION_OFFSET)
		{
			int ord = battleID - UPDATE_OPTION_OFFSET;
			
			selectedOption = BattleOption.fromOrdinal(ord);
		}
		
		if (isServerSide())
		{
			if (sendPacketToThisPlayer)
			{
				if (deltaHealth != 0)
				{
					ServerGame.instance().sockets().sendPacketToAllClients(new PacketExecuteBattleIDServerClient(getBattle().getID(), battleID, deltaHealth));
				}
				else
				{
					ServerGame.instance().sockets().sendPacketToAllClients(new PacketExecuteBattleIDServerClient(getBattle().getID(), battleID));
				}
			}
			else
			{
				if (deltaHealth != 0)
				{
					ServerGame.instance().sockets().sendPacketToAllClients(new PacketExecuteBattleIDServerClient(getBattle().getID(), battleID, deltaHealth), ((IPConnectedPlayer) ServerGame.instance().sockets().getPlayerConnection(getLevelEntity().getRole())).getConnection());
				}
				else
				{
					ServerGame.instance().sockets().sendPacketToAllClients(new PacketExecuteBattleIDServerClient(getBattle().getID(), battleID), ((IPConnectedPlayer) ServerGame.instance().sockets().getPlayerConnection(getLevelEntity().getRole())).getConnection());
				}
			}
		}
	}
	
	@Override
	public PlayerEntity getLevelEntity()
	{
		return (PlayerEntity) super.getLevelEntity();
	}
	
	@Override
	public void render(Texture renderTo, LightMap lightmap)
	{
		shadow.render(renderTo, (int) fightPos.getX() - 1, (int) fightPos.getY() + 25);
		asset.render(renderTo, (int) fightPos.getX(), (int) fightPos.getY());// fightPos);
		light.renderCentered(lightmap);
		
		super.render(renderTo, lightmap);
	}
	
	@Override
	public void renderPostLightmap(Texture renderTo)
	{
		blackFont.renderCentered(renderTo, (int) fightPos.getX() + 11, (int) fightPos.getY() + 41, "" + health);
		whiteFont.renderCentered(renderTo, (int) fightPos.getX() + 10, (int) fightPos.getY() + 40, "" + health);
		
		if (isThisTurn() && !isBattling)
		{
			boolean isSelectingAttack = getBattle().isSelectingAttack();
			if (!isSelectingAttack)
			{
				String attackDisplay = "< " + selectedOption.getDisplayName() + " >";
				blackFont.renderRight(renderTo, (int) fightPos.getX() - 4, (int) fightPos.getY() + 16, attackDisplay);
				whiteFont.renderRight(renderTo, (int) fightPos.getX() - 5, (int) fightPos.getY() + 15, attackDisplay);
			}
			
			String headText;
			if (getLevelEntity().getRole() == ClientGame.instance().getRole())
			{
				if (isSelectingAttack)
				{
					headText = "Select enemy to attack";
				}
				else
				{
					headText = "Your turn";
				}
			}
			else
			{
				if (isSelectingAttack)
				{
					headText = "Other player is selecting enemy to attack";
				}
				else
				{
					headText = "Other player's turn";
				}
			}
			
			blackFont.renderCentered(renderTo, (ClientGame.WIDTH / 2) + 1, 11, headText);
			whiteFont.renderCentered(renderTo, (ClientGame.WIDTH / 2), 10, headText);
		}
		
		super.renderPostLightmap(renderTo);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (isServerSide())
		{
			// TODO other controls and shite
		}
		else
		{
			if (!isThisTurn())
			{
				isBattling = false;
			}
			
			if (getLevelEntity().getRole() == ClientGame.instance().getRole())
			{
				if (isThisTurn() && getBattle() != null)
				{
					if (getBattle().isSelectingAttack())
					{
						if (ClientGame.instance().input().nextItem.isPressedFiltered() || ClientGame.instance().input().right.isPressedFiltered() || ClientGame.instance().input().up.isPressedFiltered())
						{
							getBattle().selectEnemyNext();
						}
						else if (ClientGame.instance().input().prevItem.isPressedFiltered() || ClientGame.instance().input().left.isPressedFiltered() || ClientGame.instance().input().down.isPressedFiltered())
						{
							getBattle().selectEnemyPrev();
						}
						else if (ClientGame.instance().input().dropItem.isPressedFiltered() || ClientGame.instance().input().escape.isPressedFiltered())
						{
							// Clears the release on ESCAPE so that it doesn't trigger the in-game menu
							ClientGame.instance().input().escape.setPressed(false);
							ClientGame.instance().input().escape.isReleasedFiltered();
							
							ClientGame.instance().sockets().sendPacket(new PacketExecuteBattleIDClientServer(IS_NOT_SELECTING_ENEMY));
							getBattle().setIsSelectingAttack(false);
						}
						else if (ClientGame.instance().input().useItem.isPressedFiltered() || ClientGame.instance().input().interact.isPressedFiltered() || ClientGame.instance().input().enter.isPressedFiltered())
						{
							ClientGame.instance().sockets().sendPacket(new PacketExecuteBattleIDClientServer(EXECUTE_OPTION_OFFSET + selectedOption.ordinal()));
							ClientGame.instance().logger().log(ALogType.DEBUG, "Executing BattleOption: " + selectedOption);
						}
					}
					else if (!isBattling)
					{
						// Idle Controls
						if (ClientGame.instance().input().nextItem.isPressedFiltered() || ClientGame.instance().input().right.isPressedFiltered())
						{
							selectedOption = selectedOption.getNext();
							ClientGame.instance().sockets().sendPacket(new PacketExecuteBattleIDClientServer(UPDATE_OPTION_OFFSET + selectedOption.ordinal()));
						}
						else if (ClientGame.instance().input().prevItem.isPressedFiltered() || ClientGame.instance().input().left.isPressedFiltered())
						{
							selectedOption = selectedOption.getLast();
							ClientGame.instance().sockets().sendPacket(new PacketExecuteBattleIDClientServer(UPDATE_OPTION_OFFSET + selectedOption.ordinal()));
						}
						else if (ClientGame.instance().input().useItem.isPressedFiltered() || ClientGame.instance().input().interact.isPressedFiltered() || ClientGame.instance().input().enter.isPressedFiltered())
						{
							switch (selectedOption)
							{
								case ATTACK:
									ClientGame.instance().sockets().sendPacket(new PacketExecuteBattleIDClientServer(IS_SELECTING_ENEMY));
									getBattle().setIsSelectingAttack(true);
									getBattle().selectEnemyInit();
									break;
								default:
									ClientGame.instance().sockets().sendPacket(new PacketExecuteBattleIDClientServer(EXECUTE_OPTION_OFFSET + selectedOption.ordinal()));
									ClientGame.instance().logger().log(ALogType.DEBUG, "Executing BattleOption: " + selectedOption);
									isBattling = true;
									break;
							}
						}
					}
				}
				
				// TODO controls and shite
				// Jump
				if (ClientGame.instance().input().up.isPressed())
				{
					
				}
				// Duck
				if (ClientGame.instance().input().down.isPressed())
				{
					
				}
			}
			
			if (asset instanceof ITickable)
			{
				// Ticks the IBattleables DrawableAsset
				((ITickable) asset).tick();
			}
			
			light.setPosition(fightPos.getX() + 5, fightPos.getY() + 3);
			light.tick();
		}
	}
}
