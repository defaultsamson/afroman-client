package ca.afroman.battle;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.entity.Crab;
import ca.afroman.interfaces.ITickable;
import ca.afroman.resource.Vector2DInt;

public class BattlingCrabWrapper extends BattlingEntityWrapper
{
	private DrawableAsset asset;
	private SpriteAnimation idleAsset;
	private Vector2DInt fightPos;
	
	public BattlingCrabWrapper(Crab fighting)
	{
		super(fighting);
		this.fightPos = new Vector2DInt(65, 68);
		
		asset = idleAsset = Assets.getSpriteAnimation(AssetType.CRAB_RIGHT).clone();
		idleAsset.getTickCounter().setInterval(15);
	}
	
	@Override
	public void render(Texture renderTo)
	{
		asset.render(renderTo, fightPos); // fightPos);
	}
	
	@Override
	public void tick()
	{
		if (asset instanceof ITickable)
		{
			// Ticks the IBattleables DrawableAsset
			((ITickable) asset).tick();
		}
	}
}
