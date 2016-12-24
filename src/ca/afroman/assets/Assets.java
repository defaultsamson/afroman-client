package ca.afroman.assets;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.resource.SetInteger;

public class Assets
{
	private static List<DrawableAssetArray> assetArrays = new ArrayList<DrawableAssetArray>();
	
	private static List<Asset> assets = new ArrayList<Asset>();
	
	// Used for displaying in the progress bar when loading
	private static int loadedAssetCount;
	private static final double ASSET_COUNT = 107;
	private static int initPercValue;
	private static final int PERC_DIFF = 70;
	
	private static void addAsset(SetInteger perc, Asset asset)
	{
		assets.add(asset);
		loadedAssetCount++;
		
		// TODO each time an asset is added, the value of ASSET_COUNT must be updated to compensate
		// System.out.println("Value: [" + loadedAssetCount + "] " + (int) (initPercValue + (((double) loadedAssetCount / (double) ASSET_COUNT) * PERC_DIFF)));
		perc.setValue((int) (initPercValue + ((loadedAssetCount / ASSET_COUNT) * PERC_DIFF)));
	}
	
	/**
	 * Disposes of all the assets loaded in the static instance of Assets.
	 */
	public static void dispose()
	{
		for (DrawableAssetArray asset : assetArrays)
		{
			asset.dispose();
		}
		
		for (Asset asset : assets)
		{
			asset.dispose();
		}
	}
	
	/**
	 * Gets an Asset who's AssetType corresponds with the provided AssetType.
	 * 
	 * @param type the provided AssetType
	 * @return the matching Asset.
	 *         <p>
	 *         <code>null</code> if no matching Asset object could be found.
	 */
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
	
