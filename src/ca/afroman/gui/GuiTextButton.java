package ca.afroman.gui;

import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;

public class GuiTextButton extends GuiButton
{
	private Font font;
	private String text;
	
	public GuiTextButton(GuiScreen screen, int id, int x, int y, Font font, String text)
	{
		super(screen, id, x, y);
		
		this.font = font;
		this.text = text;
	}
	
	public GuiTextButton(GuiScreen screen, Texture normal, Texture hover, Texture pressed, int id, int x, int y, Font font, String text)
	{
		super(screen, normal, hover, pressed, id, x, y);
		
		this.font = font;
		this.text = text;
	}
	
	@Override
	public void render(Texture drawTo)
	{
		super.render(drawTo);
		
		font.renderCentered(drawTo, hitbox.x + (hitbox.width / 2), hitbox.y + 4, text);
	}
}
