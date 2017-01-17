package ca.afroman.battle;

import java.util.Random;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.api.Entity;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.FlickeringLight;
import ca.afroman.light.LightMap;
import ca.afroman.packet.battle.PacketExecuteBattleIDServerClient;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.server.ServerGame;

public class BattleCrabEntity extends BattleEntityAutomated
{
	// Server/client
	public int health = 20;
	
	private int ticksUntilPass = -1;
	
	// Client only
	private Vector2DDouble fightPos;
	private Vector2DDouble originPos;
	
	private FlickeringLight light;
	private Texture shadow;
	private DrawableAsset asset;
	private SpriteAnimation idleAsset;
	
	public BattleCrabEntity(Entity levelEntity, int pos)
	{
		super(levelEntity);
		
		if (!isServerSide())
		{
			fightPos = pos == 1 ? new Vector2DDouble(39, 98) : pos == 2 ? new Vector2DDouble(49, 81) : new Vector2DDouble(59, 67);
			originPos = fightPos.clone();
			
			light = new FlickeringLight(true, fightPos.clone(), 55, 49, 3 + pos);
			shadow = Assets.getTexture(AssetType.BATTLE_SHADOW);
			asset = idleAsset = Assets.getSpriteAnimation(AssetType.CRAB_RIGHT).clone();
			idleAsset.getTickCounter().setInterval(14 + pos);
		}
	}
	
	@Override
	protected void aiAttack(BattlePlayerEntity pl1, BattlePlayerEntity pl2)
	{
		// TODO simplify expression
		if (pl1 == null)
		{
			attackPlayer(2);
		}
		else if (pl2 == null)
		{
			attackPlayer(1);
		}
		else
		{
			if (new Random().nextBoolean())
			{
				attackPlayer(2);
			}
			else
			{
				attackPlayer(1);
			}
		}
	}
	
	private void attackPlayer(int player)
	{
		System.out.println("Attacking player " + player);
		ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketExecuteBattleIDServerClient(getLevelEntity().getBattle().getID(), player));
		executeBattle(player);
	}
	
	@Override
	public void executeBattle(int battleID)
	{
		ticksUntilPass = 60;
		
		if (isServerSide())
		{
			System.out.println("Crab Execution: " + battleID);
		}
		else
		{
			
		}
	}
	
	@Override
	public boolean isAlive()
	{
		return health > 0;
	}
	
	@Override
	public void render(Texture renderTo, LightMap lightmap)
	{
		shadow.render(renderTo, (int) fightPos.getX() - 1, (int) fightPos.getY() + 8);
		asset.render(renderTo, (int) fightPos.getX(), (int) fightPos.getY()); // fightPos);
		light.renderCentered(lightmap);
		light.renderCentered(lightmap);
	}
	
	@Override
	public void renderPostLightmap(Texture renderTo)
	{
		if (ticksUntilPass > 10)
		{
			Assets.getFont(AssetType.FONT_BLACK).renderCentered(renderTo, ClientGame.WIDTH / 2 + 1, ClientGame.HEIGHT / 2 + 1, "Uhh... Useless Mr Crabs");
			Assets.getFont(AssetType.FONT_WHITE).renderCentered(renderTo, ClientGame.WIDTH / 2, ClientGame.HEIGHT / 2, "Uhh... Useless Mr Crabs");
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (ticksUntilPass > 0)
		{
			ticksUntilPass--;
		}
		else if (ticksUntilPass == 0)
		{
			ticksUntilPass--;
			
			if (isServerSide()) finishTurn();
		}
		
		if (isServerSide())
		{
			
		}
		else
		{
			if (asset instanceof ITickable)
			{
				// Ticks the IBattleables DrawableAsset
				((ITickable) asset).tick();
			}
			
			light.setPosition(fightPos.getX() + 8, fightPos.getY());
			light.tick();
		}
	}
}
