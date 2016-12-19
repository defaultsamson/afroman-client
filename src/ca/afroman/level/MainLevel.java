package ca.afroman.level;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.entity.HairPin;
import ca.afroman.entity.api.Direction;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.entity.api.Tile;
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
		
		DoorEvent dora = new DoorEvent(isServerSide, false, new Vector2DDouble(88.0, 48.0), inTrig, null, Direction.UP, doorColour);
		dora.addToLevel(this);
		dora.setEnabled(false);
		
		DoorEvent dory = new DoorEvent(isServerSide, false, new Vector2DDouble(160, -24), inTrig, null, Direction.RIGHT, doorColour);
		dory.addToLevel(this);
		dory.setEnabled(true);
		
		PlateTrigger plate = new PlateTrigger(isServerSide, false, new Vector2DDouble(64.0, 0.0), null, inTrig, type, doorColour);
		plate.addToLevel(this);
		
		SwitchTrigger switchToggle = new SwitchTrigger(isServerSide, false, new Vector2DDouble(240, 64), null, inTrig, switchTrig, doorColour, offColour);
		switchToggle.addToLevel(this);
		
		new HairPin(isServerSide, false, new Vector2DDouble(102, 10)).addToLevel(this);
		
		//////////// HIGHLIGHT ME ////////////
		
		// Hitboxes
		new Hitbox(isServerSide, false, 48, -48, 16, 80).addToLevel(this);
		new Hitbox(isServerSide, false, 112, -48, 16, 28).addToLevel(this);
		new Hitbox(isServerSide, false, 65, -48, 79, 13).addToLevel(this);
		new Hitbox(isServerSide, false, 80, 97, 63, 14).addToLevel(this);
		new Hitbox(isServerSide, false, 145, 130, 90, 17).addToLevel(this);
		new Hitbox(isServerSide, false, 227, 114, 30, 21).addToLevel(this);
		new Hitbox(isServerSide, false, 243, 100, 32, 22).addToLevel(this);
		new Hitbox(isServerSide, false, 259, 49, 16, 49).addToLevel(this);
		new Hitbox(isServerSide, false, 147, -54, 64, 31).addToLevel(this);
		new Hitbox(isServerSide, false, 164, -29, 8, 12).addToLevel(this);
		new Hitbox(isServerSide, false, 164, -1, 8, 11).addToLevel(this);
		new Hitbox(isServerSide, false, 122, -4, 8, 3).addToLevel(this);
		new Hitbox(isServerSide, false, 212, 90, 8, 3).addToLevel(this);
		new Hitbox(isServerSide, false, 112, 33, 38, 27).addToLevel(this);
		new Hitbox(isServerSide, false, 148, 3, 25, 57).addToLevel(this);
		new Hitbox(isServerSide, false, 132, 60, 25, 70).addToLevel(this);
		new Hitbox(isServerSide, false, 63, 49, 14, 61).addToLevel(this);
		new Hitbox(isServerSide, false, 196, -24, 79, 83).addToLevel(this);
		new Hitbox(isServerSide, false, 60, 33, 36, 27).addToLevel(this);
		
		if (!isServerSide)
		{
			// Tiles
			new Tile(0, false, new Vector2DDouble(96, 48), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(112, 64), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(96, 32), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(112, 16), Assets.getDrawableAsset(AssetType.TILE_DIRT).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128, 16), Assets.getDrawableAsset(AssetType.TILE_DIRT).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128, 0), Assets.getDrawableAsset(AssetType.TILE_DIRT).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(112, 0), Assets.getDrawableAsset(AssetType.TILE_DIRT).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128, -16), Assets.getDrawableAsset(AssetType.TILE_DIRT).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(48, 0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144, -48), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_TOP_R).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(48, -48), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_TOP_L).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64, -48), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80, -48), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(96, -48), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128, -48), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(48, -32), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(48, -16), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64, -16), Assets.getDrawableAsset(AssetType.TILE_WATER).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64, -32), Assets.getDrawableAsset(AssetType.TILE_WATER).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(96, -32), Assets.getDrawableAsset(AssetType.TILE_WATER).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80, -32), Assets.getDrawableAsset(AssetType.TILE_WATER).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80, -16), Assets.getDrawableAsset(AssetType.TILE_WATER).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(96, -16), Assets.getDrawableAsset(AssetType.TILE_WATER).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128, -32), Assets.getDrawableAsset(AssetType.TILE_DIRT).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(48, 32), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_BOTTOM_R).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144, 32), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_BOTTOM_L).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64, 48), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_TOP_L).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128, 48), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_TOP_R).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(112, 48), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_E).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80, 48), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_N).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80, 32), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_W).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(112, 32), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_S).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128, 32), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64, 32), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128, 64), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64, 64), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64, 80), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80, 96), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(96, 96), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(112, 96), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128, 80), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(128, 96), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_BOTTOM_L).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(64, 96), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_BOTTOM_R).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80, 64), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(96, 64), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(96, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(80, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(112, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144, 16), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(48, 16), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144, -32), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_E).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144, 0), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_S).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(192, -16), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(192, 0), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(192, 16), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(192, 32), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(160, 16), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(160, 32), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144, 64), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144, 80), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144, 96), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144, 112), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE_FLIP).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(192, 48), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_E).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(160, 0), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_W).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(160, 48), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_N).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(144, 48), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_TOP_L).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(192, -32), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_TOP_R).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(176, -32), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			new Tile(0, false, new Vector2DDouble(160, -32), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			
			new Tile(1, false, new Vector2DDouble(96, 16), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(80, 16), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(80, 0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(64, 16), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(64, 0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(96, 0), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(112, 0), Assets.getDrawableAsset(AssetType.TILE_GRASS_INNER_TOPLEFT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(112, 16), Assets.getDrawableAsset(AssetType.TILE_GRASS_OUTER_RIGHT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(128, 0), Assets.getDrawableAsset(AssetType.TILE_GRASS_OUTER_BOTTOMRIGHT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(128, -16), Assets.getDrawableAsset(AssetType.TILE_GRASS_OUTER_RIGHT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(128, 16), Assets.getDrawableAsset(AssetType.TILE_GRASS_INNER_BOTTOMRIGHT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(128, 0), Assets.getDrawableAsset(AssetType.TILE_GRASS_OUTER_TOPLEFT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(64, -16), Assets.getDrawableAsset(AssetType.TILE_GRASS_OUTER_TOP).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(80, -16), Assets.getDrawableAsset(AssetType.TILE_GRASS_OUTER_TOP).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(96, -16), Assets.getDrawableAsset(AssetType.TILE_GRASS_INNER_BOTTOMRIGHT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(112, -16), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(112, -32), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(112, -48), Assets.getDrawableAsset(AssetType.TILE_WALL).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(176, 48), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(176, 64), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(160, 64), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(160, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(176, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(208, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(192, 64), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(208, 64), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(192, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(208, 96), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(208, 112), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(192, 112), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(192, 96), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(176, 96), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(176, 96), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(176, 112), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(160, 112), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(160, 96), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(224, 64), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(224, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(240, 64), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(240, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(224, 96), Assets.getDrawableAsset(AssetType.TILE_GRASS).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(224, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS_WIDE_LEFT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(176, 96), Assets.getDrawableAsset(AssetType.TILE_GRASS_WIDE_RIGHT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(208, 96), Assets.getDrawableAsset(AssetType.TILE_GRASS_TALL_LEFT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(208, 96), Assets.getDrawableAsset(AssetType.TILE_GRASS_TALL_RIGHT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(224, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS_TALL_RIGHT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(173, 91), Assets.getDrawableAsset(AssetType.TILE_GRASS_WIDE_LEFT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(171, 61), Assets.getDrawableAsset(AssetType.TILE_GRASS_TALL_RIGHT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(144, -16), Assets.getDrawableAsset(AssetType.TILE_DIRT).clone()).addToLevel(this);
			new Tile(1, false, new Vector2DDouble(256, 48), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_TOP_R).clone()).addToLevel(this);
			
			new Tile(2, false, new Vector2DDouble(144, 128), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_BOTTOM_R).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(224, 128), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_BOTTOM_L).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(64, -32), Assets.getDrawableAsset(AssetType.TILE_BRIDGE_WOOD_SIDE).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(80, -32), Assets.getDrawableAsset(AssetType.TILE_BRIDGE_WOOD_SIDE).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(96, -32), Assets.getDrawableAsset(AssetType.TILE_BRIDGE_WOOD_SIDE).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(176, -16), Assets.getDrawableAsset(AssetType.TILE_ROCK).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(176, 0), Assets.getDrawableAsset(AssetType.TILE_ROCK).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(176, 16), Assets.getDrawableAsset(AssetType.TILE_ROCK).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(176, 32), Assets.getDrawableAsset(AssetType.TILE_ROCK).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(160, -16), Assets.getDrawableAsset(AssetType.TILE_ROCK).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(144, -16), Assets.getDrawableAsset(AssetType.TILE_GRASS_INNER_TOPRIGHT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(160, -16), Assets.getDrawableAsset(AssetType.TILE_GRASS_INNER_TOPLEFT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(176, 32), Assets.getDrawableAsset(AssetType.TILE_GRASS_INNER_BOTTOMRIGHT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(176, 48), Assets.getDrawableAsset(AssetType.TILE_GRASS_OUTER_BOTTOM).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(208, 48), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(224, 48), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(240, 48), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(256, 64), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(256, 80), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_SIDE).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(256, 96), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_BOTTOM_L).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(240, 112), Assets.getDrawableAsset(AssetType.TILE_WALL_INCORNER_BOTTOM_L).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(240, 96), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_S).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(224, 112), Assets.getDrawableAsset(AssetType.TILE_WALL_OUTCORNER_S).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(208, 128), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_FLIP).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(192, 128), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_FLIP).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(176, 128), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_FLIP).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(160, 128), Assets.getDrawableAsset(AssetType.TILE_WALL_GRASS_FLIP).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(192, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS_WIDE_LEFT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(176, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS_TALL_LEFT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(192, 96), Assets.getDrawableAsset(AssetType.TILE_GRASS_TALL_LEFT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(192, 96), Assets.getDrawableAsset(AssetType.TILE_GRASS_WIDE_RIGHT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(208, 64), Assets.getDrawableAsset(AssetType.TILE_GRASS_TALL_LEFT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(167, 66), Assets.getDrawableAsset(AssetType.TILE_GRASS_TALL_LEFT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(179, 82), Assets.getDrawableAsset(AssetType.TILE_GRASS_WIDE_RIGHT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(176, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS_FLOWER_RIGHT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(224, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS_FLOWER_RIGHT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(208, 96), Assets.getDrawableAsset(AssetType.TILE_GRASS_FLOWER_RIGHT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(208, 64), Assets.getDrawableAsset(AssetType.TILE_GRASS_FLOWER_LEFT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(176, 80), Assets.getDrawableAsset(AssetType.TILE_GRASS_FLOWER_LEFT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(174, 93), Assets.getDrawableAsset(AssetType.TILE_GRASS_FLOWER_RIGHT).clone()).addToLevel(this);
			new Tile(2, false, new Vector2DDouble(169, 61), Assets.getDrawableAsset(AssetType.TILE_GRASS_FLOWER_RIGHT).clone()).addToLevel(this);
			
			new Tile(3, false, new Vector2DDouble(208, 80), Assets.getDrawableAsset(AssetType.TILE_OBJECT_POST).clone()).addToLevel(this);
			new Tile(3, false, new Vector2DDouble(118, -14), Assets.getDrawableAsset(AssetType.TILE_OBJECT_POST).clone()).addToLevel(this);
			
			new Tile(4, false, new Vector2DDouble(212, 78), Assets.getDrawableAsset(AssetType.TILE_LAMP).clone()).addToLevel(this);
			
			// Lights
			new PointLight(false, new Vector2DDouble(104, 72), 42).addToLevel(this);
			new PointLight(false, new Vector2DDouble(216, 88), 60).addToLevel(this);
		}
		
		//////////// HIGHLIGHT ME ////////////
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
