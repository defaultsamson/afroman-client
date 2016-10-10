package ca.afroman.gui;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.input.TypingMode;
import ca.afroman.option.Options;
import ca.afroman.resource.Vector2DInt;
import ca.afroman.server.ServerGame;

public class GuiHostServer extends GuiMenuOutline
{
	private GuiTextField username;
	private GuiTextField password;
	private GuiTextField port;
	
	private GuiTextButton hostButton;
	
	public GuiHostServer(GuiScreen parent)
	{
		super(parent, true, true);
		
		username = new GuiTextField(this, (ClientGame.WIDTH / 2) - (112 / 2) - 57, 62, 112);
		username.setText(Options.instance().serverUsername);
		username.setMaxLength(11);
		username.setTypingMode(TypingMode.ONLY_NUMBERS_AND_LETTERS);
		username.setFocussed();
		
		password = new GuiTextField(this, (ClientGame.WIDTH / 2) - (112 / 2) - 57, 90, 72);
		password.setText(Options.instance().serverPassword);
		password.setMaxLength(11);
		password.setTypingMode(TypingMode.ONLY_NUMBERS_AND_LETTERS);
		
		port = new GuiTextField(this, (ClientGame.WIDTH / 2) - 37, 90, 36);
		port.setText(Options.instance().serverPort);
		port.setMaxLength(5);
		port.setTypingMode(TypingMode.ONLY_NUMBERS);
		
		addButton(username);
		addButton(password);
		addButton(port);
		
		hostButton = new GuiTextButton(this, 1, 144, 62, 72, Assets.getFont(AssetType.FONT_BLACK), "Host Server");
		hostButton.setEnabled(!this.username.getText().isEmpty());
		
		addButton(hostButton);
		addButton(new GuiTextButton(this, 200, 144, 90, 72, Assets.getFont(AssetType.FONT_BLACK), "Back"));
	}
	
	private boolean canContinue()
	{
		try
		{
			int port = Integer.parseInt(this.port.getText());
			
			if (port < 0 || port > 0xFFFF) return false;
		}
		catch (NumberFormatException e)
		{
			
		}
		
		return !this.username.getText().isEmpty();
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		super.drawScreen(renderTo);
		
		nobleFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2, 15), "Host A Server");
		
		blackFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2 - 57, 62 - 10), "Username");
		blackFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2 - 78, 90 - 10), "Pass");
		blackFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2 - 19, 90 - 10), "Port");
	}
	
	@Override
	public void goToParentScreen()
	{
		onLeaving();
		
		super.goToParentScreen();
	}
	
	private void hostServer()
	{
		onLeaving();
		
		// If not already hosting
		if (!ClientGame.instance().isHostingServer())
		{
			if (ServerGame.instance() == null)
			{
				new ServerGame(false, Options.instance().serverIP, Options.instance().serverPassword, Options.instance().serverPort);
			}
			else
			{
				// Start that server thread
				ServerGame.instance().startThis();
			}
		}
		
		ClientGame.instance().joinServer(Options.instance().serverIP, Options.instance().serverPort, Options.instance().serverUsername, Options.instance().serverPassword);
	}
	
	@Override
	public void keyTyped()
	{
		this.hostButton.setEnabled(canContinue());
	}
	
	private void onLeaving()
	{
		Options.instance().serverUsername = username.getText();
		Options.instance().serverPassword = password.getText();
		Options.instance().serverPort = port.getText();
		
		Options.instance().save();
	}
	
	@Override
	public void releaseAction(int buttonID)
	{
		switch (buttonID)
		{
			case 1: // Host Server
				hostServer();
				break;
			case 200:
				goToParentScreen();
				break;
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (ClientGame.instance().input().tab.isPressedFiltered())
		{
			if (username.isFocussed())
			{
				password.setFocussed();
			}
			else if (password.isFocussed())
			{
				port.setFocussed();
			}
			else
			{
				username.setFocussed();
			}
		}
		
		if (ClientGame.instance().input().enter.isPressedFiltered() && canContinue())
		{
			hostServer();
		}
		
		if (ClientGame.instance().input().escape.isPressedFiltered())
		{
			goToParentScreen();
		}
	}
}
