package ca.afroman.battle;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.Font;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
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
	private static final int UPDATE_OPTION_OFFSET = 5000;
	private static final int EXECUTE_OPTION_OFFSET = 6000;
	
	// Server and client
	private int health = 100;
	private int ticksUntilPass = -1;
	
	// Client only
	private Font blackFont;
	private Font whiteFont;
	
	private Vector2DDouble fightPos;
	private Vector2DDouble originPos;
	
	private FlickeringLight light;
	private Texture shadow;
	private DrawableAsset asset;
	private SpriteAnimation idleAsset;
	
	private BattleOption selectedOption;
	private boolean isBattling = false;
	
	// For fighting animation
	private double xInterpolation;
	private double yInterpolation;
	
	public BattlePlayerEntity(PlayerEntity levelEntity)
	{
		super(levelEntity);
		
		if (!isServerSide())
		{
			blackFont = Assets.getFont(AssetType.FONT_BLACK);
			whiteFont = Assets.getFont(AssetType.FONT_WHITE);
			
			fightPos = levelEntity.getRole() == Role.PLAYER1 ? new Vector2DDouble(173, 54) : new Vector2DDouble(184, 79);
			originPos = fightPos.clone();
			
			light = new FlickeringLight(true, fightPos.clone(), 60, 70, 5);
			shadow = Assets.getTexture(AssetType.BATTLE_SHADOW);
			asset = idleAsset = levelEntity.getRole() == Role.PLAYER1 ? Assets.getSpriteAnimation(AssetType.BATTLE_PLAYER_ONE) : Assets.getSpriteAnimation(AssetType.BATTLE_PLAYER_TWO);
			
			selectedOption = BattleOption.ATTACK;
		}
	}
	
	@Override
	public void executeBattle(int battleID)
	{
		if (battleID >= EXECUTE_OPTION_OFFSET)
		{
			int ord = battleID - EXECUTE_OPTION_OFFSET;
			
			selectedOption = BattleOption.fromOrdinal(ord);
			
			switch (selectedOption)
			{
				default:
					ticksUntilPass = 100;
					break;
				case ATTACK:
					ticksUntilPass = 120;
					if (!isServerSide())
					{
						xInterpolation = (fightPos.getX() - 50D) / 50D;
						yInterpolation = 5D / 50D;
					}
					break;
			}
			
			if (isServerSide())
			{
				ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketExecuteBattleIDServerClient(getLevelEntity().getBattle().getID(), battleID));
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
			
			if (isServerSide()) ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketExecuteBattleIDServerClient(getLevelEntity().getBattle().getID(), battleID), ((IPConnectedPlayer) ServerGame.instance().sockets().getPlayerConnection(getLevelEntity().getRole())).getConnection());
		}
	}
	
	@Override
	public PlayerEntity getLevelEntity()
	{
		return (PlayerEntity) super.getLevelEntity();
	}
	
	@Override
	public boolean isAlive()
	{
		return health > 0;
	}
	
	@Override
	public void render(Texture renderTo, LightMap lightmap)
	{
		shadow.render(renderTo, (int) fightPos.getX() - 1, (int) fightPos.getY() + 25);
		asset.render(renderTo, (int) fightPos.getX(), (int) fightPos.getY());// fightPos);
		light.renderCentered(lightmap);
		light.renderCentered(lightmap);
	}
	
	@Override
	public void renderPostLightmap(Texture renderTo)
	{
		if (isThisTurn() && !isBattling)
		{
			blackFont.renderRight(renderTo, (int) fightPos.getX() - 4, (int) fightPos.getY() + 16, "< " + selectedOption.getDisplayName() + " >");
			whiteFont.renderRight(renderTo, (int) fightPos.getX() - 5, (int) fightPos.getY() + 15, "< " + selectedOption.getDisplayName() + " >");
		}
	}
	
	@Override
	public void tick()
	{
		if (ticksUntilPass > 0)
		{
			ticksUntilPass--;
			
			if (!isServerSide())
			{
				switch (selectedOption)
				{
					default:
						break;
					case ATTACK:
						if (ticksUntilPass > 70)
						{
							fightPos.add(-xInterpolation, -yInterpolation);
						}
						else if (ticksUntilPass == 0)
						{
							fightPos.setVector(originPos);
						}
						else if (ticksUntilPass < 50)
						{
							fightPos.add(xInterpolation, yInterpolation);
						}
						break;
				}
			}
		}
		else if (ticksUntilPass == 0)
		{
			ticksUntilPass--;
			if (isServerSide()) finishTurn();
		}
		
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
				if (isThisTurn())
				{
					if (!isBattling)
					{
						// Idle Controls
						if (ClientGame.instance().input().nextItem.isPressedFiltered() || ClientGame.instance().input().right.isPressedFiltered())
						{
							selectedOption = selectedOption.getNext();
							ClientGame.instance().sockets().sender().sendPacket(new PacketExecuteBattleIDClientServer(UPDATE_OPTION_OFFSET + selectedOption.ordinal()));
						}
						else if (ClientGame.instance().input().prevItem.isPressedFiltered() || ClientGame.instance().input().left.isPressedFiltered())
						{
							selectedOption = selectedOption.getLast();
							ClientGame.instance().sockets().sender().sendPacket(new PacketExecuteBattleIDClientServer(UPDATE_OPTION_OFFSET + selectedOption.ordinal()));
						}
						else if (ClientGame.instance().input().useItem.isPressedFiltered() || ClientGame.instance().input().interact.isPressedFiltered() || ClientGame.instance().input().enter.isPressedFiltered())
						{
							ClientGame.instance().sockets().sender().sendPacket(new PacketExecuteBattleIDClientServer(EXECUTE_OPTION_OFFSET + selectedOption.ordinal()));
							ClientGame.instance().logger().log(ALogType.DEBUG, "Executing BattleOption: " + selectedOption);
							isBattling = true;
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
