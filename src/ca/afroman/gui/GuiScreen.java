package ca.afroman.gui;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;

public abstract class GuiScreen
{
	protected static Font nobleFont = Assets.getFont(AssetType.FONT_NOBLE);
	protected static Font whiteFont = Assets.getFont(AssetType.FONT_WHITE);
	protected static Font blackFont = Assets.getFont(AssetType.FONT_BLACK);
	
	protected GuiScreen parentScreen;
	private List<GuiButton> buttons;
	private List<GuiButton> buttonToRemove;
	private List<GuiButton> buttonToAdd;
	
	private GuiButton listening = null;
	
	public GuiScreen(GuiScreen parentScreen)
	{
		this.parentScreen = parentScreen;
		this.buttons = new ArrayList<GuiButton>();
		this.buttonToRemove = new ArrayList<GuiButton>();
		this.buttonToAdd = new ArrayList<GuiButton>();
	}
	
	public void addButton(GuiButton but)
	{
		buttonToAdd.add(but);
	}
	
	public void clearButtons()
	{
		buttons.clear();
	}
	
	/**
	 * Draws the screen.
	 * 
	 * @param delta the time between the last time the screen was drawn and the time that this is currently being drawn
	 */
	public abstract void drawScreen(Texture renderTo);
	
	public GuiButton getListeningButton()
	{
		return this.listening;
	}
	
	public GuiScreen getParent()
	{
		return this.parentScreen;
	}
	
	public void goToParentScreen()
	{
		ClientGame.instance().setCurrentScreen(this.parentScreen);
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
	
	public void removeButton(GuiButton button)
	{
		buttonToRemove.add(button);
	}
	
	/**
	 * Operates in the same way as renderEntries(SpriteBatch batch);
	 * 
	 * @param batch the SpriteBatch to draw the buttons to
	 */
	public void render(Texture renderTo)
	{
		drawScreen(renderTo);
		
		// Render the list backwards so that it draws buttons from top to bottom properly
		for (int i = buttons.size() - 1; i >= 0; i--)
		{
			buttons.get(i).render(renderTo);
		}
	}
	
	public void setListeningButton(GuiButton button)
	{
		// Only set the listening button if it is null. This way it will only set the first one it comes across each tick.
		if (listening == null)
		{
			this.listening = button;
		}
	}
	
	public void tick()
	{
		listening = null;
		
		// Removes all the buttons pending removal
		for (GuiButton button : this.buttonToRemove)
		{
			buttons.remove(button);
		}
		
		for (GuiButton button : this.buttonToAdd)
		{
			// Prevents buttons from being added more than once
			if (!buttons.contains(button))
			{
				// to invoke the top-most button to the buttom-most
				buttons.add(0, button);
			}
		}
		
		this.buttonToRemove.clear();
		this.buttonToAdd.clear();
		
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
}
