package ca.afroman.gui;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMap;

public class GuiJoinServer extends GuiScreen
{
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
		light = new FlickeringLight(null, ClientGame.WIDTH / 2, 38, 60, 62, 5);
		
		username = new GuiTextField(this, (ClientGame.WIDTH / 2) - (112 / 2) - 57, 60 - 4, 112);
		username.setText(ClientGame.instance().getUsername());
		username.setMaxLength(11);
		username.setAllowPunctuation(false);
		username.setFocussed();
		serverIP = new GuiTextField(this, (ClientGame.WIDTH / 2) - (112 / 2) - 57, 90 - 6, 112);
		serverIP.setMaxLength(64);
		serverIP.setText(ClientGame.instance().getServerIP());
		password = new GuiTextField(this, (ClientGame.WIDTH / 2) - (112 / 2) - 57, 120 - 8, 112);
		password.setText(ClientGame.instance().getPassword());
		password.setMaxLength(11);
		password.setAllowPunctuation(false);
		
		buttons.add(username);
		buttons.add(serverIP);
		buttons.add(password);
		
		joinButton = new GuiTextButton(this, 1, 150, 62, 72, Assets.getFont(AssetType.FONT_BLACK), "Join Server");
		this.joinButton.setEnabled(!this.username.getText().isEmpty() && !this.serverIP.getText().isEmpty());
		
		keyTyped();
		
		buttons.add(joinButton);
		buttons.add(new GuiTextButton(this, 200, 150, 90, 72, Assets.getFont(AssetType.FONT_BLACK), "Back"));
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		renderTo.draw(afroMan.getCurrentFrame(), (ClientGame.WIDTH / 2) - 20, 30);
		renderTo.draw(player2.getCurrentFrame(), (ClientGame.WIDTH / 2) + 4, 30);
		
		lightmap.clear();
		light.renderCentered(lightmap);
		lightmap.patch();
		
		renderTo.draw(lightmap, 0, 0);
		
		nobleFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 15, "Join a Server");
		
		blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2 - 57, 50 - 4, "Username");
		blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2 - 57, 80 - 6, "Server IP");
		blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2 - 57, 110 - 8, "Server Pass");
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		light.tick();
		afroMan.tick();
		player2.tick();
		
		if (ClientGame.instance().input().tab.isPressedFiltered())
		{
			if (username.isFocussed())
			{
				serverIP.setFocussed();
			}
			else if (serverIP.isFocussed())
			{
				password.setFocussed();
			}
			else
			{
				password.setFocussed(false);
			}
		}
		
		if (ClientGame.instance().input().enter.isPressedFiltered())
		{
			joinServer();
		}
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
				ClientGame.instance().setCurrentScreen(this.parentScreen);
				break;
		}
	}
	
	@Override
	public void keyTyped()
	{
		this.joinButton.setEnabled(!this.username.getText().isEmpty() && !this.serverIP.getText().isEmpty());
		
		ClientGame.instance().setUsername(this.username.getText());
		ClientGame.instance().setServerIP(this.serverIP.getText());
		ClientGame.instance().setPassword(this.password.getText());
	}
}
