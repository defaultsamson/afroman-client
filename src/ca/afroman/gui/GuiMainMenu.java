package ca.afroman.gui;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMap;

public class GuiMainMenu extends GuiScreen
{
	private SpriteAnimation afroMan;
	private SpriteAnimation player2;
	private LightMap lightmap;
	private FlickeringLight light;
	
	public GuiMainMenu()
	{
		super(null);
	}
	
	@Override
	public void init()
	{
		afroMan = Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_DOWN);
		player2 = Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_DOWN);
		
		lightmap = new LightMap(ClientGame.WIDTH, ClientGame.HEIGHT, LightMap.DEFAULT_AMBIENT);
		light = new FlickeringLight(-1, null, ClientGame.WIDTH / 2, 38, 60, 62, 5);
		
		buttons.add(new GuiTextButton(this, 1, (ClientGame.WIDTH / 2) - (72 / 2), 58 + (24 * 0), 72, blackFont, "Join"));
		buttons.add(new GuiTextButton(this, 2, (ClientGame.WIDTH / 2) - (72 / 2), 58 + (24 * 1), 72, blackFont, "Host"));
		buttons.add(new GuiTextButton(this, 0, (ClientGame.WIDTH / 2) - (72 / 2), 58 + (24 * 2), 72, blackFont, "Quit"));
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		renderTo.draw(afroMan.getCurrentFrame(), (ClientGame.WIDTH / 2) - 20, 30);
		renderTo.draw(player2.getCurrentFrame(), (ClientGame.WIDTH / 2) + 4, 30);
		
		if (ClientGame.instance().isLightingOn())
		{
			lightmap.clear();
			light.renderCentered(lightmap);
			lightmap.patch();
			
			renderTo.draw(lightmap, 0, 0);
		}
		
		nobleFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 15, "Cancer: The Adventures of Afro Man");
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (ClientGame.instance().isLightingOn())
		{
			light.tick();
			afroMan.tick();
			player2.tick();
		}
	}
	
	@Override
	public void pressAction(int buttonID)
	{
		
	}
	
	@Override
	public void releaseAction(int buttonID)
	{
		switch (buttonID)
		{
			case 2: // Host Server
				ClientGame.instance().setCurrentScreen(new GuiHostServer(this));
				break;
			case 1: // Join Server
				ClientGame.instance().setCurrentScreen(new GuiJoinServer(this));
				break;
			case 0: // Quite game
				ClientGame.instance().stopThis();
				Assets.dispose();
				System.exit(0);
				break;
		}
	}
	
	@Override
	public void keyTyped()
	{
		
	}
}
