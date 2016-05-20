package ca.afroman.gui;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMap;
import ca.afroman.server.ServerGame;
import ca.afroman.server.ServerSocket;

public class GuiHostServer extends GuiScreen
{
	private SpriteAnimation afroMan;
	private SpriteAnimation player2;
	private LightMap lightmap;
	private FlickeringLight light;
	
	private GuiTextField username;
	private GuiTextField password;
	
	private GuiTextButton hostButton;
	
	public GuiHostServer(GuiScreen parent)
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
		
		username = new GuiTextField(this, (ClientGame.WIDTH / 2) - (112 / 2) - 57, 62, 112);
		username.setText(ClientGame.instance().getUsername());
		username.setMaxLength(11);
		username.setAllowPunctuation(false);
		username.setFocussed();
		password = new GuiTextField(this, (ClientGame.WIDTH / 2) - (112 / 2) - 57, 90, 112);
		password.setText(ClientGame.instance().getPassword());
		password.setMaxLength(11);
		password.setAllowPunctuation(false);
		
		ClientGame.instance().setServerIP(ServerSocket.IPv4_LOCALHOST);
		
		buttons.add(username);
		buttons.add(password);
		
		hostButton = new GuiTextButton(this, 1, 150, 62, 72, Assets.getFont(AssetType.FONT_BLACK), "Host Server");
		hostButton.setEnabled(!this.username.getText().isEmpty());
		
		buttons.add(hostButton);
		buttons.add(new GuiTextButton(this, 200, 150, 90, 72, Assets.getFont(AssetType.FONT_BLACK), "Back"));
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
		
		nobleFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 15, "Host A Server");
		
		blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2 - 57, 62 - 10, "Username");
		blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2 - 57, 90 - 10, "Server Pass");
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
			else
			{
				password.setFocussed(false);
			}
		}
		
		if (ClientGame.instance().input().enter.isPressedFiltered())
		{
			hostServer();
		}
	}
	
	@Override
	public void pressAction(int buttonID)
	{
		switch (buttonID)
		{
			case 1: // Host Server
				hostServer();
				break;
		}
	}
	
	private void hostServer()
	{
		ClientGame.instance().setUsername(this.username.getText());
		
		// If not already hosting
		if (!ClientGame.instance().isHostingServer())
		{
			new ServerGame(this.password.getText());
			// Start that server thread
			ServerGame.instance().start();
		}
		
		ClientGame.instance().joinServer();
	}
	
	@Override
	public void releaseAction(int buttonID)
	{
		switch (buttonID)
		{
			case 200:
				ClientGame.instance().setCurrentScreen(this.parentScreen);
				break;
		}
	}
	
	@Override
	public void keyTyped()
	{
		this.hostButton.setEnabled(!this.username.getText().isEmpty());
		
		ClientGame.instance().setUsername(this.username.getText());
		ClientGame.instance().setPassword(this.password.getText());
	}
}
