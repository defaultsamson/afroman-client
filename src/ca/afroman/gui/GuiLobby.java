package ca.afroman.gui;

import java.awt.Color;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMap;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.packet.PacketDisconnect;
import ca.afroman.packet.PacketStartGame;
import ca.afroman.packet.PacketStopServer;
import ca.afroman.player.Role;
import ca.afroman.server.ServerSocket;

public class GuiLobby extends GuiScreen
{
	private SpriteAnimation player1;
	private int player1X = 0;
	private int player1Y = 0;
	private SpriteAnimation player2;
	private int player2X = 0;
	private int player2Y = 0;
	private LightMap lightmap;
	private FlickeringLight light1;
	private FlickeringLight light2;
	
	private GuiTextButton startButton;
	private GuiTextButton stopButton;
	
	public GuiLobby(GuiScreen parentScreen)
	{
		super(parentScreen);
	}
	
	@Override
	public void init()
	{
		player1 = Assets.getSpriteAnimation(AssetType.PLAYER_ONE_IDLE_DOWN);
		player2 = Assets.getSpriteAnimation(AssetType.PLAYER_TWO_IDLE_DOWN);
		
		lightmap = new LightMap(ClientGame.WIDTH, ClientGame.HEIGHT, new Color(0F, 0F, 0F, 0.3F));
		
		light1 = new FlickeringLight(null, 0, 0, 42, 44, 6);
		light2 = new FlickeringLight(null, 0, 0, 42, 44, 6);
		
		startButton = new GuiTextButton(this, 2000, 20 + 20, 116, blackFont, "Start Game");
		startButton.setEnabled(ClientGame.instance().isHostingServer());
		buttons.add(startButton);
		
		// Draw a stop server button
		if (ClientGame.instance().isHostingServer())
		{
			stopButton = new GuiTextButton(this, 2001, 148 - 20, 116, blackFont, "Stop Server");
		}
		// Draw a leave server button
		else
		{
			stopButton = new GuiTextButton(this, 2002, 148 - 20, 116, blackFont, "Disconnect");
		}
		buttons.add(stopButton);
	}
	
	@Override
	public synchronized void tick()
	{
		light1.tick();
		light2.tick();
		
		// Draws all the new buttons if the server list has been updated
		if (ClientGame.instance().hasServerListBeenUpdated())
		{
			// Removes all buttons except for that at index 0 (The Start Game Button)
			buttons.clear();
			buttons.add(startButton);
			buttons.add(stopButton);
			
			player1X = -300;
			player1Y = -300;
			player2X = -300;
			player2Y = -300;
			
			// Draws the player list
			int counter = 0;
			int row = 0;
			for (ConnectedPlayer player : ClientGame.instance().socket().getPlayers())
			{
				boolean isEvenNum = (counter & 1) == 0;
				
				if (isEvenNum) row++;
				
				int buttonX = (isEvenNum ? 20 : 148);
				int buttonY = 20 + (18 * row);
				
				// Add each player in the last as a button. Only lets the host of the server edit
				GuiTextButton button = new GuiTextButton(this, player.getID(), buttonX, buttonY, (ClientGame.instance().isHostingServer() ? blackFont : whiteFont), player.getUsername());
				button.setEnabled(ClientGame.instance().isHostingServer());
				this.buttons.add(button);
				
				// Draws the player sprite beside the name of the user playing as that character
				if (player.getRole() == Role.PLAYER1)
				{
					player1X = buttonX + (isEvenNum ? 80 : -24);
					player1Y = buttonY;
				}
				else if (player.getRole() == Role.PLAYER2)
				{
					player2X = buttonX + (isEvenNum ? 80 : -24);
					player2Y = buttonY;
				}
				
				counter++;
			}
			
			// Only enable the start button if both the player roles are not null
			startButton.setEnabled(ClientGame.instance().isHostingServer() && ClientGame.instance().socket().playerByRole(Role.PLAYER1) != null && ClientGame.instance().socket().playerByRole(Role.PLAYER2) != null);
		}
		
		light1.tick();
		light2.tick();
		
		super.tick();
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		renderTo.draw(player1.getCurrentFrame(), player1X, player1Y);
		renderTo.draw(player2.getCurrentFrame(), player2X, player2Y);
		
		light1.setX(player1X + 8);
		light1.setY(player1Y + 8);
		
		light2.setX(player2X + 8);
		light2.setY(player2Y + 8);
		
		lightmap.clear();
		light1.renderCentered(lightmap);
		light2.renderCentered(lightmap);
		lightmap.patch();
		
		renderTo.draw(lightmap, 0, 0);
		
		nobleFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 6, "Connected Players: " + ClientGame.instance().socket().getPlayers().size() + "/" + ServerSocket.MAX_PLAYERS);
		if (ClientGame.instance().isHostingServer())
		{
			blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 20, "(Click on a name to choose role)");
		}
		else
		{
			blackFont.renderCentered(renderTo, ClientGame.WIDTH / 2, 20, "(Only the host can choose roles)");
		}
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
		if (buttonID == 2000) // Start Game
		{
			ClientGame.instance().socket().sendPacket(new PacketStartGame());
		}
		else if (buttonID == 2001) // Stop Server
		{
			ClientGame.instance().socket().sendPacket(new PacketStopServer());
		}
		else if (buttonID == 2002) // Leave server
		{
			ClientGame.instance().socket().sendPacket(new PacketDisconnect());
			ClientGame.instance().exitFromGame();
		}
		else
		{
			ClientGame.instance().setCurrentScreen(new GuiChooseRole(this, buttonID));
		}
	}
}
