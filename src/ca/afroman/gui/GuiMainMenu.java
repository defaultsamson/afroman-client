package ca.afroman.gui;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMap;
import ca.afroman.option.Options;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.resource.Vector2DInt;
import ca.afroman.util.UpdateUtil;
import ca.afroman.util.VersionUtil;

public class GuiMainMenu extends GuiScreen
{
	private SpriteAnimation afroMan;
	private SpriteAnimation player2;
	private LightMap lightmap;
	private FlickeringLight light;
	
	public GuiMainMenu()
	{
		super(null);
		
		afroMan = Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_DOWN);
		player2 = Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_DOWN);
		
		lightmap = new LightMap(ClientGame.WIDTH, ClientGame.HEIGHT, LightMap.DEFAULT_AMBIENT);
		light = new FlickeringLight(false, -1, new Vector2DDouble(ClientGame.WIDTH / 2, 38), 60, 62, 5);
		
		addButton(new GuiTextButton(this, 1, (ClientGame.WIDTH / 2) - (72 / 2), 58 + (24 * 0), 72, blackFont, "Join"));
		addButton(new GuiTextButton(this, 2, (ClientGame.WIDTH / 2) - (72 / 2), 58 + (24 * 1), 72, blackFont, "Host"));
		addButton(new GuiTextButton(this, 0, (ClientGame.WIDTH / 2) - (72 / 2), 58 + (24 * 2), 72, blackFont, "Quit"));
		addButton(new GuiIconButton(this, 3, (ClientGame.WIDTH / 2) - (72 / 2) - 16 - 4, 58 + (24 * 2), 16, Assets.getTexture(AssetType.ICON_REFRESH)));
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		renderTo.draw(afroMan.getCurrentFrame(), new Vector2DInt((ClientGame.WIDTH / 2) - 20, 30));
		renderTo.draw(player2.getCurrentFrame(), new Vector2DInt((ClientGame.WIDTH / 2) + 4, 30));
		
		if (Options.instance().isLightingOn())
		{
			lightmap.clear();
			light.renderCentered(lightmap);
			lightmap.patch();
			
			renderTo.draw(lightmap, LightMap.PATCH_POSITION);
		}
		
		nobleFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2, 15), "Cancer: The Adventures of Afro Man");
	}
	
	@Override
	public void keyTyped()
	{
		
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
			case 0: // Quit game
				ClientGame.instance().quit();
				break;
			case 3: // Check for updates
				if (UpdateUtil.updateQuery())
				{
					new GuiYesNoPrompt(this, 30, "Update found (" + VersionUtil.toString(UpdateUtil.serverVersion) + ")", "Would you like to update?");
				}
				else
				{
					new GuiClickNotification(this, "No updates", "found");
				}
				break;
			case 30: // Confirm update
				new GuiYesNoPrompt(this, 32, "Doing this will close the game,", "Would you like to continue?");
				break;
			case 31: // No, don't update
				break;
			case 32:// Yes, update
				ClientGame.instance().quit(true);
				break;
			case 33: // No, don't update
				break;
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (Options.instance().isLightingOn())
		{
			light.tick();
			afroMan.tick();
			player2.tick();
		}
	}
}
