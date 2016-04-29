package ca.afroman.assets;

import java.util.HashMap;
import java.util.Map;

public enum Assets
{
	SPRITESHEET,
	FONTSHEET,
	
	FONT_BLACK,
	FONT_WHITE,
	FONT_NOBLE,
	
	RAW_PLAYER_ONE,
	PLAYER_ONE_UP,
	PLAYER_ONE_DOWN,
	PLAYER_ONE_LEFT,
	PLAYER_ONE_RIGHT,
	PLAYER_ONE_IDLE_UP,
	PLAYER_ONE_IDLE_DOWN,
	PLAYER_ONE_IDLE_LEFT,
	PLAYER_ONE_IDLE_RIGHT,
	
	RAW_PLAYER_TWO,
	PLAYER_TWO_UP,
	PLAYER_TWO_DOWN,
	PLAYER_TWO_LEFT,
	PLAYER_TWO_RIGHT,
	PLAYER_TWO_IDLE_UP,
	PLAYER_TWO_IDLE_DOWN,
	PLAYER_TWO_IDLE_LEFT,
	PLAYER_TWO_IDLE_RIGHT,
	
	TILE_GRASS,
	TILE_GRASS_INNER_TOPLEFT,
	TILE_GRASS_INNER_TOPRIGHT,
	TILE_GRASS_INNER_BOTTOMLEFT,
	TILE_GRASS_INNER_BOTTOMRIGHT,
	TILE_GRASS_OUTER_TOPLEFT,
	TILE_GRASS_OUTER_TOPRIGHT,
	TILE_GRASS_OUTER_BOTTOMLEFT,
	TILE_GRASS_OUTER_BOTTOMRIGHT,
	TILE_GRASS_OUTER_RIGHT,
	TILE_GRASS_OUTER_LEFT,
	TILE_GRASS_OUTER_BOTTOM,
	TILE_GRASS_OUTER_TOP,
	
	TILE_DIRT,
	
	TILE_WALL,
	TILE_WALL_GRASS,
	
	BUTTON_PRESSED,
	BUTTON_HOVER,
	BUTTON_NORMAL,
	
	TEXT_FIELD;
	
