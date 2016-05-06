package ca.afroman.gui;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.ClientGame;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;
import ca.afroman.asset.AssetType;

public abstract class GuiScreen
{
	protected ClientGame game;
	protected GuiScreen parentScreen;
	protected List<GuiButton> buttons;
	private List<GuiButton> buttonToRemove;
	
	protected static Font nobleFont = Assets.getFont(AssetType.FONT_NOBLE);
	protected static Font whiteFont = Assets.getFont(AssetType.FONT_WHITE);
	protected static Font blackFont = Assets.getFont(AssetType.FONT_BLACK);
	
	public GuiScreen(GuiScreen parentScreen)
	{
		this.game = ClientGame.instance();
		this.parentScreen = parentScreen;
		this.buttons = new ArrayList<GuiButton>();
		this.buttonToRemove = new ArrayList<GuiButton>();
		
		init();
	}
	
	public abstract void init();
	
	public void addButton(GuiButton button)
	{
		buttons.add(button);
	}
	
	public void removeButton(GuiButton button)
	{
		buttonToRemove.add(button);
	}
	
	public void tick()
	{
		// Removes all the buttons pending removal
		for (GuiButton button : this.buttonToRemove)
		{
			buttons.remove(button);
		}
		
		this.buttonToRemove.clear();
		
		// Ticks all the buttons
		for (GuiButton button : this.buttons)
		{
			button.tick();
		}
	}
	
	public void unfocusTextFields()
	{
		for (GuiButton button : this.buttons)
		{
			if (button instanceof GuiTextField)
			{
				((GuiTextField) button).setFocussed(false);
			}
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
		
		for (GuiButton button : this.buttons)
		{
			button.render(renderTo);
		}
	}
	
	public GuiScreen getParent()
	{
		return this.parentScreen;
	}
	
	public abstract void keyTyped();
	
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
