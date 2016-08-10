package ca.afroman.gui;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.input.InputHandler;
import ca.afroman.input.TypingKeyWrapper;
import ca.afroman.input.TypingMode;

public class GuiTextField extends GuiButton
{
	private Font font;
	private int maxLength = 18;
	private StringBuilder text = new StringBuilder();
	private boolean isFocussed = false;
	private TypingMode mode = TypingMode.FULL;
	
	public GuiTextField(GuiScreen screen, int x, int y, int width)
	{
		this(screen, Assets.getTexture(AssetType.TEXT_FIELD), Assets.getFont(AssetType.FONT_WHITE), x, y, width);
	}
	
	public GuiTextField(GuiScreen screen, Texture field, Font font, int x, int y, int width)
	{
		super(screen, field, field, field, -1, x, y, width);
		
		this.font = font;
		this.setMakeSound(false);
	}
	
	public void setMaxLength(int newMax)
	{
		maxLength = newMax;
	}
	
	private boolean drawBlinker = false;
	private int blinkCounter = 0;
	private static final int BLINK_SPEED = 20;
	private boolean letterTyped = false;
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (isFocussed)
		{
			// Times the blinking of the line at the end
			blinkCounter++;
			letterTyped = false;
			if (blinkCounter > BLINK_SPEED)
			{
				blinkCounter = 0;
				drawBlinker = !drawBlinker;
			}
			
			InputHandler input = ClientGame.instance().input();
			boolean isShifting = input.shift.isPressed() || input.capsLock.isToggled();
			
			for (TypingKeyWrapper t : TypingMode.getKeyModes())
			{
				if (t.getKey().isPressedTyping())
				{
					typeChar(t.getTypedChar(isShifting, mode));
				}
			}
			
			if (input.backspace.isPressedTyping())
			{
				if (text.length() > 0)
				{
					text.deleteCharAt(text.length() - 1);
					// text = text.substring(0, text.length() - 1);
					
					letterTyped = true;
				}
			}
			if (input.backspace.isPressed())
			{
				if (text.length() > 0)
				{
					letterTyped = true;
				}
			}
			
			if (letterTyped)
			{
				blinkCounter = 0;
				drawBlinker = true;
			}
		}
		else
		{
			blinkCounter = 0;
			drawBlinker = false;
		}
		
		if (letterTyped)
		{
			this.screen.keyTyped();
		}
	}
	
	private void typeChar(String character, TypingMode... modes)
	{
		if (character.length() > 0)
		{
			letterTyped = true;
			if (text.length() < maxLength) text.append(character);
		}
	}
	
	public void setTypingMode(TypingMode newMode)
	{
		mode = newMode;
	}
	
	public void setFocussed()
	{
		this.setFocussed(true);
	}
	
	public void setFocussed(boolean isFocussed)
	{
		if (isFocussed) screen.unfocusTextFields();
		this.isFocussed = isFocussed;
	}
	
	public boolean isFocussed()
	{
		return isFocussed;
	}
	
	public String getText()
	{
		return text.toString();
	}
	
	public void setText(String newText)
	{
		if (newText.length() > maxLength)
		{
			newText = newText.substring(0, maxLength);
		}
		
		text = new StringBuilder();
		text.append(newText);
		
		// this.text = newText;
	}
	
	@Override
	protected void onPressed()
	{
		this.setFocussed();
	}
	
	@Override
	protected void onRelease()
	{
		
	}
	
	@Override
	public void render(Texture drawTo)
	{
		super.render(drawTo);
		
		String displayText = text + (drawBlinker && text.length() < maxLength ? "_" : "");
		
		font.render(drawTo, hitbox.x + 2, hitbox.y + 4, displayText);
	}
}
