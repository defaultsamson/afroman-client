package ca.pixel.game.assets;

import java.util.HashMap;
import java.util.Map;

public enum Assets
{
	SPRITESHEET,
	FONTSHEET,
	
	FONT_NORMAL,
	FONT_WHITE,
	
	RAW_PLAYER,
	PLAYER_UP,
	PLAYER_DOWN,
	PLAYER_LEFT,
	PLAYER_RIGHT,
	PLAYER_IDLE_UP,
	PLAYER_IDLE_DOWN,
	PLAYER_IDLE_LEFT,
	PLAYER_IDLE_RIGHT,
	
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
	TILE_WALL_GRASS;
	
	public static HashMap<Assets, Asset> assets = new HashMap<Assets, Asset>();
	static
	{
		assets.put(SPRITESHEET, Texture.fromResource("/spritesheet.png"));
		assets.put(FONTSHEET, Texture.fromResource("/fonts.png"));
		
		Texture sheet = Assets.getTexture(SPRITESHEET);
		Texture font = Assets.getTexture(FONTSHEET);
		
		assets.put(FONT_NORMAL, new Font(font.getSubTexture(0, 8 * 0, 256, 32)));
		assets.put(FONT_WHITE, new Font(font.getSubTexture(0, 8 * 12, 256, 32)));
		
		assets.put(RAW_PLAYER, new TextureArray(sheet.getSubTexture(0, 0, 16 * 3, 16 * 4), 3, 4, 16, 16));
		
		TextureArray player = Assets.getTextureArray(RAW_PLAYER);
		assets.put(PLAYER_UP, new SpriteAnimation(true, 12, player.getTexture(9), player.getTexture(10), player.getTexture(11)));
		assets.put(PLAYER_DOWN, new SpriteAnimation(true, 12, player.getTexture(0), player.getTexture(1), player.getTexture(2)));
		assets.put(PLAYER_LEFT, new SpriteAnimation(true, 12, player.getTexture(3), player.getTexture(4), player.getTexture(5)));
		assets.put(PLAYER_RIGHT, new SpriteAnimation(true, 12, player.getTexture(6), player.getTexture(7), player.getTexture(8)));
		assets.put(PLAYER_IDLE_UP, new SpriteAnimation(true, 0, player.getTexture(10)));
		assets.put(PLAYER_IDLE_DOWN, new SpriteAnimation(true, 0, player.getTexture(1)));
		assets.put(PLAYER_IDLE_LEFT, new SpriteAnimation(true, 0, player.getTexture(4)));
		assets.put(PLAYER_IDLE_RIGHT, new SpriteAnimation(true, 0, player.getTexture(7)));
		
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
		for (Assets asset : Assets.values())
		{
			if (asset.ordinal() == ordinal) return asset;
		}
		
		return null;
	}
}
