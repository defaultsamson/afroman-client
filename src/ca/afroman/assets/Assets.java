package ca.afroman.assets;

import java.util.ArrayList;
import java.util.List;

public class Assets
{
	private static List<AssetArray> assetArrays = new ArrayList<AssetArray>();
	
	private static List<Asset> assets = new ArrayList<Asset>();
	
	public static void dispose()
	{
		for (AssetArray asset : assetArrays)
		{
			asset.dispose();
		}
		
		for (Asset asset : assets)
		{
			asset.dispose();
		}
	}
	
	public static Asset getAsset(AssetType type)
	{
		if (type == null) return null;
		
		for (Asset asset : assets)
		{
			if (asset != null && asset.getAssetType() == type)
			{
				return asset;
			}
		}
		return null;
	}
	
	public static AssetArray getAssetArray(AssetType type)
	{
		if (type == null) return null;
		
		for (AssetArray asset : assetArrays)
		{
			if (asset.getAssetType() == type)
			{
				return asset;
			}
		}
		return null;
	}
	
	public static List<Asset> getAssets()
	{
		return assets;
	}
	
	public static AudioClip getAudioClip(AssetType type)
	{
		Asset asset = getAsset(type);
		
		if (asset != null && asset.getAssetType() == type && asset instanceof AudioClip)
		{
			return (AudioClip) asset;
		}
		
		return null;
	}
	
	public static Font getFont(AssetType type)
	{
		Asset asset = getAssetArray(type);
		
		if (asset != null && asset.getAssetType() == type && asset instanceof Font)
		{
			return (Font) asset;
		}
		
		return null;
	}
	
	public static SpriteAnimation getSpriteAnimation(AssetType type)
	{
		Asset asset = getAsset(type);
		
		if (asset != null && asset.getAssetType() == type && asset instanceof SpriteAnimation)
		{
			return (SpriteAnimation) asset;
		}
		
		return null;
	}
	
	public static StepSpriteAnimation getStepSpriteAnimation(AssetType type)
	{
		Asset asset = getAsset(type);
		
		if (asset != null && asset.getAssetType() == type && asset instanceof StepSpriteAnimation)
		{
			return (StepSpriteAnimation) asset;
		}
		
		return null;
	}
	
	public static Texture getTexture(AssetType type)
	{
		Asset asset = getAsset(type);
		
		if (asset != null && asset.getAssetType() == type && asset instanceof Texture)
		{
			return (Texture) asset;
		}
		
		return null;
	}
	