	/**
	 * Gets a DrawableAssetArray who's AssetType corresponds with the provided AssetType.
	 * 
	 * @param type the provided AssetType
	 * @return the matching DrawableAssetArray.
	 *         <p>
	 *         <code>null</code> if no matching DrawableAssetArray object could be found.
	 */
	public static DrawableAssetArray getAssetArray(AssetType type)
	{
		if (type == null) return null;
		
		for (DrawableAssetArray asset : assetArrays)
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
	
	/**
	 * Gets a AudioClip who's AssetType corresponds with the provided AssetType.
	 * 
	 * @param type the provided AssetType
	 * @return the matching AudioClip.
	 *         <p>
	 *         <code>null</code> if no matching AudioClip object could be found.
	 */
	public static AudioClip getAudioClip(AssetType type)
	{
		Asset asset = getAsset(type);
		
		if (asset != null && asset.getAssetType() == type && asset instanceof AudioClip)
		{
			return (AudioClip) asset;
		}
		
		return null;
	}
	
	/**
	 * Gets a DrawableAsset who's AssetType corresponds with the provided AssetType.
	 * 
	 * @param type the provided AssetType
	 * @return the matching DrawableAsset.
	 *         <p>
	 *         <code>null</code> if no matching DrawableAsset object could be found.
	 */
	public static DrawableAsset getDrawableAsset(AssetType type)
	{
		Asset asset = getAsset(type);
		
		if (asset != null && asset.getAssetType() == type && asset instanceof DrawableAsset)
		{
			return (DrawableAsset) asset;
		}
		
		return null;
	}
	/**
	 * Gets a Font who's AssetType corresponds with the provided AssetType.
	 * 
	 * @param type the provided AssetType
	 * @return the matching Font.
	 *         <p>
	 *         <code>null</code> if no matching Font object could be found.
	 */
	public static Font getFont(AssetType type)
	{
		Asset asset = getAssetArray(type);
		
		if (asset != null && asset.getAssetType() == type && asset instanceof Font)
		{
			return (Font) asset;
		}
		
		return null;
	}
	/**
	 * Gets a SpriteAnimation who's AssetType corresponds with the provided AssetType.
	 * 
	 * @param type the provided AssetType
	 * @return the matching SpriteAnimation.
	 *         <p>
	 *         <code>null</code> if no matching SpriteAnimation object could be found.
	 */
	public static SpriteAnimation getSpriteAnimation(AssetType type)
	{
		Asset asset = getAsset(type);
		
		if (asset != null && asset.getAssetType() == type && asset instanceof SpriteAnimation)
		{
			return (SpriteAnimation) asset;
		}
		
		return null;
	}
	/**
	 * Gets a StepSpriteAnimation who's AssetType corresponds with the provided AssetType.
	 * 
	 * @param type the provided AssetType
	 * @return the matching StepSpriteAnimation.
	 *         <p>
	 *         <code>null</code> if no matching StepSpriteAnimation object could be found.
	 */
	public static StepSpriteAnimation getStepSpriteAnimation(AssetType type)
	{
		Asset asset = getAsset(type);
		
		if (asset != null && asset.getAssetType() == type && asset instanceof StepSpriteAnimation)
		{
			return (StepSpriteAnimation) asset;
		}
		
		return null;
	}
	
	/**
	 * Gets a Texture who's AssetType corresponds with the provided AssetType.
	 * 
	 * @param type the provided AssetType
	 * @return the matching Texture.
	 *         <p>
	 *         <code>null</code> if no matching Texture object could be found.
	 */
	public static Texture getTexture(AssetType type)
	{
		Asset asset = getAsset(type);
		
		if (asset != null && asset.getAssetType() == type && asset instanceof Texture)
		{
			return (Texture) asset;
		}
		
		return null;
	}
	
	/**
	 * Loads all the game's local Assets to be used. These files should
	 * all be from the resource, meaning that each Asset's file(s) should
	 * be found within the running jar file when this is compiled.
	 */
	public static void load(SetInteger perc)
	{
		// perc is the percentage bar when loading shite
		initPercValue = perc.getValue();
		loadedAssetCount = 0;
		
		Texture sheet = Texture.fromResource(AssetType.INVALID, "spritesheet.png");
		Texture font = Texture.fromResource(AssetType.INVALID, "gui/fonts.png");
		Texture buttons = Texture.fromResource(AssetType.INVALID, "gui/buttons.png");
		
		Texture filter = Texture.fromResource(AssetType.FILTER, "filter_opaque.png");
		filter.setFromGreyscaleToAlphaMask();
		addAsset(perc, filter);
		
		addAsset(perc, Texture.fromResource(AssetType.CAT, "cat.png"));
		
		assetArrays.add(new Font(AssetType.FONT_BLACK, font.getSubTexture(AssetType.FONT_BLACK, 0, 8 * 0, 256, 32)));
		assetArrays.add(new Font(AssetType.FONT_WHITE, font.getSubTexture(AssetType.FONT_WHITE, 0, 8 * 12, 256, 32)));
		assetArrays.add(new Font(AssetType.FONT_NOBLE, font.getSubTexture(AssetType.FONT_NOBLE, 0, 8 * 4, 256, 32)));
		
		assetArrays.add(new DrawableAssetArray(AssetType.PLAYER_ONE_RAW, Texture.fromResource(AssetType.PLAYER_ONE_RAW, "player1.png").toTextureArray(3, 4)));
		
		DrawableAssetArray player = Assets.getAssetArray(AssetType.PLAYER_ONE_RAW);
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_ONE_UP, true, 10, (Texture) player.getDrawableAsset(9), (Texture) player.getDrawableAsset(10), (Texture) player.getDrawableAsset(11)));
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_ONE_DOWN, true, 10, (Texture) player.getDrawableAsset(0), (Texture) player.getDrawableAsset(1), (Texture) player.getDrawableAsset(2)));
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_ONE_LEFT, true, 10, (Texture) player.getDrawableAsset(3), (Texture) player.getDrawableAsset(4), (Texture) player.getDrawableAsset(5)));
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_ONE_RIGHT, true, 10, (Texture) player.getDrawableAsset(6), (Texture) player.getDrawableAsset(7), (Texture) player.getDrawableAsset(8)));
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_ONE_IDLE_UP, true, 0, (Texture) player.getDrawableAsset(10)));
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_ONE_IDLE_DOWN, true, 0, (Texture) player.getDrawableAsset(1)));
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_ONE_IDLE_LEFT, true, 0, (Texture) player.getDrawableAsset(4)));
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_ONE_IDLE_RIGHT, true, 0, (Texture) player.getDrawableAsset(7)));
		
		assetArrays.add(new DrawableAssetArray(AssetType.PLAYER_TWO_RAW, Texture.fromResource(AssetType.PLAYER_TWO_RAW, "player2.png").toTextureArray(3, 4)));
		
		DrawableAssetArray player2 = Assets.getAssetArray(AssetType.PLAYER_TWO_RAW);
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_TWO_UP, true, 10, (Texture) player2.getDrawableAsset(9), (Texture) player2.getDrawableAsset(10), (Texture) player2.getDrawableAsset(11)));
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_TWO_DOWN, true, 10, (Texture) player2.getDrawableAsset(0), (Texture) player2.getDrawableAsset(1), (Texture) player2.getDrawableAsset(2)));
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_TWO_LEFT, true, 10, (Texture) player2.getDrawableAsset(3), (Texture) player2.getDrawableAsset(4), (Texture) player2.getDrawableAsset(5)));
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_TWO_RIGHT, true, 10, (Texture) player2.getDrawableAsset(6), (Texture) player2.getDrawableAsset(7), (Texture) player2.getDrawableAsset(8)));
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_TWO_IDLE_UP, true, 0, (Texture) player2.getDrawableAsset(10)));
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_TWO_IDLE_DOWN, true, 0, (Texture) player2.getDrawableAsset(1)));
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_TWO_IDLE_LEFT, true, 0, (Texture) player2.getDrawableAsset(4)));
		addAsset(perc, new SpriteAnimation(AssetType.PLAYER_TWO_IDLE_RIGHT, true, 0, (Texture) player2.getDrawableAsset(7)));
		
		assetArrays.add(new DrawableAssetArray(AssetType.CRAB_RAW, Texture.fromResource(AssetType.CRAB_RAW, "crab.png").toTextureArray(2, 4)));
		
		DrawableAssetArray crab = Assets.getAssetArray(AssetType.CRAB_RAW);
		addAsset(perc, new SpriteAnimation(AssetType.CRAB_UP, true, 10, (Texture) crab.getDrawableAsset(6), (Texture) crab.getDrawableAsset(7)));
		addAsset(perc, new SpriteAnimation(AssetType.CRAB_DOWN, true, 10, (Texture) crab.getDrawableAsset(0), (Texture) crab.getDrawableAsset(1)));
		addAsset(perc, new SpriteAnimation(AssetType.CRAB_LEFT, true, 10, (Texture) crab.getDrawableAsset(4), (Texture) crab.getDrawableAsset(5)));
		addAsset(perc, new SpriteAnimation(AssetType.CRAB_RIGHT, true, 10, (Texture) crab.getDrawableAsset(2), (Texture) crab.getDrawableAsset(3)));
		addAsset(perc, new SpriteAnimation(AssetType.CRAB_IDLE_UP, true, 0, (Texture) crab.getDrawableAsset(6)));
		addAsset(perc, new SpriteAnimation(AssetType.CRAB_IDLE_DOWN, true, 0, (Texture) crab.getDrawableAsset(1)));
		addAsset(perc, new SpriteAnimation(AssetType.CRAB_IDLE_LEFT, true, 0, (Texture) crab.getDrawableAsset(5)));
		addAsset(perc, new SpriteAnimation(AssetType.CRAB_IDLE_RIGHT, true, 0, (Texture) crab.getDrawableAsset(2)));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_GRASS, 16 * 0, 16 * 0, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_GRASS_INNER_TOPLEFT, 16 * 3, 16 * 3, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_BOTTOM, 16 * 4, 16 * 3, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_GRASS_INNER_TOPRIGHT, 16 * 5, 16 * 3, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_RIGHT, 16 * 3, 16 * 4, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_LEFT, 16 * 5, 16 * 4, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_GRASS_INNER_BOTTOMLEFT, 16 * 3, 16 * 5, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_TOP, 16 * 4, 16 * 5, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_GRASS_INNER_BOTTOMRIGHT, 16 * 5, 16 * 5, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_TOPLEFT, 16 * 6, 16 * 3, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_TOPRIGHT, 16 * 7, 16 * 3, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_BOTTOMLEFT, 16 * 6, 16 * 4, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_GRASS_OUTER_BOTTOMRIGHT, 16 * 7, 16 * 4, 16, 16));
		
		Texture tallGrass = sheet.getSubTexture(AssetType.TILE_GRASS_TALL_LEFT, 16 * 2, 16 * 3, 16, 16);
		addAsset(perc, tallGrass);
		addAsset(perc, tallGrass.clone(AssetType.TILE_GRASS_TALL_RIGHT).flipX());
		Texture wideGrass = sheet.getSubTexture(AssetType.TILE_GRASS_WIDE_RIGHT, 16 * 1, 16 * 3, 16, 16);
		addAsset(perc, wideGrass);
		addAsset(perc, wideGrass.clone(AssetType.TILE_GRASS_WIDE_LEFT).flipX());
		Texture purpleFlower = sheet.getSubTexture(AssetType.TILE_GRASS_FLOWER_RIGHT, 16 * 0, 16 * 3, 16, 16);
		addAsset(perc, purpleFlower);
		addAsset(perc, purpleFlower.clone(AssetType.TILE_GRASS_FLOWER_LEFT).flipX());
		
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_DIRT, 16 * 0, 16 * 1, 16, 16));
		
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_WATER, 16 * 2, 16 * 0, 16, 16));
		
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_OBJECT_POST, 16 * 6, 16 * 1, 16, 16, -2));
		
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_WALL, 16 * 1, 16 * 0, 16, 16));
		Texture wallGrass = sheet.getSubTexture(AssetType.TILE_WALL_GRASS, 16 * 1, 16 * 1, 16, 16);
		addAsset(perc, wallGrass);
		addAsset(perc, wallGrass.clone(AssetType.TILE_WALL_GRASS_FLIP).rotate(180));
		Texture wallSide = sheet.getSubTexture(AssetType.TILE_WALL_GRASS_SIDE, 16 * 4, 0, 16, 16);
		addAsset(perc, wallSide);
		addAsset(perc, wallSide.clone(AssetType.TILE_WALL_GRASS_SIDE_FLIP).rotate(180));
		
		Texture inCornerL = sheet.getSubTexture(AssetType.TILE_WALL_INCORNER_TOP_L, 16 * 3, 16, 16, 16);
		addAsset(perc, inCornerL);
		addAsset(perc, inCornerL.clone(AssetType.TILE_WALL_INCORNER_BOTTOM_L).rotate(180));
		Texture inCornerR = sheet.getSubTexture(AssetType.TILE_WALL_INCORNER_TOP_R, 16 * 3, 16 * 0, 16, 16);
		addAsset(perc, inCornerR);
		addAsset(perc, inCornerR.clone(AssetType.TILE_WALL_INCORNER_BOTTOM_R).rotate(180));
		
		Texture outCorner = sheet.getSubTexture(AssetType.TILE_WALL_OUTCORNER_N, 16 * 4, 16 * 1, 16, 16);
		addAsset(perc, outCorner);
		addAsset(perc, outCorner.clone(AssetType.TILE_WALL_OUTCORNER_E).rotate(90));
		addAsset(perc, outCorner.clone(AssetType.TILE_WALL_OUTCORNER_S).rotate(180));
		addAsset(perc, outCorner.clone(AssetType.TILE_WALL_OUTCORNER_W).rotate(270));
		
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_WALL_PIPE, 16 * 5, 16 * 0, 16, 32));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_WALL_PIPE_HALF, 16 * 14, 16 * 0, 16, 32));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_WALL_PIPE_SIDE, 16 * 10, 16 * 0, 16, 16));
		Texture topPipeCorner = sheet.getSubTexture(AssetType.TILE_WALL_PIPE_TLCORNER, 16 * 11, 16 * 0, 16, 32);
		addAsset(perc, topPipeCorner);
		addAsset(perc, topPipeCorner.clone(AssetType.TILE_WALL_PIPE_TRCORNER).flipX());
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_WALL_PIPE_EDGE, 16 * 6, 16 * 0, 16, 16));
		
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_BRIDGE_WOOD_SIDE, 16 * 7, 16 * 7, 24, 24));
		
		Texture openDoor = sheet.getSubTexture(AssetType.TILE_DOOR_UP_OPEN, 16 * 7 + 8, 16 * 1, 32, 16);
		Texture closedDoor = sheet.getSubTexture(AssetType.TILE_DOOR_UP_CLOSED, 16 * 7 + 8, 16 * 0, 32, 16);
		addAsset(perc, openDoor);
		addAsset(perc, closedDoor);
		addAsset(perc, openDoor.clone(AssetType.TILE_DOOR_DOWN_OPEN).flipY());
		addAsset(perc, closedDoor.clone(AssetType.TILE_DOOR_DOWN_CLOSED).flipY());
		
		// The height of each half of the door when it's placed along the vertical axis
		int doorHeight = openDoor.getWidth() / 2;
		
		addAsset(perc, openDoor.getSubTexture(AssetType.TILE_DOOR_LEFT_TOP_OPEN, 0, 0, doorHeight, 16, -16).rotate(-90));
		addAsset(perc, openDoor.getSubTexture(AssetType.TILE_DOOR_LEFT_BOTTOM_OPEN, doorHeight, 0, doorHeight, 16, 0).rotate(-90));
		addAsset(perc, closedDoor.getSubTexture(AssetType.TILE_DOOR_LEFT_TOP_CLOSED, 0, 0, doorHeight, 16, -16).rotate(-90));
		addAsset(perc, closedDoor.getSubTexture(AssetType.TILE_DOOR_LEFT_BOTTOM_CLOSED, doorHeight, 0, doorHeight, 16, 0).rotate(-90));
		
		addAsset(perc, openDoor.getSubTexture(AssetType.TILE_DOOR_RIGHT_TOP_OPEN, 0, 0, doorHeight, 16, -16).rotate(90));
		addAsset(perc, openDoor.getSubTexture(AssetType.TILE_DOOR_RIGHT_BOTTOM_OPEN, doorHeight, 0, doorHeight, 16, 0).rotate(90));
		addAsset(perc, closedDoor.getSubTexture(AssetType.TILE_DOOR_RIGHT_TOP_CLOSED, 0, 0, doorHeight, 16, -16).rotate(90));
		addAsset(perc, closedDoor.getSubTexture(AssetType.TILE_DOOR_RIGHT_BOTTOM_CLOSED, doorHeight, 0, doorHeight, 16, 0).rotate(90));
		
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_PLATE_UP, 16 * 10, 16 * 3, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_PLATE_DOWN, 16 * 10, 16 * 4, 16, 16));
		
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_SWITCH_LEFT, 16 * 11, 16 * 3, 16, 16, -2));
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_SWITCH_RIGHT, 16 * 11, 16 * 4, 16, 16, -2));
		
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_BLOCK, 16 * 13, 16 * 2, 16, 16));
		addAsset(perc, new SpriteAnimation(AssetType.TILE_BlOCK_ANIMATED, false, 12, sheet.getSubTexture(AssetType.TILE_AURA, 16 * 12, 16 * 2, 16 * 4, 16).toTextureArray(4, 1)));
		
		addAsset(perc, new SpriteAnimation(AssetType.TILE_AURA, false, 12, sheet.getSubTexture(AssetType.TILE_AURA, 16 * 4, 16 * 2, 16 * 6, 16).toTextureArray(6, 1)));
		
		addAsset(perc, sheet.getSubTexture(AssetType.TILE_ROCK, 16 * 2, 16 * 1, 16, 16));
		
		addAsset(perc, Texture.fromResource(AssetType.ITEM_HOLDER, "item_holder.png"));
		
		addAsset(perc, sheet.getSubTexture(AssetType.ITEM_HAIRPIN_LARGE, 16 * 6, 16 * 5, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.ITEM_HAIRPIN_SMALL, 16 * 7, 16 * 5, 8, 8));
		
		addAsset(perc, sheet.getSubTexture(AssetType.ITEM_KNUCKLES_LARGE, 16 * 8, 16 * 5, 16, 16));
		addAsset(perc, sheet.getSubTexture(AssetType.ITEM_KNUCKLES_SMALL, 16 * 9, 16 * 5, 8, 8));
		
		// Texture lamp = sheet.getSubTexture(AssetType.TILE_LAMP_LEFT, 16 * 8, 16 * 4, 16, 16);
		// addAsset(perc, lamp);
		// addAsset(perc, lamp.clone(AssetType.TILE_LAMP_RIGHT).flipX());
		addAsset(perc, new SpriteAnimation(AssetType.TILE_LAMP, true, 16, sheet.getSubTexture(AssetType.TILE_LAMP, 16 * 8, 16 * 4, 24, 16).toTextureArray(3, 2)));
		
		addAsset(perc, new SpriteAnimation(AssetType.SLIME, false, 12, sheet.getSubTexture(AssetType.SLIME, 16 * 0, (16 * 7), 16 * 2, 16).toTextureArray(2, 1)));
		
		Texture next = Texture.fromResource(AssetType.INVALID, "gui/icons.png");
		
		addAsset(perc, buttons.getSubTexture(AssetType.BUTTON_NORMAL, 0, 0, 3, 16));
		addAsset(perc, buttons.getSubTexture(AssetType.BUTTON_HOVER, 3, 0, 3, 16));
		addAsset(perc, buttons.getSubTexture(AssetType.BUTTON_PRESSED, 6, 0, 3, 16));
		
		Texture update = Texture.fromResource(AssetType.INVALID, "gui/icon_update.png");
		addAsset(perc, new StepSpriteAnimation(new int[] { 0 }, AssetType.ICON_UPDATE, false, 1, update.toTextureArray(25, 1)));
		
		Texture settings = Texture.fromResource(AssetType.INVALID, "gui/icon_settings.png");
		addAsset(perc, new StepSpriteAnimation(new int[] { 0 }, AssetType.ICON_SETTINGS, false, 1, settings.toTextureArray(24, 1)));
		
		addAsset(perc, next.getSubTexture(AssetType.ICON_NEXT, 0, 0, 16, 16));
		
		addAsset(perc, sheet.getSubTexture(AssetType.ICON_REFRESH, 0, 16 * 2, 16, 16));
		
		addAsset(perc, buttons.getSubTexture(AssetType.TEXT_FIELD, 9, 0, 3, 16));
		addAsset(perc, buttons.getSubTexture(AssetType.TEXT_FIELD_CLEAR, 12, 0, 3, 16));
		
		addAsset(perc, AudioClip.fromResource(AssetType.AUDIO_BUTTON_PUSH, "but_down", AudioType.SFX));
		addAsset(perc, AudioClip.fromResource(AssetType.AUDIO_BUTTON_RELEASE, "but_up", AudioType.SFX));
		addAsset(perc, AudioClip.fromResource(AssetType.AUDIO_MENU_MUSIC, "music/menu", AudioType.MUSIC));
	}
}
