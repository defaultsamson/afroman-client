package ca.afroman.level;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.entity.Tile;
import ca.afroman.entity.api.Direction;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.events.DoorEvent;
import ca.afroman.events.PlateTrigger;
import ca.afroman.events.SwitchTrigger;
import ca.afroman.events.TriggerType;
import ca.afroman.level.api.Level;
import ca.afroman.level.api.LevelType;
import ca.afroman.light.PointLight;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ColourUtil;

public class MainLevel extends Level
{
	
	public MainLevel(boolean isServerSide)
	{
		super(isServerSide, LevelType.MAIN);
		
		final int doorID = 20;
		List<Integer> inTrig = new ArrayList<Integer>();
		inTrig.add(doorID);
		
		List<TriggerType> type = new ArrayList<TriggerType>();
		type.add(TriggerType.PLAYER_COLLIDE);
		type.add(TriggerType.PLAYER_UNCOLLIDE);
		
		List<TriggerType> switchTrig = new ArrayList<TriggerType>();
		switchTrig.add(TriggerType.PLAYER_INTERACT);
		
		int doorColour = ColourUtil.TILE_REPLACE_COLOUR_RED;
		int offColour = ColourUtil.TILE_REPLACE_COLOUR_DARKGREY;
		
		DoorEvent dora = new DoorEvent(isServerSide, false, new Vector2DDouble(80.0, 48.0), inTrig, null, Direction.UP, doorColour);
		dora.addToLevel(this);
		dora.setEnabled(false);
		
		PlateTrigger plate = new PlateTrigger(isServerSide, false, new Vector2DDouble(64.0, 0.0), null, inTrig, type, doorColour);
		plate.addToLevel(this);
		
		SwitchTrigger switchToggle = new SwitchTrigger(isServerSide, false, new Vector2DDouble(128.0, 16.0), null, inTrig, switchTrig, doorColour, offColour);
		switchToggle.addToLevel(this);
		
		// Hitboxes
		new Hitbox(false, 48.0, -48.0, 16.0, 80.0).addToLevel(this);
		new Hitbox(false, 144.0, -48.0, 16.0, 80.0).addToLevel(this);
		new Hitbox(false, 122.0, -4.0, 8.0, 6.0).addToLevel(this);
		new Hitbox(false, 112.0, -48.0, 16.0, 28.0).addToLevel(this);
		new Hitbox(false, 65.0, -48.0, 79.0, 13.0).addToLevel(this);
		new Hitbox(false, 48.0, 32.0, 16.0, 16.0).addToLevel(this);
		new Hitbox(false, 65.0, 33.0, 31.0, 15.0).addToLevel(this);
		new Hitbox(false, 65.0, 48.0, 15.0, 64.0).addToLevel(this);
		new Hitbox(false, 80.0, 97.0, 63.0, 14.0).addToLevel(this);
		new Hitbox(false, 128.0, 55.0, 14.0, 45.0).addToLevel(this);
		new Hitbox(false, 80.0, 52.0, 16.0, 8.0).addToLevel(this);
		new Hitbox(false, 112.0, 52.0, 16.0, 8.0).addToLevel(this);
		new Hitbox(false, 112.0, 33.0, 39.0, 15.0).addToLevel(this);
		
		if (!isServerSide)
		{
			// Tiles
			new Tile(0, false, new Vector2DDouble(96.0, 48.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(112.0, 64.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(96.0, 32.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(112.0, 16.0), Assets.getDrawableAsset(AssetType.TILE_DIRT).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128.0, 16.0), Assets.getDrawableAsset(AssetType.TILE_DIRT).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128.0, 0.0), Assets.getDrawableAsset(AssetType.TILE_DIRT).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(112.0, 0.0), Assets.getDrawableAsset(AssetType.TILE_DIRT).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128.0, -16.0), Assets.getDrawableAsset(AssetType.TILE_DIRT).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(48.0, 0.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144.0, 0.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144.0, -16.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144.0, -32.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144.0, -48.0), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_TOP_R).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(48.0, -48.0), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_TOP_L).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64.0, -48.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80.0, -48.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(96.0, -48.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128.0, -48.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(48.0, -32.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(48.0, -16.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64.0, -16.0), Assets.getDrawableAsset(AssetType.TILE_WATER).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64.0, -32.0), Assets.getDrawableAsset(AssetType.TILE_WATER).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(96.0, -32.0), Assets.getDrawableAsset(AssetType.TILE_WATER).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80.0, -32.0), Assets.getDrawableAsset(AssetType.TILE_WATER).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80.0, -16.0), Assets.getDrawableAsset(AssetType.TILE_WATER).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(96.0, -16.0), Assets.getDrawableAsset(AssetType.TILE_WATER).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128.0, -32.0), Assets.getDrawableAsset(AssetType.TILE_DIRT).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(48.0, 32.0), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_BOTTOM_R).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144.0, 32.0), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_BOTTOM_L).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64.0, 48.0), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_TOP_L).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128.0, 48.0), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_TOP_R).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(112.0, 48.0), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_E).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80.0, 48.0), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_N).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80.0, 32.0), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_W).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(112.0, 32.0), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_S).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128.0, 32.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64.0, 32.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128.0, 64.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64.0, 64.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64.0, 80.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80.0, 96.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(96.0, 96.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(112.0, 96.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128.0, 80.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128.0, 96.0), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_BOTTOM_L).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64.0, 96.0), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_BOTTOM_R).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80.0, 64.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(96.0, 64.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(96.0, 80.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80.0, 80.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(112.0, 80.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144.0, 16.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(48.0, 16.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			
			new Tile(1, false, new Vector2DDouble(96.0, 16.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(80.0, 16.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(80.0, 0.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(64.0, 16.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(64.0, 0.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(96.0, 0.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(112.0, 0.0), Assets.getDrawableAsset(AssetType.TILE_GRASS_INNER_TOPLEFT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(112.0, 16.0), Assets.getDrawableAsset(AssetType.TILE_GRASS_OUTER_RIGHT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(128.0, 0.0), Assets.getDrawableAsset(AssetType.TILE_GRASS_OUTER_BOTTOMRIGHT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(128.0, -16.0), Assets.getDrawableAsset(AssetType.TILE_GRASS_OUTER_RIGHT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(128.0, 16.0), Assets.getDrawableAsset(AssetType.TILE_GRASS_INNER_BOTTOMRIGHT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(128.0, 0.0), Assets.getDrawableAsset(AssetType.TILE_GRASS_OUTER_TOPLEFT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(64.0, -16.0), Assets.getDrawableAsset(AssetType.TILE_GRASS_OUTER_TOP).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(80.0, -16.0), Assets.getDrawableAsset(AssetType.TILE_GRASS_OUTER_TOP).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(96.0, -16.0), Assets.getDrawableAsset(AssetType.TILE_GRASS_INNER_BOTTOMRIGHT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(112.0, -16.0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(112.0, -32.0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(112.0, -48.0), Assets.getDrawableAsset(AssetType.TILE_WALL).clone()).addToLevel(this);
			
			new Tile(3, false, new Vector2DDouble(118.0, -14.0), Assets.getDrawableAsset(AssetType.TILE_OBJECT_POST).clone()).addToLevel(this);
			
			// Lights
			new PointLight(false, new Vector2DDouble(104.0, 72.0), 42.0).addToLevel(this);
		}
	}
	
	@Override
	public void chainEvents(Entity triggerer, int inTrigger)
	{
		super.chainEvents(triggerer, inTrigger);
	}
	
	@Override
	public void render(Texture renderTo)
	{
		Graphics2D g = renderTo.getGraphics();
		Paint old = g.getPaint();
		g.setPaint(new Color(0.1F, 0.1F, 0.1F));
		g.fillRect(0, 0, renderTo.getWidth(), renderTo.getHeight());
		g.setPaint(old);
		
		super.render(renderTo);
	}
}
