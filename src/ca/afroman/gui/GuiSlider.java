package ca.afroman.gui;

import java.awt.Color;
import java.awt.Paint;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.resource.Vector2DInt;

public class GuiSlider extends GuiButton
{
	private static final int EDGE_BOUNDS = 2;
	private static final Color SLIDER_COLOUR = new Color(0.5F, 0.5F, 0.5F, 1.0F);
	private static final Color SLIDER_TRANS = new Color(1F, 1F, 1F, 0.1F);
	
	private Font font;
	private int minValue;
	private int maxValue;
	private int value = 0;
	private Vector2DInt textDrawPos;
	private String tag;
	
	public GuiSlider(GuiScreen screen, int id, int x, int y, int width, int min, int max, int value, String tag)
	{
		this(screen, Assets.getTexture(AssetType.TEXT_FIELD), Assets.getFont(AssetType.FONT_WHITE), id, x, y, width, min, max, value, tag);
	}
	
	public GuiSlider(GuiScreen screen, Texture field, Font font, int id, int x, int y, int width, int min, int max, int value, String tag)
	{
		super(screen, field, field, field, id, x, y, width);
		
		this.font = font;
		this.minValue = min;
		this.maxValue = max;
		setValue(value);
		textDrawPos = new Vector2DInt(x + (width / 2), y + 4);
		this.tag = tag;
	}
	
	public int getMaxValue()
	{
		return maxValue;
	}
	
	public int getMinValue()
	{
		return minValue;
	}
	
	public int getValue()
	{
		return value;
	}
	
	@Override
	protected void onPress()
	{
		// Need this here, otherwise it will do onPress behaviour from the super method
	}
	
	@Override
	protected void onRelease()
	{
		// Need this here, otherwise it will do onPress behaviour from the super method
	}
	
	@Override
	public void render(Texture drawTo)
	{
		super.render(drawTo);
		
		// Draws the line
		int maxPercent = (maxValue - minValue);
		int x = hitbox.x + (int) ((double) (value - minValue) / (double) maxPercent * (hitbox.getWidth() - EDGE_BOUNDS - 1)) + 1;
		int y = hitbox.y + 1;
		int endY = hitbox.y + hitbox.height - 2;
		
		Paint oldPaint = drawTo.getGraphics().getPaint();
		
		drawTo.getGraphics().setPaint(SLIDER_TRANS);
		drawTo.getGraphics().fillRect(hitbox.x + 1, y, x - hitbox.x, hitbox.height - 2);
		
		drawTo.getGraphics().setPaint(SLIDER_COLOUR);
		drawTo.getGraphics().drawLine(x, y, x, endY);
		
		drawTo.getGraphics().setPaint(oldPaint);
		
		font.renderCentered(drawTo, textDrawPos, (tag.length() > 0 ? tag + ": " : "") + value);
	}
	
	public void setMaxValue(int value)
	{
		this.maxValue = value;
	}
	
	public void setMinValue(int value)
	{
		this.minValue = value;
	}
	
	public void setValue(int value)
	{
		// If it's not the same value, then update
		if (value != this.value)
		{
			// Ensures that the calculated value isn't out of the definaed range
			int newValue = Math.max(Math.min(value, maxValue), minValue);
			this.value = newValue;
			this.screen.updateValue(getID(), this.value);
		}
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (this.isEnabled())
		{
			if (isPressed())
			{
				// When it's pressed, get the new value based on the mouse position
				int clickX = ClientGame.instance().input().getMousePos().getX() - hitbox.x - (EDGE_BOUNDS - 1);
				
				// calculates the new value based on where the hitbox is being clicked
				int calcValue = (int) Math.round((clickX / ((double) (hitbox.width - EDGE_BOUNDS - 1) / (double) (maxValue - minValue))) + minValue);
				
				setValue(calcValue);
			}
			if (isHovering())
			{
				if (ClientGame.instance().input().mouseWheelUp.isPressedFiltered())
				{
					setValue(getValue() + 1);
				}
				if (ClientGame.instance().input().mouseWheelDown.isPressedFiltered())
				{
					setValue(getValue() - 1);
				}
			}
		}
	}
}