	public static void load()
	{
		Texture sheet = Texture.fromResource(AssetType.INVALID, "spritesheet.png");
		Texture font = Texture.fromResource(AssetType.INVALID, "gui/fonts.png");
		Texture buttons = Texture.fromResource(AssetType.INVALID, "gui/buttons.png");
		
		Texture filter = Texture.fromResource(AssetType.FILTER, "filter_opaque.png");
		filter.setFromGreyscaleToAlphaMask();
		assets.add(filter);
		
		assets.add(Texture.fromResource(AssetType.CAT, "cat.png"));
		
		assetArrays.add(new Font(AssetType.FONT_BLACK, font.getSubTexture(AssetType.FONT_BLACK, 0, 8 * 0, 256, 32)));
		assetArrays.add(new Font(AssetType.FONT_WHITE, font.getSubTexture(AssetType.FONT_WHITE, 0, 8 * 12, 256, 32)));
		assetArrays.add(new Font(AssetType.FONT_NOBLE, font.getSubTexture(AssetType.FONT_NOBLE, 0, 8 * 4, 256, 32)));
		
		assetArrays.add(new AssetArray(AssetType.PLAYER_ONE_RAW, Texture.fromResource(AssetType.PLAYER_ONE_RAW, "player1.png").toTextureArray(3, 4)));
		
		AssetArray player = Assets.getAssetArray(AssetType.PLAYER_ONE_RAW);
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_UP, true, 10, (Texture) player.getAsset(9), (Texture) player.getAsset(10), (Texture) player.getAsset(11)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_DOWN, true, 10, (Texture) player.getAsset(0), (Texture) player.getAsset(1), (Texture) player.getAsset(2)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_LEFT, true, 10, (Texture) player.getAsset(3), (Texture) player.getAsset(4), (Texture) player.getAsset(5)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_RIGHT, true, 10, (Texture) player.getAsset(6), (Texture) player.getAsset(7), (Texture) player.getAsset(8)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_IDLE_UP, true, 0, (Texture) player.getAsset(10)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_IDLE_DOWN, true, 0, (Texture) player.getAsset(1)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_IDLE_LEFT, true, 0, (Texture) player.getAsset(4)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_IDLE_RIGHT, true, 0, (Texture) player.getAsset(7)));
		
		assetArrays.add(new AssetArray(AssetType.PLAYER_TWO_RAW, Texture.fromResource(AssetType.PLAYER_TWO_RAW, "player2.png").toTextureArray(3, 4)));
		
		AssetArray player2 = Assets.getAssetArray(AssetType.PLAYER_TWO_RAW);
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_UP, true, 10, (Texture) player2.getAsset(9), (Texture) player2.getAsset(10), (Texture) player2.getAsset(11)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_DOWN, true, 10, (Texture) player2.getAsset(0), (Texture) player2.getAsset(1), (Texture) player2.getAsset(2)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_LEFT, true, 10, (Texture) player2.getAsset(3), (Texture) player2.getAsset(4), (Texture) player2.getAsset(5)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_RIGHT, true, 10, (Texture) player2.getAsset(6), (Texture) player2.getAsset(7), (Texture) player2.getAsset(8)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_IDLE_UP, true, 0, (Texture) player2.getAsset(10)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_IDLE_DOWN, true, 0, (Texture) player2.getAsset(1)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_IDLE_LEFT, true, 0, (Texture) player2.getAsset(4)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_IDLE_RIGHT, true, 0, (Texture) player2.getAsset(7)));
		
		assets.add(sheet.getSubTexture(AssetType.TILE_GRASS, 16 * 0, 16 * 0, 16, 16));
		assets.add(sheet.getSubTexture(AssetType.TILE_GRASS_INNER_TOPLEFT, 16 * 3, 16 * 3, 16, 16));
		assets.add(sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_BOTTOM, 16 * 4, 16 * 3, 16, 16));
		assets.add(sheet.getSubTexture(AssetType.TILE_GRASS_INNER_TOPRIGHT, 16 * 5, 16 * 3, 16, 16));
		assets.add(sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_RIGHT, 16 * 3, 16 * 4, 16, 16));
		assets.add(sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_LEFT, 16 * 5, 16 * 4, 16, 16));
		assets.add(sheet.getSubTexture(AssetType.TILE_GRASS_INNER_BOTTOMLEFT, 16 * 3, 16 * 5, 16, 16));
		assets.add(sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_TOP, 16 * 4, 16 * 5, 16, 16));
		assets.add(sheet.getSubTexture(AssetType.TILE_GRASS_INNER_BOTTOMRIGHT, 16 * 5, 16 * 5, 16, 16));
		assets.add(sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_TOPLEFT, 16 * 6, 16 * 3, 16, 16));
		assets.add(sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_TOPRIGHT, 16 * 7, 16 * 3, 16, 16));
		assets.add(sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_BOTTOMLEFT, 16 * 6, 16 * 4, 16, 16));
		assets.add(sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_BOTTOMRIGHT, 16 * 7, 16 * 4, 16, 16));
		
		assets.add(sheet.getSubTexture(AssetType.TILE_DIRT, 16 * 0, 16 * 1, 16, 16));
		
		assets.add(sheet.getSubTexture(AssetType.TILE_WATER, 16 * 2, 16 * 0, 16, 16));
		
		assets.add(sheet.getSubTexture(AssetType.TILE_WALL, 16 * 1, 16 * 0, 16, 16));
		Texture wallgrass = sheet.getSubTexture(AssetType.TILE_WALL_GRASS, 16 * 1, 16 * 1, 16, 16);
		assets.add(wallgrass);
		assets.add(wallgrass.clone(AssetType.TILE_WALL_GRASS_FLIP).rotate(180));
		Texture wallSide = sheet.getSubTexture(AssetType.TILE_WALL_GRASS_SIDE, 16 * 4, 0, 16, 16);
		assets.add(wallSide);
		assets.add(wallSide.clone(AssetType.TILE_WALL_GRASS_SIDE_FLIP).rotate(180));
		
		Texture inCornerL = sheet.getSubTexture(AssetType.TILE_WALL_INCORNER_TOP_L, 16 * 3, 16, 16, 16);
		assets.add(inCornerL);
		assets.add(inCornerL.clone(AssetType.TILE_WALL_INCORNER_BOTTOM_L).rotate(180));
		Texture inCornerR = sheet.getSubTexture(AssetType.TILE_WALL_INCORNER_TOP_R, 16 * 3, 16 * 0, 16, 16);
		assets.add(inCornerR);
		assets.add(inCornerR.clone(AssetType.TILE_WALL_INCORNER_BOTTOM_R).rotate(180));
		
		Texture outCorner = sheet.getSubTexture(AssetType.TILE_WALL_OUTCORNER_N, 16 * 4, 16 * 1, 16, 16);
		assets.add(outCorner);
		assets.add(outCorner.clone(AssetType.TILE_WALL_OUTCORNER_E).rotate(90));
		assets.add(outCorner.clone(AssetType.TILE_WALL_OUTCORNER_S).rotate(180));
		assets.add(outCorner.clone(AssetType.TILE_WALL_OUTCORNER_W).rotate(270));
		
		assets.add(sheet.getSubTexture(AssetType.TILE_WALL_PIPE, 16 * 5, 16 * 0, 16, 32));
		assets.add(sheet.getSubTexture(AssetType.TILE_WALL_PIPE_EDGE, 16 * 6, 16 * 0, 16, 16));
		
		assets.add(sheet.getSubTexture(AssetType.TILE_BRIDGE_WOOD_SIDE, 16 * 7, 16 * 7, 24, 24));
		
		Texture openDoor = sheet.getSubTexture(AssetType.TILE_DOOR_FRONT_OPEN, 16 * 7, 16 * 1, 48, 16);
		Texture closedDoor = sheet.getSubTexture(AssetType.TILE_DOOR_FRONT_CLOSED, 16 * 7, 16 * 0, 48, 16);
		assets.add(openDoor);
		assets.add(closedDoor);
		assets.add(openDoor.clone(AssetType.TILE_DOOR_RIGHT_OPEN).rotate(90));
		assets.add(closedDoor.clone(AssetType.TILE_DOOR_RIGHT_OPEN).rotate(90));
		assets.add(openDoor.clone(AssetType.TILE_DOOR_BOTTOM_OPEN).rotate(180));
		assets.add(closedDoor.clone(AssetType.TILE_DOOR_BOTTOM_OPEN).rotate(180));
		assets.add(openDoor.clone(AssetType.TILE_DOOR_LEFT_OPEN).rotate(270));
		assets.add(closedDoor.clone(AssetType.TILE_DOOR_LEFT_OPEN).rotate(270));
		
		assets.add(new StepSpriteAnimation(new int[] { 0, 1 }, AssetType.TILE_PLATE_TEST, false, 30, sheet.getSubTexture(AssetType.TILE_PLATE_TEST, 16 * 10, 16 * 3, 16, 32).toTextureArray(1, 2)));
		
		assets.add(new SpriteAnimation(AssetType.TILE_AURA, false, 12, sheet.getSubTexture(AssetType.TILE_AURA, 16 * 4, 16 * 2, 16 * 6, 16).toTextureArray(6, 1)));
		
		assets.add(sheet.getSubTexture(AssetType.TILE_ROCK, 16 * 2, 16 * 1, 16, 16));
		
		// Texture lamp = sheet.getSubTexture(AssetType.TILE_LAMP_LEFT, 16 * 8, 16 * 4, 16, 16);
		// assets.add(lamp);
		// assets.add(lamp.clone(AssetType.TILE_LAMP_RIGHT).flipX());
		assets.add(new SpriteAnimation(AssetType.TILE_LAMP, true, 16, sheet.getSubTexture(AssetType.TILE_LAMP, 16 * 8, 16 * 4, 24, 16).toTextureArray(3, 2)));
		
		assets.add(new SpriteAnimation(AssetType.SLIME, false, 12, sheet.getSubTexture(AssetType.SLIME, 16 * 0, (16 * 7), 16 * 2, 16).toTextureArray(2, 1)));
		
		assets.add(buttons.getSubTexture(AssetType.BUTTON_NORMAL, 0, 0, 3, 16));
		assets.add(buttons.getSubTexture(AssetType.BUTTON_HOVER, 3, 0, 3, 16));
		assets.add(buttons.getSubTexture(AssetType.BUTTON_PRESSED, 6, 0, 3, 16));
		
		Texture update = Texture.fromResource(AssetType.INVALID, "gui/icon_update.png");
		assets.add(new StepSpriteAnimation(new int[] { 0 }, AssetType.ICON_UPDATE, false, 1, update.toTextureArray(25, 1)));
		
		Texture settings = Texture.fromResource(AssetType.INVALID, "gui/icon_settings.png");
		assets.add(new StepSpriteAnimation(new int[] { 0 }, AssetType.ICON_SETTINGS, false, 1, settings.toTextureArray(24, 1)));
		
		Texture next = Texture.fromResource(AssetType.INVALID, "gui/icons.png");
		
		assets.add(next.getSubTexture(AssetType.ICON_NEXT, 0, 0, 16, 16));
		
		assets.add(sheet.getSubTexture(AssetType.ICON_REFRESH, 0, 16 * 2, 16, 16));
		
		assets.add(buttons.getSubTexture(AssetType.TEXT_FIELD, 9, 0, 3, 16));
		assets.add(buttons.getSubTexture(AssetType.TEXT_FIELD_CLEAR, 12, 0, 3, 16));
		
		assets.add(AudioClip.fromResource(AssetType.AUDIO_BUTTON_PUSH, AudioType.SFX, "but_down"));
		assets.add(AudioClip.fromResource(AssetType.AUDIO_BUTTON_RELEASE, AudioType.SFX, "but_up"));
		assets.add(AudioClip.fromResource(AssetType.AUDIO_MENU_MUSIC, AudioType.MUSIC, "music/menu"));
	}
}
