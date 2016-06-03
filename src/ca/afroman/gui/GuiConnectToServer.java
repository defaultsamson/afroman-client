package ca.afroman.gui;

import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.server.ServerSocketManager;

public class GuiConnectToServer extends GuiScreen
{
	private long startTime;
	private int millsPassed;
	
	public GuiConnectToServer(GuiScreen parent)
	{
		super(parent);
	}
	
	@Override
	public void init()
	{
		buttons.add(new GuiTextButton(this, 0, (ClientGame.WIDTH / 2) - (72 / 2), 110, 72, blackFont, "Cancel"));
		
		startTime = System.currentTimeMillis();
		millsPassed = 0;
	}
	
	@Override
	public void tick()
	{
		millsPassed = (int) (System.currentTimeMillis() - startTime);
		
		super.tick();
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 20, "Connecting to Server: " + ClientGame.instance().getServerIP());
		
		blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 45, "Waiting for server response");
		blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 55, "for " + (millsPassed / 1000) + " seconds...");
		
		blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 80, "If nothing happens for a while,");
		blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 90, "cancel and try rejoining.");
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
			case 0:
				ClientGame.instance().setCurrentScreen(this.parentScreen);
				ClientGame.instance().sockets().setServerIP(null, ServerSocketManager.PORT); // TODO selectable port
				break;
		}
	}
	
	@Override
	public void keyTyped()
	{
		
	}
}
