package ca.afroman.level;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.entity.Tile;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.level.api.Level;
import ca.afroman.level.api.LevelType;
import ca.afroman.light.FlickeringLight;
import ca.afroman.light.PointLight;
import ca.afroman.resource.Vector2DDouble;

public class MainLevel extends Level
{
	
	public MainLevel(boolean isServerSide)
	{
		super(isServerSide, LevelType.MAIN);
		
		// Hitboxes
		new Hitbox(false, 0.0, 0.0, 16.0, 16.0).addToLevel(this);
		new Hitbox(false, 10.0, -20.0, 16.0, 16.0).addToLevel(this);
		
		if (!isServerSide)
		{
			// Tiles
			new Tile(1, Assets.getDrawableAsset(AssetType.ICON_UPDATE).clone(), new Vector2DDouble(96.0, -16.0)).addToLevel(this);
			new Tile(3, Assets.getDrawableAsset(AssetType.CAT).clone(), new Vector2DDouble(20.0, 30.0)).addToLevel(this);
			new Tile(3, Assets.getDrawableAsset(AssetType.TILE_WALL).clone(), new Vector2DDouble(10.0, -20.0)).addToLevel(this);
			
			// Lights
			new PointLight(false, new Vector2DDouble(10.0, -20.0), 20.0).addToLevel(this);
			new PointLight(false, new Vector2DDouble(24.0, 0.0), 5.0).addToLevel(this);
			new PointLight(false, new Vector2DDouble(72.0, -8.0), 10.0).addToLevel(this);
			new FlickeringLight(false, new Vector2DDouble(40.0, 24.0), 20.0, 18.0, 10).addToLevel(this);
		}
	}
}
