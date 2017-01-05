package ca.afroman.battle;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.Crab;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.FlickeringLight;
import ca.afroman.light.LightMap;
import ca.afroman.packet.battle.PacketExecuteBattleIDServerClient;
import ca.afroman.resource.Vector2DInt;
import ca.afroman.server.ServerGame;

public class BattlingCrabWrapper extends BattlingEntityWrapper
{
	private FlickeringLight light;
	private DrawableAsset asset;
	private SpriteAnimation idleAsset;
	private Vector2DInt fightPos;
	
	private int ticksUntilPass = -1;
	
	private boolean sentIt = false;
	
	public BattlingCrabWrapper(Crab fighting)
	{
		super(fighting);
		this.fightPos = new Vector2DInt(40, 81);
		
		asset = idleAsset = Assets.getSpriteAnimation(AssetType.CRAB_RIGHT).clone();
		idleAsset.getTickCounter().setInterval(15);
		light = new FlickeringLight(true, fightPos.toVector2DDouble(), 55, 45, 4);
	}
	
	@Override
	public void executeBattle(int battleID)
	{
		System.out.println("Crab Execution: " + battleID);
		ticksUntilPass = 60;
		
		if (isServerSide())
		{
			
		}
		else
		{
			
		}
	}
	@Override
	public void render(Texture renderTo, LightMap map)
	{
		asset.render(renderTo, fightPos); // fightPos);
		light.renderCentered(map);
		light.renderCentered(map);
		
		if (ticksUntilPass > 10)
		{
			Assets.getFont(AssetType.FONT_BLACK).renderCentered(renderTo, ClientGame.WIDTH / 2 + 1, ClientGame.HEIGHT / 2 + 1, "Uhh... Useless Mr Crabs");
			Assets.getFont(AssetType.FONT_WHITE).renderCentered(renderTo, ClientGame.WIDTH / 2, ClientGame.HEIGHT / 2, "Uhh... Useless Mr Crabs");
		}
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
			if (isThisTurn())
			{
				if (!sentIt)
				{
					sentIt = true;
					ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketExecuteBattleIDServerClient(getFightingEnemy().getBattle().getID(), 2));
					
					executeBattle(2);
				}
			}
			else
			{
				
				sentIt = false;
			}
		}
		
		if (asset instanceof ITickable)
		{
			// Ticks the IBattleables DrawableAsset
			((ITickable) asset).tick();
		}
		
		light.setPosition(fightPos.getX() + 8, fightPos.getY());
		light.tick();
	}
}
