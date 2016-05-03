package ca.afroman.gui;

import ca.afroman.Game;
import ca.afroman.assets.Texture;

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
		buttons.add(new GuiTextButton(this, 0, (Game.WIDTH / 2) - (72 / 2), 110, blackFont, "Cancel"));
		
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
		blackFont.renderCentered(renderTo, Game.WIDTH / 2, 20, "Connecting to Server: " + game.getServerIP());
		
		blackFont.renderCentered(renderTo, Game.WIDTH / 2, 45, "Waiting for server response");
		blackFont.renderCentered(renderTo, Game.WIDTH / 2, 55, "for " + (millsPassed / 1000) + " seconds...");
		
		blackFont.renderCentered(renderTo, Game.WIDTH / 2, 80, "If nothing happens for a while,");
		blackFont.renderCentered(renderTo, Game.WIDTH / 2, 90, "cancel and try rejoining.");
	}
	
	@Override
	public void pressAction(int buttonID)
	{
		
	}
	
	@Override
	public void releaseAction(int buttonID)
	{
		switch(buttonID)
		{
			case 0:
				Game.instance().setCurrentScreen(this.parentScreen);
				Game.instance().socketClient.setServerIP(null);
				break;
		}
	}
	
	@Override
	public void keyTyped()
	{
		
	}
}
