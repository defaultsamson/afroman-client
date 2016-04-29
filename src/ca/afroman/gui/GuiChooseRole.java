package ca.afroman.gui;

import java.awt.Color;

import ca.afroman.Game;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.entity.Role;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMap;

public class GuiChooseRole extends GuiScreen
{
	private int playerID;
	
	private SpriteAnimation player1;
	private int player1X = 0;
	private int player1Y = 0;
	private SpriteAnimation player2;
	private int player2X = 0;
	private int player2Y = 0;
	private LightMap lightmap;
	private FlickeringLight light1;
	private FlickeringLight light2;
	
	private GuiTextButton player1b;
	private GuiTextButton player2b;
	
	public GuiChooseRole(GuiScreen parentScreen, int playerID)
	{
		super(parentScreen);
		
		this.playerID = playerID;
	}
	
	@Override
	public void init()
	{
		player1X = 40;
		player1Y = 30;
		
		player1 = Assets.getSpriteAnimation(Assets.PLAYER_ONE_IDLE_DOWN);
		player2 = Assets.getSpriteAnimation(Assets.PLAYER_TWO_IDLE_DOWN);
		
		lightmap = new LightMap(Game.WIDTH, Game.HEIGHT, new Color(0F, 0F, 0F, 0.3F));
		
		light1 = new FlickeringLight(0, 0, 42, 44, 8);
		light2 = new FlickeringLight(0, 0, 42, 44, 8);
		
		Role role = game.socketClient.playerByID(playerID).getRole();
		
		this.buttons.add(new GuiTextButton(this, 200, (Game.WIDTH / 2) - (72 / 2), 116, blackFont, "Cancel"));
		player1b = new GuiTextButton(this, 201, (Game.WIDTH / 2) - (72 / 2) - 78, 116, blackFont, "Player 1");
		player1b.setEnabled(role != Role.PLAYER1);
		
		player2b = new GuiTextButton(this, 202, (Game.WIDTH / 2) - (72 / 2) + 78, 116, blackFont, "Player 2");
		player2b.setEnabled(role != Role.PLAYER2);
		
		this.buttons.add(player1b);
		this.buttons.add(player2b);
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		
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
		switch (buttonID)
		{
			case 200:
				Game.instance().setCurrentScreen(this.parentScreen);
				break;
		}
	}
}
