package ca.afroman.battle;

import java.util.Random;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.api.Entity;
import ca.afroman.game.Game;
import ca.afroman.game.Role;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.FlickeringLight;
import ca.afroman.light.LightMap;
import ca.afroman.log.ALogType;
import ca.afroman.packet.battle.PacketExecuteBattleIDServerClient;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.server.ServerGame;

public class BattleCrabEntity extends BattleEntityAutomated
{
	// Server/client
	private int ticksUntilPass = -1;
	
	// Client only
	private Vector2DDouble fightPos;
	private Vector2DDouble originPos;
	
	private FlickeringLight light;
	private Texture shadow;
	private DrawableAsset asset;
	private SpriteAnimation idleAsset;
	private SpriteAnimation arrow;
	
	// For fighting animation
	private double xInterpolation;
	private double yInterpolation;
	
	private BattlePlayerEntity playerAttacking;
	private int deltaDamage;
	
	public BattleCrabEntity(Entity levelEntity, BattlePosition pos)
	{
		super(levelEntity, pos);
		
		if (!isServerSide())
		{
			fightPos = new Vector2DDouble(pos.getReferenceX() - 12, pos.getReferenceY() - 15);
			originPos = fightPos.clone();
			
			light = new FlickeringLight(true, fightPos.clone(), 55, 49, 3 + pos.ordinal());
			shadow = Assets.getTexture(AssetType.BATTLE_SHADOW);
			asset = idleAsset = Assets.getSpriteAnimation(AssetType.CRAB_RIGHT).clone();
			idleAsset.getTickCounter().setInterval(14 + pos.ordinal());
			arrow = Assets.getSpriteAnimation(AssetType.BATTLE_ARROW);
		}
	}
	
	@Override
	protected void aiAttack(BattlePlayerEntity pl1, BattlePlayerEntity pl2)
	{
		// TODO simplify expression
		if (pl1 == null || !pl1.isAlive())
		{
			attackPlayer(Role.PLAYER2);
		}
		else if (pl2 == null || !pl2.isAlive())
		{
			attackPlayer(Role.PLAYER1);
		}
		else
		{
			if (new Random().nextBoolean())
			{
				attackPlayer(Role.PLAYER1);
			}
			else
			{
				attackPlayer(Role.PLAYER2);
			}
		}
	}
	
	private void attackPlayer(Role player)
	{
		System.out.println("Attacking player " + player);
		int damage = -3 - new Random().nextInt(5);
		ServerGame.instance().sockets().sender().sendPacketToAllClients(new PacketExecuteBattleIDServerClient(getBattle().getID(), player.ordinal(), damage));
		executeBattle(player.ordinal(), damage);
	}
	
	@Override
	public void executeBattle(int battleID, int deltaHealth)
	{
		super.executeBattle(battleID, deltaHealth);
		
		BattlePlayerEntity player = getBattle().getPlayer(Role.fromOrdinal(battleID));
		
		if (player == null)
		{
			Game.instance(isServerSide()).logger().log(ALogType.CRITICAL, "Crab couldn't attack player because the BattlePlayerEntity is null");
			return;
		}
		
		playerAttacking = player;
		this.deltaDamage = deltaHealth;
		
		if (!isServerSide())
		{
			BattlePosition bPos = player.getBattlePosition();
			
			xInterpolation = (getBattlePosition().getReferenceX() - bPos.getReferenceX()) / 40D;
			yInterpolation = (getBattlePosition().getReferenceY() - bPos.getReferenceY()) / 40D;// 5D / 50D;
		}
		
		ticksUntilPass = 100;
	}
	
	@Override
	public void render(Texture renderTo, LightMap lightmap)
	{
		shadow.render(renderTo, (int) fightPos.getX() - 1, (int) fightPos.getY() + 8);
		asset.render(renderTo, (int) fightPos.getX(), (int) fightPos.getY()); // fightPos);
		light.renderCentered(lightmap);
		// light.renderCentered(lightmap);
	}
	
	@Override
	public void renderPostLightmap(Texture renderTo)
	{
		if (ticksUntilPass > 10)
		{
			blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2 + 1, ClientGame.HEIGHT / 2 + 1, "Uhh... Useless Mr Crabs");
			whiteFont.renderCentered(renderTo, ClientGame.WIDTH / 2, ClientGame.HEIGHT / 2, "Uhh... Useless Mr Crabs");
		}
		
		if (isThisSelected() && getBattle().isSelectingAttack())
		{
			arrow.render(renderTo, (int) fightPos.getX() + 2, (int) fightPos.getY() - 10);
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (ticksUntilPass > 0)
		{
			ticksUntilPass--;
			
			if (!isServerSide())
			{
				if (ticksUntilPass > 60)
				{
					fightPos.add(-xInterpolation, -yInterpolation);
				}
				else if (ticksUntilPass == 0)
				{
					fightPos.setVector(originPos);
				}
				else if (ticksUntilPass < 40)
				{
					fightPos.add(xInterpolation, yInterpolation);
				}
			}
			
			if (ticksUntilPass == 60)
			{
				// TODO make a way to defend against this attack?
				playerAttacking.addHealth(deltaDamage);
			}
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
			
			arrow.tick();
			
			light.setPosition(fightPos.getX() + 8, fightPos.getY());
			light.tick();
		}
	}
}
