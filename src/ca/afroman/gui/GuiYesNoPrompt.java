package ca.afroman.gui;

import java.awt.Color;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.resource.Vector2DInt;

class GuiNoButton extends GuiTextButton
{
	private GuiYesNoPrompt prompt;
	
	GuiNoButton(GuiScreen screen, int response, int x, int y, GuiYesNoPrompt prompt)
	{
		super(screen, response, x, y, 36, Assets.getFont(AssetType.FONT_BLACK), "No");
		
		this.prompt = prompt;
	}
	
	@Override
	protected void onPress(boolean isLeft)
	{
		// Need this here, otherwise it will do onPress behaviour from the super method
	}
	
	@Override
	protected void onRelease(boolean isLeft)
	{
		super.onRelease(isLeft);
		prompt.remove();
	}
}

public class GuiYesNoPrompt extends GuiTextButton
{
	private static final int BUTTON_WIDTH = 36;
	private static final int BUTTON_SPACING = 2;
	
	private static int drawWidth;
	
	private static final int SIDE_SPACING = 10;
	private static final int DRAW_HEIGHT = 16 + 41;
	private String notifText1;
	private String notifText2;
	private Vector2DInt drawLocation;
	private GuiNoButton noButt;
	
	/**
	 * Asks the user to confirm or deny something.
	 * 
	 * @param screen
	 * @param response the buttonID returned to the GuiScreen upon being released. (<b>response</b> for yes, <b>response + 1</b> for no)
	 * @param text
	 */
	public GuiYesNoPrompt(GuiScreen screen, int response, String text)
	{
		this(screen, response, text, "");
	}
	
	/**
	 * Asks the user to confirm or deny something.
	 * 
	 * @param screen
	 * @param response the buttonID returned to the GuiScreen upon being released. (<b>response</b> for yes, <b>response + 1</b> for no)
	 * @param text1
	 * @param text2
	 */
	public GuiYesNoPrompt(GuiScreen screen, int response, String text1, String text2)
	{
		super(screen, response, (ClientGame.WIDTH / 2) - (BUTTON_WIDTH + BUTTON_SPACING), (ClientGame.HEIGHT / 2) + 8, BUTTON_WIDTH, Assets.getFont(AssetType.FONT_BLACK), "Yes");
		
		this.notifText1 = text1;
		this.notifText2 = text2;
		
		drawWidth = Math.max((2 * (BUTTON_WIDTH + BUTTON_SPACING)) + SIDE_SPACING, (Math.max(text1.length(), text2.length()) * Font.CHAR_WIDTH) + SIDE_SPACING);
		
		drawLocation = new Vector2DInt((ClientGame.WIDTH - drawWidth) / 2, (ClientGame.HEIGHT - DRAW_HEIGHT) / 2);
		
		noButt = new GuiNoButton(screen, response + 1, (ClientGame.WIDTH / 2) + BUTTON_SPACING, hitbox.y, this);
		
		screen.addButton(this);
		screen.addButton(noButt);
	}
	
	@Override
	protected void onPress(boolean isLeft)
	{
		// Need this here, otherwise it will do onPress behaviour from the super method
	}
	
	@Override
	protected void onRelease(boolean isLeft)
	{
		super.onRelease(isLeft);
		remove();
	}
	
	public void remove()
	{
		screen.removeButton(this);
		screen.removeButton(noButt);
	}
	
	@Override
	public void render(Texture drawTo)
	{
		drawTo.drawFillRect(new Color(0F, 0F, 0F, 1F), new Color(1F, 1F, 1F, 1F), drawLocation, drawWidth, DRAW_HEIGHT);
		
		font.renderCentered(drawTo, new Vector2DInt(drawLocation.getX() + (drawWidth / 2), drawLocation.getY() + 7), notifText1);
		font.renderCentered(drawTo, new Vector2DInt(drawLocation.getX() + (drawWidth / 2), drawLocation.getY() + 19), notifText2);
		
		super.render(drawTo);
	}
}
