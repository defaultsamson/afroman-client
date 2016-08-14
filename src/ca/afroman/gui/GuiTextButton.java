package ca.afroman.gui;

import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;
import ca.afroman.resource.Vector2DInt;

public class GuiTextButton extends GuiButton
{
	protected Font font;
	private String text;
	
	public GuiTextButton(GuiScreen screen, int id, int x, int y, int width, Font font, String text)
	{
		super(screen, id, x, y, width);
		
		this.font = font;
		this.text = text;
	}
	
	public GuiTextButton(GuiScreen screen, Texture normal, Texture hover, Texture pressed, int id, int x, int y, int width, Font font, String text)
	{
		super(screen, normal, hover, pressed, id, x, y, width);
		
		this.font = font;
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}
	
	@Override
	public void render(Texture drawTo)
	{
		super.render(drawTo);
		
		font.renderCentered(drawTo, new Vector2DInt(hitbox.x + (hitbox.width / 2), hitbox.y + 4), text);
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
}
