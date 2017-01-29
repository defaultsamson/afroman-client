package ca.afroman.events;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.battle.BattleScene;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.Tile;
import ca.afroman.game.Game;
import ca.afroman.level.api.Level;
import ca.afroman.log.ALogType;
import ca.afroman.resource.Vector2DDouble;

public class BattleTumble extends HitboxTrigger
{
	private static final double HITBOX_WIDTH = 12;
	private static final double HITBOX_HEIGHT = 12;
	private static final double HITBOX_X_OFF = 2;
	private static final double HITBOX_Y_OFF = 2;
	
	private static List<TriggerType> getTriggerTypeList()
	{
		ArrayList<TriggerType> list = new ArrayList<TriggerType>();
		list.add(TriggerType.PLAYER_COLLIDE);
		return list;
	}
	private BattleScene scene;
	
	private Tile tile;
	
	public BattleTumble(boolean isServerSide, boolean isMicromanaged, Vector2DDouble position, BattleScene scene)
	{
		super(isServerSide, isMicromanaged, position, null, null, getTriggerTypeList(), new Hitbox(isServerSide, true, HITBOX_X_OFF, HITBOX_Y_OFF, HITBOX_WIDTH, HITBOX_HEIGHT));
		
		this.scene = scene;
		tile = new Tile(Level.DEFAULT_DYNAMIC_TILE_LAYER_INDEX, true, position, isServerSide ? null : Assets.getDrawableAsset(AssetType.BATTLE_TUMBLE).clone()); // Assets.getDrawableAsset(AssetType.CAT));
	}
	
	@Override
	public void addToLevel(Level newLevel)
	{
		if (level == newLevel) return;
		
		if (level != null)
		{
			if (!isServerSide())
			{
				tile.removeFromLevel();
			}
			
			level.getEvents().remove(this);
		}
		
		// Sets the new level
		level = newLevel;
		
		if (level != null)
		{
			if (!isServerSide())
			{
				System.out.println("Adding to the shite");
				tile.setLayer(level.getDynamicLayer());
				tile.addToLevel(level);
			}
			
			level.getEvents().add(this);
		}
	}
	
	@Override
	public void trigger(Entity triggerer)
	{
		if (triggerer instanceof PlayerEntity)
		{
			PlayerEntity p = (PlayerEntity) triggerer;
			
			scene.addEntityToBattle(p);
		}
		else
		{
			Game.instance(isServerSide()).logger().log(ALogType.WARNING, "BattleTumble was triggered by a non-PlayerEntity");
		}
	}
}
