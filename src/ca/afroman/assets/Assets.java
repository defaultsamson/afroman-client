package ca.afroman.assets;

import java.util.HashMap;
import java.util.Map.Entry;

import ca.afroman.server.AssetType;

public class Assets
{
	public static HashMap<AssetType, Asset> assets = new HashMap<AssetType, Asset>();
	static
	{
		assets.put(AssetType.SPRITESHEET, Texture.fromResource("/spritesheet.png"));
		assets.put(AssetType.FONTSHEET, Texture.fromResource("/fonts.png"));
		
		Texture sheet = Assets.getTexture(AssetType.SPRITESHEET);
		Texture font = Assets.getTexture(AssetType.FONTSHEET);
		
		assets.put(AssetType.FONT_BLACK, new Font(font.getSubTexture(0, 8 * 0, 256, 32)));
		assets.put(AssetType.FONT_WHITE, new Font(font.getSubTexture(0, 8 * 12, 256, 32)));
		assets.put(AssetType.FONT_NOBLE, new Font(font.getSubTexture(0, 8 * 4, 256, 32)));
		
		assets.put(AssetType.RAW_PLAYER_ONE, new TextureArray(sheet.getSubTexture(0, 0, 16 * 3, 16 * 4), 3, 4, 16, 16));
		
		TextureArray player = Assets.getTextureArray(AssetType.RAW_PLAYER_ONE);
		assets.put(AssetType.PLAYER_ONE_UP, new SpriteAnimation(true, 12, player.getTexture(9), player.getTexture(10), player.getTexture(11)));
		assets.put(AssetType.PLAYER_ONE_DOWN, new SpriteAnimation(true, 12, player.getTexture(0), player.getTexture(1), player.getTexture(2)));
		assets.put(AssetType.PLAYER_ONE_LEFT, new SpriteAnimation(true, 12, player.getTexture(3), player.getTexture(4), player.getTexture(5)));
		assets.put(AssetType.PLAYER_ONE_RIGHT, new SpriteAnimation(true, 12, player.getTexture(6), player.getTexture(7), player.getTexture(8)));
		assets.put(AssetType.PLAYER_ONE_IDLE_UP, new SpriteAnimation(true, 0, player.getTexture(10)));
		assets.put(AssetType.PLAYER_ONE_IDLE_DOWN, new SpriteAnimation(true, 0, player.getTexture(1)));
		assets.put(AssetType.PLAYER_ONE_IDLE_LEFT, new SpriteAnimation(true, 0, player.getTexture(4)));
		assets.put(AssetType.PLAYER_ONE_IDLE_RIGHT, new SpriteAnimation(true, 0, player.getTexture(7)));
		
		assets.put(AssetType.RAW_PLAYER_TWO, new TextureArray(sheet.getSubTexture(0, 184, 16 * 3, 16 * 4), 3, 4, 16, 16));
		
		TextureArray player2 = Assets.getTextureArray(AssetType.RAW_PLAYER_TWO);
		assets.put(AssetType.PLAYER_TWO_UP, new SpriteAnimation(true, 12, player2.getTexture(9), player2.getTexture(10), player2.getTexture(11)));
		assets.put(AssetType.PLAYER_TWO_DOWN, new SpriteAnimation(true, 12, player2.getTexture(0), player2.getTexture(1), player2.getTexture(2)));
		assets.put(AssetType.PLAYER_TWO_LEFT, new SpriteAnimation(true, 12, player2.getTexture(3), player2.getTexture(4), player2.getTexture(5)));
		assets.put(AssetType.PLAYER_TWO_RIGHT, new SpriteAnimation(true, 12, player2.getTexture(6), player2.getTexture(7), player2.getTexture(8)));
		assets.put(AssetType.PLAYER_TWO_IDLE_UP, new SpriteAnimation(true, 0, player2.getTexture(10)));
		assets.put(AssetType.PLAYER_TWO_IDLE_DOWN, new SpriteAnimation(true, 0, player2.getTexture(1)));
		assets.put(AssetType.PLAYER_TWO_IDLE_LEFT, new SpriteAnimation(true, 0, player2.getTexture(4)));
		assets.put(AssetType.PLAYER_TWO_IDLE_RIGHT, new SpriteAnimation(true, 0, player2.getTexture(7)));
		
		assets.put(AssetType.TILE_GRASS, sheet.getSubTexture(16 * 3, 16 * 0, 16, 16));
		assets.put(AssetType.TILE_GRASS_INNER_TOPLEFT, sheet.getSubTexture(16 * 3, 16 * 3, 16, 16));
		assets.put(AssetType.TILE_GRASS_OUTER_BOTTOM, sheet.getSubTexture(16 * 4, 16 * 3, 16, 16));
		assets.put(AssetType.TILE_GRASS_INNER_TOPRIGHT, sheet.getSubTexture(16 * 5, 16 * 3, 16, 16));
		assets.put(AssetType.TILE_GRASS_OUTER_RIGHT, sheet.getSubTexture(16 * 3, 16 * 4, 16, 16));
		assets.put(AssetType.TILE_GRASS_OUTER_LEFT, sheet.getSubTexture(16 * 5, 16 * 4, 16, 16));
		assets.put(AssetType.TILE_GRASS_INNER_BOTTOMLEFT, sheet.getSubTexture(16 * 3, 16 * 5, 16, 16));
		assets.put(AssetType.TILE_GRASS_OUTER_TOP, sheet.getSubTexture(16 * 4, 16 * 5, 16, 16));
		assets.put(AssetType.TILE_GRASS_INNER_BOTTOMRIGHT, sheet.getSubTexture(16 * 5, 16 * 5, 16, 16));
		assets.put(AssetType.TILE_GRASS_OUTER_TOPLEFT, sheet.getSubTexture(16 * 6, 16 * 3, 16, 16));
		assets.put(AssetType.TILE_GRASS_OUTER_TOPRIGHT, sheet.getSubTexture(16 * 7, 16 * 3, 16, 16));
		assets.put(AssetType.TILE_GRASS_OUTER_BOTTOMLEFT, sheet.getSubTexture(16 * 6, 16 * 4, 16, 16));
		assets.put(AssetType.TILE_GRASS_OUTER_BOTTOMRIGHT, sheet.getSubTexture(16 * 7, 16 * 4, 16, 16));
		assets.put(AssetType.TILE_DIRT, sheet.getSubTexture(16 * 3, 16 * 1, 16, 16));
		assets.put(AssetType.TILE_WALL, sheet.getSubTexture(16 * 4, 16 * 0, 16, 16));
		assets.put(AssetType.TILE_WALL_GRASS, sheet.getSubTexture(16 * 4, 16 * 1, 16, 16));
		
		assets.put(AssetType.BUTTON_NORMAL, sheet.getSubTexture(0, 120, 72, 16));
		assets.put(AssetType.BUTTON_HOVER, sheet.getSubTexture(0, 136, 72, 16));
		assets.put(AssetType.BUTTON_PRESSED, sheet.getSubTexture(0, 152, 72, 16));
		
		assets.put(AssetType.TEXT_FIELD, sheet.getSubTexture(0, 168, 112, 16));
	}
	
	public static Font getFont(AssetType asset)
	{
		Asset got = assets.get(asset);
		
		if (got instanceof Font)
		{
			return (Font) got;
		}
		
		return null;
	}
	
	public static Texture getTexture(AssetType asset)
	{
		Asset got = assets.get(asset);
		if (got instanceof Texture)
		{
			return (Texture) got;
		}
		return null;
	}
	
	public static TextureArray getTextureArray(AssetType asset)
	{
		Asset got = assets.get(asset);
		if (got instanceof TextureArray)
		{
			return (TextureArray) got;
		}
		return null;
	}
	
	public static SpriteAnimation getSpriteAnimation(AssetType asset)
	{
		Asset got = assets.get(asset);
		if (got instanceof SpriteAnimation)
		{
			return (SpriteAnimation) got;
		}
		return null;
	}
	
	public static AssetType getAssetEnum(Asset asset)
	{
		for (Entry<AssetType, Asset> asset2 : assets.entrySet())
		{
			if (asset2.getValue().equals(asset))
			{
				return asset2.getKey();
			}
		}
		
		return null;
	}
}
