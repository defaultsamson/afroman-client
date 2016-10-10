package ca.afroman.gui;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.client.ExitGameReason;
import ca.afroman.resource.Vector2DInt;
import ca.afroman.server.ServerGame;

public class GuiInGameMenu extends GuiScreen
{
	public GuiInGameMenu(GuiScreen parent)
	{
		super(parent);
		
		addButton(new GuiTextButton(this, 0, (ClientGame.WIDTH / 2) - (72 / 2), 58 + (24 * 0), 72, blackFont, "Resume"));
		
		if (ClientGame.instance().isHostingServer())
		{
			addButton(new GuiTextButton(this, 1, (ClientGame.WIDTH / 2) - (72 / 2), 58 + (24 * 1), 72, blackFont, "Stop Server"));
		}
		else
		{
			addButton(new GuiTextButton(this, 2, (ClientGame.WIDTH / 2) - (72 / 2), 58 + (24 * 1), 72, blackFont, "Disconnect"));
		}
		addButton(new GuiIconButton(this, 4, (ClientGame.WIDTH / 2) - (72 / 2) - 16 - 4, 58 + (24 * 1), 16, Assets.getStepSpriteAnimation(AssetType.ICON_SETTINGS).clone()));
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		nobleFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2, 15), "Game Not Paused!");
	}
	
	@Override
	public void releaseAction(int buttonID)
	{
		switch (buttonID)
		{
			case 0: // Resume
				goToParentScreen();
				break;
			case 2: // Disconnect
				ClientGame.instance().exitFromGame(ExitGameReason.DISCONNECT);
				break;
			case 1: // Stop Server
				if (ServerGame.instance() != null)
				{
					ServerGame.instance().stopThis();
				}
				break;
			case 4:// Options menu
				ClientGame.instance().setCurrentScreen(new GuiOptionsMenu(this, true));
				break;
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (ClientGame.instance().input().escape.isReleasedFiltered())
		{
			goToParentScreen();
		}
	}
}
