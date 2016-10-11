package ca.afroman.gui;

import java.awt.Color;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.resource.Vector2DInt;

public class GuiClickNotification extends GuiTextButton
{
	private static final int BUTTON_WIDTH = 72;
	private static final int SIDE_SPACING = 10;
	
	private static int drawWidth;
	
	private static final int DRAW_HEIGHT = 16 + 41;
	private String notifText1;
	private String notifText2;
	private Vector2DInt drawLocation;
	
	public GuiClickNotification(GuiScreen screen, int id, String text)
	{
		this(screen, id, text, "");
	}
	
	public GuiClickNotification(GuiScreen screen, int id, String text1, String text2)
	{
		super(screen, id, (ClientGame.WIDTH / 2) - (BUTTON_WIDTH / 2), (ClientGame.HEIGHT / 2) + 8, BUTTON_WIDTH, Assets.getFont(AssetType.FONT_BLACK), "Okay");
		
		this.notifText1 = text1;
		this.notifText2 = text2;
		
		drawWidth = Math.max(BUTTON_WIDTH + SIDE_SPACING, (Math.max(text1.length(), text2.length()) * Font.CHAR_WIDTH) + SIDE_SPACING);
		
		drawLocation = new Vector2DInt((ClientGame.WIDTH - drawWidth) / 2, (ClientGame.HEIGHT - DRAW_HEIGHT) / 2);
		
		screen.addButton(this);
	}
	
	@Override
	protected void onRelease(boolean isLeft)
	{
		super.onRelease(isLeft);
		screen.removeButton(this);
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
