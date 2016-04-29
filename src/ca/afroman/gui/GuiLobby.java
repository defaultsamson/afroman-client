package ca.afroman.gui;

import java.awt.Color;

import ca.afroman.Game;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.entity.Role;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMap;
import ca.afroman.network.ConnectedPlayer;

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
	
	public GuiLobby(GuiScreen parentScreen)
	{
		super(parentScreen);
	}
	
	@Override
	public void init()
	{
		player1 = Assets.getSpriteAnimation(Assets.PLAYER_ONE_IDLE_DOWN);
		player2 = Assets.getSpriteAnimation(Assets.PLAYER_TWO_IDLE_DOWN);
		
		lightmap = new LightMap(Game.WIDTH, Game.HEIGHT, new Color(0F, 0F, 0F, 0.3F));
		
		light1 = new FlickeringLight(0, 0, 42, 44, 8);
		light2 = new FlickeringLight(0, 0, 42, 44, 8);
		
		startButton = new GuiTextButton(this, 2000, (Game.WIDTH / 2) - (72 / 2), 116, blackFont, "Start Game");
		buttons.add(startButton);
	}
	
	@Override
	public void tick()
	{
		// Removes all buttons except for that at index 0 (The Start Game Button)
		for (int i = 1; i < buttons.size(); i++)
		{
			this.buttons.remove(i);
		}
		
		light1.tick();
		light2.tick();
		
		player1X = -300;
		player1Y = -300;
		player2X = -300;
		player2Y = -300;
		
		// Draws the player list
		int counter = 0;
		int row = 0;
		for (ConnectedPlayer player : game.socketClient.getPlayers())
		{
			boolean isEvenNum = (counter & 1) == 0;
			
			if (isEvenNum) row++;
			
			int buttonX = (isEvenNum ? 20 : 148);
			int buttonY = 20 + (18 * row);
			
			// Add each player in the last as a button. Only lets the host of the server edit
			GuiTextButton button = new GuiTextButton(this, player.getID(), buttonX, buttonY, (game.isHostingServer() ? blackFont : whiteFont), player.getUsername());
			button.setEnabled(game.isHostingServer());
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
		
		nobleFont.renderCentered(renderTo, Game.WIDTH / 2, 6, "Connected Players");
		if (game.isHostingServer())
		{
			blackFont.renderCentered(renderTo, Game.WIDTH / 2, 20, "(Click on a name to choose role)");
		}
		else
		{
			blackFont.renderCentered(renderTo, Game.WIDTH / 2, 20, "(Only the host can choose roles)");
		}
	}
	
	@Override
	public void keyTyped()
	{
		
	}
	
	@Override
	public void pressAction(int buttonID)
	{
		if (buttonID != 2000)
		{
			Game.instance().setCurrentScreen(new GuiChooseRole(this, buttonID));
		}
	}
	
	@Override
	public void releaseAction(int buttonID)
	{
		if (buttonID == 2000)
		{
			// TODO start game
		}
		else
		{
			Game.instance().setCurrentScreen(new GuiChooseRole(this, buttonID));
		}
	}
	
}
