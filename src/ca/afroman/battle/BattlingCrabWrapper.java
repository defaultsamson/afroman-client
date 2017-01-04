package ca.afroman.battle;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.entity.Crab;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.FlickeringLight;
import ca.afroman.light.LightMap;
import ca.afroman.resource.Vector2DInt;

public class BattlingCrabWrapper extends BattlingEntityWrapper
{
	private FlickeringLight light;
	private DrawableAsset asset;
	private SpriteAnimation idleAsset;
	private Vector2DInt fightPos;
	
	public BattlingCrabWrapper(Crab fighting)
	{
		super(fighting);
		this.fightPos = new Vector2DInt(40, 81);
		
		asset = idleAsset = Assets.getSpriteAnimation(AssetType.CRAB_RIGHT).clone();
		idleAsset.getTickCounter().setInterval(15);
		light = new FlickeringLight(true, fightPos.toVector2DDouble(), 55, 45, 4);
	}
	
	@Override
	public void render(Texture renderTo, LightMap map)
	{
		asset.render(renderTo, fightPos); // fightPos);
		light.renderCentered(map);
		light.renderCentered(map);
	}
	
	@Override
	public void tick()
	{
		if (asset instanceof ITickable)
		{
			// Ticks the IBattleables DrawableAsset
			((ITickable) asset).tick();
		}
		
		light.setPosition(fightPos.getX() + 8, fightPos.getY());
		light.tick();
	}
}
