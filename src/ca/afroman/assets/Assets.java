package ca.afroman.assets;

import java.util.ArrayList;
import java.util.List;

public class Assets
{
	private static List<AssetArray> assetArrays = new ArrayList<AssetArray>();
	
	private static List<Asset> assets = new ArrayList<Asset>();
	
	public static void load()
	{
		Texture sheet = Texture.fromResource(AssetType.INVALID, "spritesheet.png");
		Texture font = Texture.fromResource(AssetType.INVALID, "fonts.png");
		Texture buttons = Texture.fromResource(AssetType.INVALID, "buttons.png");
		
		Texture filter = Texture.fromResource(AssetType.FILTER, "filter_opaque.png");
		filter.setFromGreyscaleToAlphaMask();
		assets.add(filter);
		
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
		
		assets.add(new SpriteAnimation(AssetType.TILE_AURA, 12, sheet.getSubTexture(AssetType.TILE_AURA, 16 * 4, 16 * 2, 16 * 6, 16).toTextureArray(6, 1)));
		
		assets.add(buttons.getSubTexture(AssetType.BUTTON_NORMAL, 0, 0, 3, 16));
		assets.add(buttons.getSubTexture(AssetType.BUTTON_HOVER, 3, 0, 3, 16));
		assets.add(buttons.getSubTexture(AssetType.BUTTON_PRESSED, 6, 0, 3, 16));
		
		assets.add(buttons.getSubTexture(AssetType.TEXT_FIELD, 9, 0, 3, 16));
		assets.add(buttons.getSubTexture(AssetType.TEXT_FIELD_CLEAR, 12, 0, 3, 16));
		
		assets.add(AudioClip.fromResource(AssetType.AUDIO_BUTTON_PUSH, "but_down.wav"));
		assets.add(AudioClip.fromResource(AssetType.AUDIO_BUTTON_RELEASE, "but_up.wav"));
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
	
	public static Texture getTexture(AssetType type)
	{
		Asset asset = getAsset(type);
		
		if (asset != null && asset.getAssetType() == type && asset instanceof Texture)
		{
			return (Texture) asset;
		}
		
		return null;
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
	
	public static SpriteAnimation getSpriteAnimation(AssetType type)
	{
		Asset asset = getAsset(type);
		
		if (asset != null && asset.getAssetType() == type && asset instanceof SpriteAnimation)
		{
			return (SpriteAnimation) asset;
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
	
	public static Asset getAsset(AssetType type)
	{
		if (type == null) return null;
		
		for (Asset asset : assets)
		{
			if (asset.getAssetType() == type)
			{
				return asset;
			}
		}
		return null;
	}
}
