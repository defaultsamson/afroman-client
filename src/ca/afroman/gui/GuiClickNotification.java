package ca.afroman.gui;

import java.awt.Color;

import ca.afroman.Game;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;

public class GuiClickNotification extends GuiTextButton
{
	private String notifText1;
	private String notifText2;
	
	public GuiClickNotification(GuiScreen screen, String text)
	{
		this(screen, text, "");
	}
	
	public GuiClickNotification(GuiScreen screen, String text1, String text2)
	{
		super(screen, -1, (Game.WIDTH / 2) - 36, (Game.HEIGHT / 2) + 10, Assets.getFont(Assets.FONT_BLACK), "Okay");
		
		this.notifText1 = text1;
		this.notifText2 = text2;
		
		screen.buttons.add(this);
	}
	
	@Override
	public void render(Texture drawTo)
	{
		drawTo.getGraphics().setPaint(new Color(1F, 1F, 1F, 1F));
		drawTo.getGraphics().fillRect(hitbox.x - 10, hitbox.y - 35, hitbox.width + 20 - 1, hitbox.height + 40);
		drawTo.getGraphics().setPaint(new Color(0F, 0F, 0F, 1F));
		drawTo.getGraphics().drawRect(hitbox.x - 10, hitbox.y - 35, hitbox.width + 20 - 1, hitbox.height + 40);
		
		font.renderCentered(drawTo, hitbox.x + (hitbox.width / 2), hitbox.y - 26, notifText1);
		font.renderCentered(drawTo, hitbox.x + (hitbox.width / 2), hitbox.y - 14, notifText2);
		
		super.render(drawTo);
	}
	
	@Override
	protected void onPressed()
	{
		
	}
	
	@Override
	protected void onRelease()
	{
		screen.removeButton(this);
	}
}
