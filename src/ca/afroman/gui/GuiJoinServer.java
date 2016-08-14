package ca.afroman.gui;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMap;
import ca.afroman.input.TypingMode;
import ca.afroman.log.ALogType;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.resource.Vector2DInt;

public class GuiJoinServer extends GuiScreen
{
	private static String userText = "";
	private static String ipText = "";
	private static String passText = "";
	
	private SpriteAnimation afroMan;
	private SpriteAnimation player2;
	private LightMap lightmap;
	private FlickeringLight light;
	
	private GuiTextField username;
	private GuiTextField serverIP;
	private GuiTextField password;
	
	private GuiTextButton joinButton;
	
	public GuiJoinServer(GuiScreen parent)
	{
		super(parent);
	}
	
	@Override
	public void init()
	{
		afroMan = Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_DOWN);
		player2 = Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_DOWN);
		
		lightmap = new LightMap(ClientGame.WIDTH, ClientGame.HEIGHT, LightMap.DEFAULT_AMBIENT);
		light = new FlickeringLight(false, -1, new Vector2DDouble(ClientGame.WIDTH / 2, 38), 60, 62, 5);
		
		username = new GuiTextField(this, (ClientGame.WIDTH / 2) - (112 / 2) - 57, 60 - 4, 112);
		username.setText(userText);
		username.setMaxLength(11);
		username.setTypingMode(TypingMode.ONLY_NUMBERS_AND_LETTERS);
		username.setFocussed();
		
		// GuiTextField(this, (ClientGame.WIDTH / 2) - (112 / 2) - 57, 90 - 6, 112);
		password = new GuiTextField(this, (ClientGame.WIDTH / 2) - (112 / 2) - 57, 90 - 6, 112);
		password.setText(passText);
		password.setMaxLength(11);
		password.setTypingMode(TypingMode.ONLY_NUMBERS_AND_LETTERS);
		
		// new GuiTextField(this, (ClientGame.WIDTH / 2) - (112 / 2) - 57, 120 - 8, 112);
		serverIP = new GuiTextField(this, (ClientGame.WIDTH / 2) - (112 / 2) - 57, 120 - 8, 224);
		serverIP.setMaxLength(64);
		serverIP.setText(ipText);
		
		buttons.add(username);
		buttons.add(serverIP);
		buttons.add(password);
		
		joinButton = new GuiTextButton(this, 1, 144, 60 - 4, 72, Assets.getFont(AssetType.FONT_BLACK), "Join Server");
		this.joinButton.setEnabled(!this.username.getText().isEmpty() && !this.serverIP.getText().isEmpty());
		
		keyTyped();
		
		buttons.add(joinButton);
		buttons.add(new GuiTextButton(this, 200, 144, 90 - 6, 72, Assets.getFont(AssetType.FONT_BLACK), "Back"));
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		renderTo.draw(afroMan.getCurrentFrame(), new Vector2DInt((ClientGame.WIDTH / 2) - 20, 30));
		renderTo.draw(player2.getCurrentFrame(), new Vector2DInt((ClientGame.WIDTH / 2) + 4, 30));
		
		if (ClientGame.instance().isLightingOn())
		{
			lightmap.clear();
			light.renderCentered(lightmap);
			lightmap.patch();
			
			renderTo.draw(lightmap, LightMap.PATCH_POSITION);
		}
		
		nobleFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2, 15), "Join a Server");
		
		blackFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2 - 57, 50 - 4), "Username");
		blackFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2 - 57, 80 - 6), "Password");
		blackFont.renderCentered(renderTo, new Vector2DInt(ClientGame.WIDTH / 2 - 57, 110 - 8), "Server IP");
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
		
		if (ClientGame.instance().input().tab.isPressedFiltered())
		{
			if (username.isFocussed())
			{
				password.setFocussed();
			}
			else if (password.isFocussed())
			{
				serverIP.setFocussed();
			}
			else
			{
				username.setFocussed();
			}
		}
		
		if (ClientGame.instance().input().enter.isPressedFiltered() && canContinue())
		{
			joinServer();
		}
		
		if (ClientGame.instance().input().escape.isPressedFiltered())
		{
			goToParentScreen();
		}
	}
	
	@Override
	public void goToParentScreen()
	{
		userText = username.getText();
		ipText = serverIP.getText();
		passText = password.getText();
		
		super.goToParentScreen();
	}
	
	@Override
	public void pressAction(int buttonID)
	{
		switch (buttonID)
		{
			
		}
	}
	
	private void joinServer()
	{
		userText = username.getText();
		ipText = serverIP.getText();
		passText = password.getText();
		
		String[] portSplit = ipText.split(":");
		
		String port = "";
		
		if (portSplit.length > 1)
		{
			try
			{
				int nPort = Integer.parseInt(portSplit[portSplit.length - 1]);
				port += nPort;
			}
			catch (NumberFormatException e)
			{
				ClientGame.instance().logger().log(ALogType.WARNING, "Unable to parse port", e);
			}
		}
		
		ClientGame.instance().setUsername(userText);
		ClientGame.instance().setServerIP(portSplit[0]);
		ClientGame.instance().setPort(port);
		ClientGame.instance().setPassword(passText);
		
		ClientGame.instance().joinServer();
	}
	
	@Override
	public void releaseAction(int buttonID)
	{
		switch (buttonID)
		{
			case 1: // Join Server
				joinServer();
				break;
			case 200:
				goToParentScreen();
				break;
		}
	}
	
	private boolean canContinue()
	{
		return !this.username.getText().isEmpty() && !this.serverIP.getText().isEmpty() && serverIP.getText().split(":").length <= 2;
	}
	
	@Override
	public void keyTyped()
	{
		this.joinButton.setEnabled(canContinue());
	}
}
