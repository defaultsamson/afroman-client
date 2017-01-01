package ca.afroman.battle;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.LightMap;

public class BattleScene implements ITickable
{
	private static final Texture bg = Assets.getTexture(AssetType.BATTLE_RUINS_BG);
	
	private BattlingEntityWrapper entity;
	private BattlingEntityWrapper player1;
	private BattlingEntityWrapper player2;
	
	public BattleScene(BattlingEntityWrapper entity, BattlingEntityWrapper player1, BattlingEntityWrapper player2)
	{
		this.entity = entity;
		this.player1 = player1;
		this.player2 = player2;
	}
	
	public void render(Texture renderTo)
	{
		bg.render(renderTo, LightMap.PATCH_POSITION);
		
		entity.render(renderTo);
		if (player1 != null) player1.render(renderTo);
		if (player2 != null) player2.render(renderTo);
	}
	
	@Override
	public void tick()
	{
		// TODO controls and shite
		// Jump
		if (ClientGame.instance().input().up.isPressed())
		{
			
		}
		// Duck
		if (ClientGame.instance().input().down.isPressed())
		{
			
		}
		
		entity.tick();
		if (player1 != null) player1.tick();
		if (player2 != null) player2.tick();
	}
}
