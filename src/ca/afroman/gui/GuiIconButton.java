package ca.afroman.gui;

import ca.afroman.assets.DrawableAsset;
import ca.afroman.assets.SpriteAnimation;
import ca.afroman.assets.StepSpriteAnimation;
import ca.afroman.assets.Texture;
import ca.afroman.interfaces.ITickable;
import ca.afroman.resource.Vector2DInt;

public class GuiIconButton extends GuiButton
{
	private DrawableAsset icon;
	
	public GuiIconButton(GuiScreen screen, int id, int x, int y, int width, DrawableAsset icon)
	{
		super(screen, id, x, y, width);
		
		this.icon = icon;
	}
	
	public GuiIconButton(GuiScreen screen, Texture normal, Texture hover, Texture pressed, int id, int x, int y, int width, DrawableAsset icon)
	{
		super(screen, normal, hover, pressed, id, x, y, width);
		
		this.icon = icon;
	}
	
	public DrawableAsset getIcon()
	{
		return icon;
	}
	
	@Override
	public void onHover()
	{
		super.onHover();
		
		if (isEnabled())
		{
			if (icon instanceof StepSpriteAnimation)
			{
				((StepSpriteAnimation) icon).progress();
			}
		}
	}
	
	@Override
	public void onRelease(boolean isLeft)
	{
		super.onRelease(isLeft);
		
		if (icon instanceof SpriteAnimation)
		{
			((SpriteAnimation) icon).setFrame(0);
		}
	}
	
	@Override
	public void render(Texture drawTo)
	{
		super.render(drawTo);
		
		icon.render(drawTo, new Vector2DInt(hitbox.x, hitbox.y));
	}
	
	public void setIcon(DrawableAsset icon)
	{
		this.icon = icon;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (icon instanceof ITickable) ((ITickable) icon).tick();
	}
}
