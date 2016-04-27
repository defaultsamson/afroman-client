package ca.afroman.gui;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.Game;
import ca.afroman.assets.Texture;

public abstract class GuiScreen
{
	protected Game game;
	protected GuiScreen parentScreen;
	protected List<GuiButton> buttons;
	
	public GuiScreen(Game game, GuiScreen parentScreen)
	{
		this.game = game;
		this.parentScreen = parentScreen;
		this.buttons = new ArrayList<GuiButton>();
		
		init();
	}
	
	public abstract void init();
	
	public void addButton(GuiButton button)
	{
		buttons.add(button);
	}
	
	public void tick()
	{
		for(GuiButton button : this.buttons)
		{
			button.tick();
		}
	}
	
	/**
	 * Draws the screen.
	 * 
	 * @param delta the time between the last time the screen was drawn and the time that this is currently being drawn
	 */
	public abstract void drawScreen(Texture renderTo);
	
	/**
	 * Operates in the same way as renderEntries(SpriteBatch batch);
	 * 
	 * @param batch the SpriteBatch to draw the buttons to
	 */
	public void render(Texture renderTo)
	{
		drawScreen(renderTo);
		
		for(GuiButton button : this.buttons)
		{
			button.render(renderTo);
		}
	}
	
	/**
	 * Fires whenever a button is pressed.
	 * 
	 * @param buttonID the id of the button
	 */
	public abstract void pressAction(int buttonID);
	
	/**
	 * Fires whenever a button is released.
	 * 
	 * @param buttonID the id of the button
	 */
	public abstract void releaseAction(int buttonID);
}
