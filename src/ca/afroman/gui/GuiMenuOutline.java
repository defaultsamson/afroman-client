package ca.afroman.gui;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.light.FlickeringLight;
import ca.afroman.light.LightMap;
import ca.afroman.option.Options;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.resource.Vector2DInt;

public class GuiMenuOutline extends GuiScreen
{
	private SpriteAnimation afroMan;
	private SpriteAnimation player2;
	private Vector2DInt afroManPos;
	private Vector2DInt player2Pos;
	private LightMap lightmap;
	private FlickeringLight light;
	
	public GuiMenuOutline(GuiScreen parent, boolean hasLightmap, boolean hasCharacters)
	{
		super(parent);
		
		afroMan = null;
		player2 = null;
		lightmap = null;
		light = null;
		
		if (hasCharacters)
		{
			afroMan = Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_DOWN);
			afroManPos = new Vector2DInt((ClientGame.WIDTH / 2) - 20, 30);
			player2 = Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_DOWN);
			player2Pos = new Vector2DInt((ClientGame.WIDTH / 2) + 4, 30);
		}
		
		if (hasLightmap)
		{
			lightmap = new LightMap(ClientGame.WIDTH, ClientGame.HEIGHT, LightMap.DEFAULT_AMBIENT);
		}
		
		if (hasCharacters && hasLightmap)
		{
			light = new FlickeringLight(true, new Vector2DDouble(ClientGame.WIDTH / 2, 38), 60, 62, 5);
		}
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		super.drawScreen(renderTo);
		
		if (afroMan != null)
		{
			renderTo.draw(afroMan.getDisplayedTexture(), afroManPos);
		}
		if (player2 != null)
		{
			renderTo.draw(player2.getDisplayedTexture(), player2Pos);
		}
		
		if (Options.instance().isLightingOn())
		{
			if (lightmap != null)
			{
				lightmap.clear();
				
				if (light != null) light.renderCentered(lightmap);
				
				lightmap.patch();
				
				renderTo.draw(lightmap, LightMap.PATCH_POSITION);
			}
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (Options.instance().isLightingOn())
		{
			if (light != null)
			{
				light.tick();
			}
		}
		
		if (afroMan != null)
		{
			afroMan.tick();
		}
		
		if (player2 != null)
		{
			player2.tick();
		}
	}
}
