package ca.afroman.battle;

import java.util.Random;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.battle.animation.BattleAnimationAdministerDamage;
import ca.afroman.battle.animation.BattleAnimationAttack;
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
	// Client only
	private FlickeringLight light;
	private Texture shadow;
	private DrawableAsset asset;
	private SpriteAnimation idleAsset;
	private SpriteAnimation arrow;
	
	public BattleCrabEntity(Entity levelEntity, BattlePosition pos)
	{
		super(levelEntity, pos);
		
		if (!isServerSide())
		{
			fightPos = new Vector2DDouble(pos.getReferenceX() - 31, pos.getReferenceY() - 29);
			originPos = fightPos.clone();
			
			light = new FlickeringLight(true, fightPos.clone(), 55, 49, 3 + pos.ordinal());
			shadow = Assets.getTexture(AssetType.BATTLE_SHADOW);
			asset = idleAsset = Assets.getSpriteAnimation(AssetType.BATTLE_CRAB).clone();
			idleAsset.getTickCounter().setInterval(7 + pos.ordinal());
			arrow = Assets.getSpriteAnimation(AssetType.BATTLE_ARROW);
		}
		
		health = 20;
		maxHealth = 20;
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
		
		int travelTicks = 40;
		new BattleAnimationAdministerDamage(isServerSide(), player, travelTicks, deltaHealth).addToBattleEntity(this);
		new BattleAnimationAttack(isServerSide(), getBattlePosition(), player.getBattlePosition(), travelTicks, 10, fightPos).addToBattleEntity(this);
	}
	
	@Override
	public void render(Texture renderTo, LightMap lightmap)
	{
		super.render(renderTo, lightmap);
		
		shadow.render(renderTo, (int) fightPos.getX() - 1, (int) fightPos.getY() + 8);
		asset.render(renderTo, (int) fightPos.getX(), (int) fightPos.getY()); // fightPos);
		light.renderCentered(lightmap);
		// light.renderCentered(lightmap);
	}
	
	@Override
	public void renderPostLightmap(Texture renderTo)
	{
		super.renderPostLightmap(renderTo);
		
		if (isThisSelected() && getBattle().isSelectingAttack())
		{
			arrow.render(renderTo, (int) fightPos.getX() + 2, (int) fightPos.getY() - 10);
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
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
