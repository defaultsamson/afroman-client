package ca.afroman.gui;

import java.awt.Color;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.resource.Vector2DInt;

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
		super(screen, -1, (ClientGame.WIDTH / 2) - 36, (ClientGame.HEIGHT / 2) + 10, 72, Assets.getFont(AssetType.FONT_BLACK), "Okay");
		
		this.notifText1 = text1;
		this.notifText2 = text2;
		
		screen.buttons.add(this);
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
	
	@Override
	public void render(Texture drawTo)
	{
		drawTo.drawFillRect(new Color(0F, 0F, 0F, 1F), new Color(1F, 1F, 1F, 1F), new Vector2DInt(hitbox.x - 10, hitbox.y - 35), hitbox.width + 20, hitbox.height + 41);
		
		font.renderCentered(drawTo, new Vector2DInt(hitbox.x + (hitbox.width / 2), hitbox.y - 26), notifText1);
		font.renderCentered(drawTo, new Vector2DInt(hitbox.x + (hitbox.width / 2), hitbox.y - 14), notifText2);
		
		super.render(drawTo);
	}
}
