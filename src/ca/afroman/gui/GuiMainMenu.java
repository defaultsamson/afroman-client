package ca.afroman.gui;

import java.awt.Color;

import ca.afroman.Game;
import ca.afroman.assets.Assets;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMap;

public class GuiMainMenu extends GuiScreen
{
	private SpriteAnimation afroMan;
	private SpriteAnimation player2;
	private LightMap lightmap;
	private FlickeringLight light;
	
	public GuiMainMenu()
	{
		super(null);
	}
	
	@Override
	public void init()
	{
		afroMan = Assets.getSpriteAnimation(Assets.PLAYER_ONE_IDLE_DOWN);
		player2 = Assets.getSpriteAnimation(Assets.PLAYER_TWO_IDLE_DOWN);
		
		lightmap = new LightMap(Game.WIDTH, Game.HEIGHT, new Color(0F, 0F, 0F, 0.3F));
		light = new FlickeringLight(Game.WIDTH / 2, 38, 60, 62, 5);
		
		buttons.add(new GuiTextButton(this, 1, (Game.WIDTH / 2) - (72 / 2), 60, blackFont, "Join Game"));
		buttons.add(new GuiTextButton(this, 2, (Game.WIDTH / 2) - (72 / 2), 90, blackFont, "Host Game"));
	}
	
	@Override
	public void drawScreen(Texture renderTo)
	{
		renderTo.draw(afroMan.getCurrentFrame(), (Game.WIDTH / 2) - 20, 30);
		renderTo.draw(player2.getCurrentFrame(), (Game.WIDTH / 2) + 4, 30);
		
		lightmap.clear();
		light.renderCentered(lightmap);
		lightmap.patch();
		
		renderTo.draw(lightmap, 0, 0);
		
		nobleFont.renderCentered(renderTo, Game.WIDTH / 2, 15, "Cancer: The Adventures of Afro Man");
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		light.tick();
		afroMan.tick();
		player2.tick();
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
			case 2: // Host Server
				game.setCurrentScreen(new GuiHostServer(this));
				break;
			case 1: // Join Server
				game.setCurrentScreen(new GuiJoinServer(this));
		}
	}
	
	@Override
	public void keyTyped()
	{
		
	}
}
