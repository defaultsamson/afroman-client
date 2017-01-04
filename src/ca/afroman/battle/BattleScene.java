package ca.afroman.battle;

import java.awt.Color;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.interfaces.ITickable;
import ca.afroman.light.LightMap;
import ca.afroman.resource.ServerClientObject;

public class BattleScene extends ServerClientObject implements ITickable
{
	private static final Texture bg = Assets.getTexture(AssetType.BATTLE_RUINS_BG);
	
	private BattlingEntityWrapper entity;
	private BattlingPlayerWrapper player1;
	private BattlingPlayerWrapper player2;
	
	private LightMap lightmap;
	
	public BattleScene(BattlingEntityWrapper entity, BattlingPlayerWrapper player1, BattlingPlayerWrapper player2)
	{
		super(entity.getFightingEnemy().isServerSide());
		
		this.entity = entity;
		this.player1 = player1;
		this.player2 = player2;
		
		if (!isServerSide())
		{
			ClientGame.instance().playMusic(Assets.getAudioClip(AssetType.AUDIO_BATTLE_MUSIC), true);
		}
		
		lightmap = new LightMap(ClientGame.WIDTH, ClientGame.HEIGHT, new Color(0F, 0F, 0F, 0.5F));
	}
	
	public void render(Texture renderTo)
	{
		bg.render(renderTo, 0, 0);
		
		lightmap.clear();
		
		entity.render(renderTo, lightmap);
		if (player1 != null) player1.render(renderTo, lightmap);
		if (player2 != null) player2.render(renderTo, lightmap);
		
		lightmap.patch();
		lightmap.render(renderTo, 0, 0);
	}
	
	@Override
	public void tick()
	{
		entity.tick();
		if (player1 != null) player1.tick();
		if (player2 != null) player2.tick();
	}
}
