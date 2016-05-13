package ca.afroman.assets;

import java.util.HashMap;
import java.util.Map.Entry;

public class Assets
{
	public static HashMap<AssetType, AssetArray> assetArrays = new HashMap<AssetType, AssetArray>();
	
	public static HashMap<AssetType, Asset> assets = new HashMap<AssetType, Asset>();
	static
	{
		assets.put(AssetType.SPRITESHEET, Texture.fromResource("/spritesheet.png"));
		assets.put(AssetType.FONTSHEET, Texture.fromResource("/fonts.png"));
		
		Texture filter = Texture.fromResource("/filter_opaque.png");
		filter.setFromGreyscaleToAlphaMask();
		assets.put(AssetType.FILTER, filter);
		
		Texture sheet = Assets.getTexture(AssetType.SPRITESHEET);
		Texture font = Assets.getTexture(AssetType.FONTSHEET);
		
		assetArrays.put(AssetType.FONT_BLACK, new Font(font.getSubTexture(0, 8 * 0, 256, 32)));
		assetArrays.put(AssetType.FONT_WHITE, new Font(font.getSubTexture(0, 8 * 12, 256, 32)));
		assetArrays.put(AssetType.FONT_NOBLE, new Font(font.getSubTexture(0, 8 * 4, 256, 32)));
		
		assetArrays.put(AssetType.RAW_PLAYER_ONE, new AssetArray(sheet.getSubTexture(0, 0, 16 * 3, 16 * 4).toTextureArray(3, 4)));
		
		AssetArray player = Assets.getAssetArray(AssetType.RAW_PLAYER_ONE);
		assets.put(AssetType.PLAYER_ONE_UP, new SpriteAnimation(true, 12, (Texture) player.getAsset(9), (Texture) player.getAsset(10), (Texture) player.getAsset(11)));
		assets.put(AssetType.PLAYER_ONE_DOWN, new SpriteAnimation(true, 12, (Texture) player.getAsset(0), (Texture) player.getAsset(1), (Texture) player.getAsset(2)));
		assets.put(AssetType.PLAYER_ONE_LEFT, new SpriteAnimation(true, 12, (Texture) player.getAsset(3), (Texture) player.getAsset(4), (Texture) player.getAsset(5)));
		assets.put(AssetType.PLAYER_ONE_RIGHT, new SpriteAnimation(true, 12, (Texture) player.getAsset(6), (Texture) player.getAsset(7), (Texture) player.getAsset(8)));
		assets.put(AssetType.PLAYER_ONE_IDLE_UP, new SpriteAnimation(true, 0, (Texture) player.getAsset(10)));
		assets.put(AssetType.PLAYER_ONE_IDLE_DOWN, new SpriteAnimation(true, 0, (Texture) player.getAsset(1)));
		assets.put(AssetType.PLAYER_ONE_IDLE_LEFT, new SpriteAnimation(true, 0, (Texture) player.getAsset(4)));
		assets.put(AssetType.PLAYER_ONE_IDLE_RIGHT, new SpriteAnimation(true, 0, (Texture) player.getAsset(7)));
		
		assetArrays.put(AssetType.RAW_PLAYER_TWO, new AssetArray(sheet.getSubTexture(0, 184, 16 * 3, 16 * 4).toTextureArray(3, 4)));
		
		AssetArray player2 = Assets.getAssetArray(AssetType.RAW_PLAYER_TWO);
		assets.put(AssetType.PLAYER_TWO_UP, new SpriteAnimation(true, 12, (Texture) player2.getAsset(9), (Texture) player2.getAsset(10), (Texture) player2.getAsset(11)));
		assets.put(AssetType.PLAYER_TWO_DOWN, new SpriteAnimation(true, 12, (Texture) player2.getAsset(0), (Texture) player2.getAsset(1), (Texture) player2.getAsset(2)));
		assets.put(AssetType.PLAYER_TWO_LEFT, new SpriteAnimation(true, 12, (Texture) player2.getAsset(3), (Texture) player2.getAsset(4), (Texture) player2.getAsset(5)));
		assets.put(AssetType.PLAYER_TWO_RIGHT, new SpriteAnimation(true, 12, (Texture) player2.getAsset(6), (Texture) player2.getAsset(7), (Texture) player2.getAsset(8)));
		assets.put(AssetType.PLAYER_TWO_IDLE_UP, new SpriteAnimation(true, 0, (Texture) player2.getAsset(10)));
		assets.put(AssetType.PLAYER_TWO_IDLE_DOWN, new SpriteAnimation(true, 0, (Texture) player2.getAsset(1)));
		assets.put(AssetType.PLAYER_TWO_IDLE_LEFT, new SpriteAnimation(true, 0, (Texture) player2.getAsset(4)));
		assets.put(AssetType.PLAYER_TWO_IDLE_RIGHT, new SpriteAnimation(true, 0, (Texture) player2.getAsset(7)));
		
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
		
		assets.put(AssetType.TILE_WATER, sheet.getSubTexture(16 * 5, 16 * 0, 16, 16));
		
		assets.put(AssetType.TILE_WALL, sheet.getSubTexture(16 * 4, 16 * 0, 16, 16));
		assets.put(AssetType.TILE_WALL_GRASS, sheet.getSubTexture(16 * 4, 16 * 1, 16, 16));
		
		assets.put(AssetType.BUTTON_NORMAL, sheet.getSubTexture(0, 120, 72, 16));
		assets.put(AssetType.BUTTON_HOVER, sheet.getSubTexture(0, 136, 72, 16));
		assets.put(AssetType.BUTTON_PRESSED, sheet.getSubTexture(0, 152, 72, 16));
		
		assets.put(AssetType.TEXT_FIELD, sheet.getSubTexture(0, 168, 112, 16));
	}
	
	public static Font getFont(AssetType asset)
	{
		AssetArray got = assetArrays.get(asset);
		
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
	
	public static AssetArray getAssetArray(AssetType asset)
	{
		AssetArray got = assetArrays.get(asset);
		if (got instanceof AssetArray)
		{
			return got;
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
