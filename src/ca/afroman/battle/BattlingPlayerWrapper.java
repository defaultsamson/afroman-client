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
import ca.afroman.inventory.Inventory;
import ca.afroman.light.FlickeringLight;
import ca.afroman.light.LightMap;
import ca.afroman.log.ALogType;
import ca.afroman.packet.battle.PacketExecuteSelectedBattleOptionClientServer;
import ca.afroman.packet.battle.PacketUpdateSelectedBattleOptionClientServer;
import ca.afroman.resource.Vector2DInt;

public class BattlingPlayerWrapper extends BattlingEntityWrapper
{
	private Font blackFont;
	private Font whiteFont;
	
	private FlickeringLight light;
	private Texture shadow;
	private DrawableAsset asset;
	private SpriteAnimation idleAsset;
	
	private Vector2DInt fightPos;
	private BattleOption option;
	
	private Inventory inv;
	private Role role;
	
	private int ticksUntilPass = -1;
	
	public BattlingPlayerWrapper(PlayerEntity fighting)
	{
		super(fighting);
		
		inv = fighting.getInventory();
		role = fighting.getRole();
		
		fightPos = role == Role.PLAYER1 ? new Vector2DInt(183, 54) : new Vector2DInt(194, 79);
		
		option = BattleOption.ATTACK;
		
		if (!isServerSide())
		{
			blackFont = Assets.getFont(AssetType.FONT_BLACK);
			whiteFont = Assets.getFont(AssetType.FONT_WHITE);
			
			asset = idleAsset = role == Role.PLAYER1 ? Assets.getSpriteAnimation(AssetType.BATTLE_PLAYER_ONE) : Assets.getSpriteAnimation(AssetType.BATTLE_PLAYER_TWO);
			shadow = Assets.getTexture(AssetType.BATTLE_SHADOW);
			light = new FlickeringLight(true, fightPos.toVector2DDouble(), 60, 70, 3);
		}
	}
	
	@Override
	public void executeBattle(int battleID)
	{
		ticksUntilPass = 100;
		
		if (isServerSide())
		{
			
		}
		else
		{
			
		}
	}
	
	@Override
	public PlayerEntity getFightingEnemy()
	{
		return (PlayerEntity) super.getFightingEnemy();
	}
	
	@Override
	public void render(Texture renderTo, LightMap lightmap)
	{
		shadow.render(renderTo, fightPos.getX() - 1, fightPos.getY() + 25);
		asset.render(renderTo, fightPos);// fightPos);
		light.renderCentered(lightmap);
		light.renderCentered(lightmap);
		
		if (isThisTurn())
		{
			blackFont.renderRight(renderTo, fightPos.getX() - 4, fightPos.getY() + 16, "< " + option.getDisplayName() + " >");
			whiteFont.renderRight(renderTo, fightPos.getX() - 5, fightPos.getY() + 15, "< " + option.getDisplayName() + " >");
		}
		
		if (ticksUntilPass > 0)
		{
			String shit;
			if (ticksUntilPass > 90)
			{
				shit = "Oh";
			}
			else if (ticksUntilPass > 70)
			{
				shit = "Fuck";
			}
			else if (ticksUntilPass > 50)
			{
				StringBuilder sb = new StringBuilder().append("H");
				
				for (int i = 0; i < 70 - ticksUntilPass; i++)
				{
					sb.append("o");
				}
				
				shit = sb.toString();
			}
			else if (ticksUntilPass > 30)
			{
				shit = "Hooooooooooooooooooooly shit";
			}
			else
			{
				shit = "Nothing happened...";
			}
			
			blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2 + 1, ClientGame.HEIGHT / 2 - 40 + 1, shit);
			whiteFont.renderCentered(renderTo, ClientGame.WIDTH / 2, ClientGame.HEIGHT / 2 - 40, shit);
		}
	}
	
	public void setBattleOption(BattleOption option)
	{
		this.option = option;
	}
	
	@Override
	public void tick()
	{
		if (ticksUntilPass > 0)
		{
			ticksUntilPass--;
		}
		else if (ticksUntilPass == 0)
		{
			ticksUntilPass--;
			
			if (isServerSide()) getFightingEnemy().getBattle().passTurn();
		}
		
		if (isServerSide())
		{
			// TODO other controls and shite
		}
		else
		{
			if (role == ClientGame.instance().getRole())
			{
				if (isThisTurn())
				{
					if (ClientGame.instance().input().nextItem.isPressedFiltered() || ClientGame.instance().input().right.isPressedFiltered())
					{
						option = option.getNext();
						ClientGame.instance().sockets().sender().sendPacket(new PacketUpdateSelectedBattleOptionClientServer(option));
					}
					else if (ClientGame.instance().input().prevItem.isPressedFiltered() || ClientGame.instance().input().left.isPressedFiltered())
					{
						option = option.getLast();
						ClientGame.instance().sockets().sender().sendPacket(new PacketUpdateSelectedBattleOptionClientServer(option));
					}
					else if (ClientGame.instance().input().useItem.isPressedFiltered() || ClientGame.instance().input().interact.isPressedFiltered() || ClientGame.instance().input().enter.isPressedFiltered())
					{
						ClientGame.instance().sockets().sender().sendPacket(new PacketExecuteSelectedBattleOptionClientServer(option));
						ClientGame.instance().logger().log(ALogType.DEBUG, "Executing BattleOption: " + option);
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
