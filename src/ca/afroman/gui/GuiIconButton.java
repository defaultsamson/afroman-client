package ca.afroman.gui;

import ca.afroman.assets.Texture;
import ca.afroman.interfaces.IRenderable;
import ca.afroman.resource.Vector2DInt;

public class GuiIconButton extends GuiButton
{
	private IRenderable icon;
	
	public GuiIconButton(GuiScreen screen, int id, int x, int y, int width, IRenderable icon)
	{
		super(screen, id, x, y, width);
		
		this.icon = icon;
	}
	
	public GuiIconButton(GuiScreen screen, Texture normal, Texture hover, Texture pressed, int id, int x, int y, int width, IRenderable icon)
	{
		super(screen, normal, hover, pressed, id, x, y, width);
		
		this.icon = icon;
	}
	
	public IRenderable getIcon()
	{
		return icon;
	}
	
	@Override
	public void render(Texture drawTo)
	{
		super.render(drawTo);
		
		icon.render(drawTo, new Vector2DInt(hitbox.x, hitbox.y));
	}
	
	public void setIcon(IRenderable icon)
	{
		this.icon = icon;
	}
}
