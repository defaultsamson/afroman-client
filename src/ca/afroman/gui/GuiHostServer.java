package ca.afroman.gui;

import java.awt.Color;

import ca.afroman.ClientGame;
import ca.afroman.asset.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
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
		
		lightmap = new LightMap(ClientGame.WIDTH, ClientGame.HEIGHT, new Color(0F, 0F, 0F, 0.3F));
		light = new FlickeringLight(null, ClientGame.WIDTH / 2, 38, 60, 62, 5);
		
		username = new GuiTextField(this, (ClientGame.WIDTH / 2) - (112 / 2) - 57, 62);
		username.setText(game.getUsername());
		username.setMaxLength(11);
		username.setAllowPunctuation(false);
		password = new GuiTextField(this, (ClientGame.WIDTH / 2) - (112 / 2) - 57, 90);
		password.setText(game.getPassword());
		password.setMaxLength(11);
		password.setAllowPunctuation(false);
		
		game.setServerIP(ServerSocket.IPv4_LOCALHOST);
		
		buttons.add(username);
		buttons.add(password);
		
		hostButton = new GuiTextButton(this, 1, 150, 62, Assets.getFont(AssetType.FONT_BLACK), "Host Server");
		hostButton.setEnabled(!this.username.getText().isEmpty());
		
		buttons.add(hostButton);
		buttons.add(new GuiTextButton(this, 200, 150, 90, Assets.getFont(AssetType.FONT_BLACK), "Back"));
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		lightmap.clear();
		light.renderCentered(lightmap);
		lightmap.patch();
		
		renderTo.draw(lightmap, 0, 0);
		
		nobleFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 15, "Host A Server");
		
		blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2 - 57, 62 - 10, "Username");
		blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2 - 57, 90 - 10, "Server Pass");
		
		renderTo.draw(afroMan.getCurrentFrame(), (ClientGame.WIDTH / 2) - 20, 30);
		renderTo.draw(player2.getCurrentFrame(), (ClientGame.WIDTH / 2) + 4, 30);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		light.tick();
		afroMan.tick();
		player2.tick();
		
		if (ClientGame.instance().input.tab.isPressedFiltered())
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
	}
	
	@Override
	public void pressAction(int buttonID)
	{
		switch (buttonID)
		{
			case 1: // Host Server
				game.setUsername(this.username.getText());
				
				// If not already hosting
				if (!game.isHosting)
				{
					// If the server is null, instantiate it
					if (game.server == null) game.server = new ServerGame(this.password.getText());
					
					// Start that server thread
					game.server.start();
				}
				
				game.isHosting = true;
				
				ClientGame.instance().joinServer();
				break;
		}
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
		
		game.setUsername(this.username.getText());
		game.setPassword(this.password.getText());
	}
}
