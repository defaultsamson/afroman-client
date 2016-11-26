package ca.afroman.level;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.entity.api.DrawableEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.level.api.Level;
import ca.afroman.level.api.LevelType;
import ca.afroman.resource.Vector2DDouble;

public class MainLevel extends Level
{
	
	public MainLevel(boolean isServerSide)
	{
		super(isServerSide, LevelType.MAIN);
		
		if (!isServerSide)
		{
			new DrawableEntity(isServerSide, Entity.getIDCounter().getNext(), Assets.getTexture(AssetType.CAT).clone(), new Vector2DDouble(20, 30)).addTileToLevel(this, 3);
			new DrawableEntity(isServerSide, Entity.getIDCounter().getNext(), Assets.getTexture(AssetType.TILE_WALL).clone(), new Vector2DDouble(10, -20)).addTileToLevel(this, 3);
		}
	}
}
