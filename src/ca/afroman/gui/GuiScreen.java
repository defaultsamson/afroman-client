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
	protected static Font nobleFont = Assets.getFont(AssetType.FONT_BLACK);
	protected static Font whiteFont = Assets.getFont(AssetType.FONT_WHITE);
	protected static Font blackFont = Assets.getFont(AssetType.FONT_BLACK);
	
	protected GuiScreen parentScreen;
	protected List<GuiButton> buttons;
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
		for (GuiButton bu : buttons)
		{
			removeButton(bu);
		}
	}
	
	/**
	 * Same function as the render() method, but ensures that buttons are drawn
	 * overtop of the rest of the screen being drawn.
	 * 
	 * @param renderTo
	 */
	public void drawScreen(Texture renderTo)
	{
		
	}
	
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
	
	/**
	 * Fires whenever a letter is typed in a GuiTextField
	 */
	public void keyTyped()
	{
		
	}
	
	/**
	 * Fires whenever a button is pressed.
	 * 
	 * @param buttonID the id of the button
	 */
	public void pressAction(int buttonID, boolean isLeft)
	{
		
	}
	
	/**
	 * Fires whenever a button is released.
	 * 
	 * @param buttonID the id of the button
	 */
	public void releaseAction(int buttonID, boolean isLeft)
	{
		
	}
	
	public void removeButton(GuiButton button)
	{
		buttonToRemove.add(button);
	}
	
	/**
	 * Renders this GuiScreen to the renderTo Texture.
	 * 
	 * @deprecated use drawScreen when extending
	 * 
	 * @param renderTo
	 */
	@Deprecated
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
	
	/**
	 * Fires whenever the value of a slider is modified
	 * 
	 * @param sliderID
	 * @param newValue
	 */
	public void updateValue(int sliderID, int newValue)
	{
		
	}
}