	public static HashMap<Assets, Asset> assets = new HashMap<Assets, Asset>();
	static
	{
		assets.put(SPRITESHEET, Texture.fromResource("/spritesheet.png"));
		assets.put(FONTSHEET, Texture.fromResource("/fonts.png"));
		
		Texture sheet = Assets.getTexture(SPRITESHEET);
		Texture font = Assets.getTexture(FONTSHEET);
		
		assets.put(FONT_BLACK, new Font(font.getSubTexture(0, 8 * 0, 256, 32)));
		assets.put(FONT_WHITE, new Font(font.getSubTexture(0, 8 * 12, 256, 32)));
		assets.put(FONT_NOBLE, new Font(font.getSubTexture(0, 8 * 4, 256, 32)));
		
		assets.put(RAW_PLAYER_ONE, new TextureArray(sheet.getSubTexture(0, 0, 16 * 3, 16 * 4), 3, 4, 16, 16));
		
		TextureArray player = Assets.getTextureArray(RAW_PLAYER_ONE);
		assets.put(PLAYER_ONE_UP, new SpriteAnimation(true, 12, player.getTexture(9), player.getTexture(10), player.getTexture(11)));
		assets.put(PLAYER_ONE_DOWN, new SpriteAnimation(true, 12, player.getTexture(0), player.getTexture(1), player.getTexture(2)));
		assets.put(PLAYER_ONE_LEFT, new SpriteAnimation(true, 12, player.getTexture(3), player.getTexture(4), player.getTexture(5)));
		assets.put(PLAYER_ONE_RIGHT, new SpriteAnimation(true, 12, player.getTexture(6), player.getTexture(7), player.getTexture(8)));
		assets.put(PLAYER_ONE_IDLE_UP, new SpriteAnimation(true, 0, player.getTexture(10)));
		assets.put(PLAYER_ONE_IDLE_DOWN, new SpriteAnimation(true, 0, player.getTexture(1)));
		assets.put(PLAYER_ONE_IDLE_LEFT, new SpriteAnimation(true, 0, player.getTexture(4)));
		assets.put(PLAYER_ONE_IDLE_RIGHT, new SpriteAnimation(true, 0, player.getTexture(7)));
		
		assets.put(RAW_PLAYER_TWO, new TextureArray(sheet.getSubTexture(0, 184, 16 * 3, 16 * 4), 3, 4, 16, 16));
		
		TextureArray player2 = Assets.getTextureArray(RAW_PLAYER_TWO);
		assets.put(PLAYER_TWO_UP, new SpriteAnimation(true, 12, player2.getTexture(9), player2.getTexture(10), player2.getTexture(11)));
		assets.put(PLAYER_TWO_DOWN, new SpriteAnimation(true, 12, player2.getTexture(0), player2.getTexture(1), player2.getTexture(2)));
		assets.put(PLAYER_TWO_LEFT, new SpriteAnimation(true, 12, player2.getTexture(3), player2.getTexture(4), player2.getTexture(5)));
		assets.put(PLAYER_TWO_RIGHT, new SpriteAnimation(true, 12, player2.getTexture(6), player2.getTexture(7), player2.getTexture(8)));
		assets.put(PLAYER_TWO_IDLE_UP, new SpriteAnimation(true, 0, player2.getTexture(10)));
		assets.put(PLAYER_TWO_IDLE_DOWN, new SpriteAnimation(true, 0, player2.getTexture(1)));
		assets.put(PLAYER_TWO_IDLE_LEFT, new SpriteAnimation(true, 0, player2.getTexture(4)));
		assets.put(PLAYER_TWO_IDLE_RIGHT, new SpriteAnimation(true, 0, player2.getTexture(7)));
		
		assets.put(TILE_GRASS, sheet.getSubTexture(16 * 3, 16 * 0, 16, 16));
		assets.put(TILE_GRASS_INNER_TOPLEFT, sheet.getSubTexture(16 * 3, 16 * 3, 16, 16));
		assets.put(TILE_GRASS_OUTER_BOTTOM, sheet.getSubTexture(16 * 4, 16 * 3, 16, 16));
		assets.put(TILE_GRASS_INNER_TOPRIGHT, sheet.getSubTexture(16 * 5, 16 * 3, 16, 16));
		assets.put(TILE_GRASS_OUTER_RIGHT, sheet.getSubTexture(16 * 3, 16 * 4, 16, 16));
		assets.put(TILE_GRASS_OUTER_LEFT, sheet.getSubTexture(16 * 5, 16 * 4, 16, 16));
		assets.put(TILE_GRASS_INNER_BOTTOMLEFT, sheet.getSubTexture(16 * 3, 16 * 5, 16, 16));
		assets.put(TILE_GRASS_OUTER_TOP, sheet.getSubTexture(16 * 4, 16 * 5, 16, 16));
		assets.put(TILE_GRASS_INNER_BOTTOMRIGHT, sheet.getSubTexture(16 * 5, 16 * 5, 16, 16));
		assets.put(TILE_GRASS_OUTER_TOPLEFT, sheet.getSubTexture(16 * 6, 16 * 3, 16, 16));
		assets.put(TILE_GRASS_OUTER_TOPRIGHT, sheet.getSubTexture(16 * 7, 16 * 3, 16, 16));
		assets.put(TILE_GRASS_OUTER_BOTTOMLEFT, sheet.getSubTexture(16 * 6, 16 * 4, 16, 16));
		assets.put(TILE_GRASS_OUTER_BOTTOMRIGHT, sheet.getSubTexture(16 * 7, 16 * 4, 16, 16));
		assets.put(TILE_DIRT, sheet.getSubTexture(16 * 3, 16 * 1, 16, 16));
		assets.put(TILE_WALL, sheet.getSubTexture(16 * 4, 16 * 0, 16, 16));
		assets.put(TILE_WALL_GRASS, sheet.getSubTexture(16 * 4, 16 * 1, 16, 16));
		
		assets.put(BUTTON_NORMAL, sheet.getSubTexture(0, 120, 72, 16));
		assets.put(BUTTON_HOVER, sheet.getSubTexture(0, 136, 72, 16));
		assets.put(BUTTON_PRESSED, sheet.getSubTexture(0, 152, 72, 16));
		
		assets.put(TEXT_FIELD, sheet.getSubTexture(0, 168, 112, 16));
	}
	
	public static Font getFont(Assets asset)
	{
		Asset got = assets.get(asset);
		
		if (got instanceof Font)
		{
			return (Font) got;
		}
		
		return null;
	}
	
	public static Texture getTexture(Assets asset)
	{
		Asset got = assets.get(asset);
		if (got instanceof Texture)
		{
			return (Texture) got;
		}
		return null;
	}
	
	public static TextureArray getTextureArray(Assets asset)
	{
		Asset got = assets.get(asset);
		if (got instanceof TextureArray)
		{
			return (TextureArray) got;
		}
		return null;
	}
	
	public static SpriteAnimation getSpriteAnimation(Assets asset)
	{
		Asset got = assets.get(asset);
		if (got instanceof SpriteAnimation)
		{
			return (SpriteAnimation) got;
		}
		return null;
	}
	
	public static Assets getAssetEnum(Asset asset)
	{
		for (Map.Entry<Assets, Asset> asset2 : assets.entrySet())
		{
			if (asset2.getValue().equals(asset))
			{
				return asset2.getKey();
			}
		}
		
		return null;
	}
	
	public static Assets fromOrdinal(int ordinal)
	{
		return Assets.values()[ordinal];
	}
}
