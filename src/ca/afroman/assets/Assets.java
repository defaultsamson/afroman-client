package ca.afroman.assets;

import java.util.ArrayList;
import java.util.List;

public class Assets
{
	public static List<AssetArray> assetArrays = new ArrayList<AssetArray>();
	
	public static List<Asset> assets = new ArrayList<Asset>();
	static
	{
		assets.add(Texture.fromResource(AssetType.SPRITESHEET, "/spritesheet.png"));
		assets.add(Texture.fromResource(AssetType.FONTSHEET, "/fonts.png"));
		
		Texture filter = Texture.fromResource(AssetType.FILTER, "/filter_opaque.png");
		filter.setFromGreyscaleToAlphaMask();
		assets.add(filter);
		
		Texture sheet = Assets.getTexture(AssetType.SPRITESHEET);
		Texture font = Assets.getTexture(AssetType.FONTSHEET);
		
		assetArrays.add(new Font(AssetType.FONT_BLACK, font.getSubTexture(AssetType.FONT_BLACK, 0, 8 * 0, 256, 32)));
		assetArrays.add(new Font(AssetType.FONT_WHITE, font.getSubTexture(AssetType.FONT_WHITE, 0, 8 * 12, 256, 32)));
		assetArrays.add(new Font(AssetType.FONT_NOBLE, font.getSubTexture(AssetType.FONT_NOBLE, 0, 8 * 4, 256, 32)));
		
		assetArrays.add(new AssetArray(AssetType.RAW_PLAYER_ONE, sheet.getSubTexture(AssetType.RAW_PLAYER_ONE, 0, 0, 16 * 3, 16 * 4).toTextureArray(3, 4)));
		
		AssetArray player = Assets.getAssetArray(AssetType.RAW_PLAYER_ONE);
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_UP, true, 12, (Texture) player.getAsset(9), (Texture) player.getAsset(10), (Texture) player.getAsset(11)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_DOWN, true, 12, (Texture) player.getAsset(0), (Texture) player.getAsset(1), (Texture) player.getAsset(2)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_LEFT, true, 12, (Texture) player.getAsset(3), (Texture) player.getAsset(4), (Texture) player.getAsset(5)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_RIGHT, true, 12, (Texture) player.getAsset(6), (Texture) player.getAsset(7), (Texture) player.getAsset(8)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_IDLE_UP, true, 0, (Texture) player.getAsset(10)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_IDLE_DOWN, true, 0, (Texture) player.getAsset(1)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_IDLE_LEFT, true, 0, (Texture) player.getAsset(4)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_ONE_IDLE_RIGHT, true, 0, (Texture) player.getAsset(7)));
		
		assetArrays.add(new AssetArray(AssetType.RAW_PLAYER_TWO, sheet.getSubTexture(AssetType.RAW_PLAYER_TWO, 0, 184, 16 * 3, 16 * 4).toTextureArray(3, 4)));
		
		AssetArray player2 = Assets.getAssetArray(AssetType.RAW_PLAYER_TWO);
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_UP, true, 12, (Texture) player2.getAsset(9), (Texture) player2.getAsset(10), (Texture) player2.getAsset(11)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_DOWN, true, 12, (Texture) player2.getAsset(0), (Texture) player2.getAsset(1), (Texture) player2.getAsset(2)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_LEFT, true, 12, (Texture) player2.getAsset(3), (Texture) player2.getAsset(4), (Texture) player2.getAsset(5)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_RIGHT, true, 12, (Texture) player2.getAsset(6), (Texture) player2.getAsset(7), (Texture) player2.getAsset(8)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_IDLE_UP, true, 0, (Texture) player2.getAsset(10)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_IDLE_DOWN, true, 0, (Texture) player2.getAsset(1)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_IDLE_LEFT, true, 0, (Texture) player2.getAsset(4)));
		assets.add(new SpriteAnimation(AssetType.PLAYER_TWO_IDLE_RIGHT, true, 0, (Texture) player2.getAsset(7)));
		
		assets.add(sheet.getSubTexture(AssetType.TILE_GRASS, 16 * 3, 16 * 0, 16, 16));
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
		
		assets.add(sheet.getSubTexture(AssetType.TILE_DIRT, 16 * 3, 16 * 1, 16, 16));
		
		assets.add(sheet.getSubTexture(AssetType.TILE_WATER, 16 * 5, 16 * 0, 16, 16));
		
		assets.add(sheet.getSubTexture(AssetType.TILE_WALL, 16 * 4, 16 * 0, 16, 16));
		assets.add(sheet.getSubTexture(AssetType.TILE_WALL_GRASS, 16 * 4, 16 * 1, 16, 16));
		
		assets.add(sheet.getSubTexture(AssetType.BUTTON_NORMAL, 0, 120, 72, 16));
		assets.add(sheet.getSubTexture(AssetType.BUTTON_HOVER, 0, 136, 72, 16));
		assets.add(sheet.getSubTexture(AssetType.BUTTON_PRESSED, 0, 152, 72, 16));
		
		assets.add(sheet.getSubTexture(AssetType.TEXT_FIELD, 0, 168, 112, 16));
	}
	
	public static Font getFont(AssetType type)
	{
		Asset asset = getAssetArray(type);
		
		if (asset != null && asset.assetType() == type && asset instanceof Font)
		{
			return (Font) asset;
		}
		
		return null;
	}
	
	public static Texture getTexture(AssetType type)
	{
		Asset asset = getAsset(type);
		
		if (asset != null && asset.assetType() == type && asset instanceof Texture)
		{
			return (Texture) asset;
		}
		
		return null;
	}
	
	public static SpriteAnimation getSpriteAnimation(AssetType type)
	{
		Asset asset = getAsset(type);
		
		if (asset != null && asset.assetType() == type && asset instanceof SpriteAnimation)
		{
			return (SpriteAnimation) asset;
		}
		
		return null;
	}
	
	public static AssetArray getAssetArray(AssetType type)
	{
		for (AssetArray asset : assetArrays)
		{
			if (asset.assetType() == type)
			{
				return asset;
			}
		}
		return null;
	}
	
	public static Asset getAsset(AssetType type)
	{
		for (Asset asset : assets)
		{
			if (asset.assetType() == type)
			{
				return asset;
			}
		}
		return null;
	}
}
