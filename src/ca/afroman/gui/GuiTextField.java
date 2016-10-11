package ca.afroman.gui;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;
import ca.afroman.client.ClientGame;
import ca.afroman.input.InputHandler;
import ca.afroman.input.TypingKeyWrapper;
import ca.afroman.input.TypingMode;
import ca.afroman.resource.Vector2DInt;

public class GuiTextField extends GuiButton
{
	private static final int BLINK_SPEED = 20;
	private Font font;
	private int maxLength = 18;
	private StringBuilder text = new StringBuilder();
	private boolean isFocussed = false;
	
	private int textOffset = 0;
	private int cursorPosition = 0;
	
	Vector2DInt textDrawPos;
	Vector2DInt cursorDrawPos;
	
	private TypingMode mode = TypingMode.FULL;
	
	private boolean drawBlinker = false;
	private int blinkCounter = 0;
	
	public GuiTextField(GuiScreen screen, int x, int y, int width)
	{
		this(screen, Assets.getTexture(AssetType.TEXT_FIELD), Assets.getFont(AssetType.FONT_WHITE), x, y, width);
	}
	
	public GuiTextField(GuiScreen screen, Texture field, Font font, int x, int y, int width)
	{
		super(screen, field, field, field, -1, x, y, width);
		
		this.font = font;
		this.setMakeSound(false);
		
		textDrawPos = new Vector2DInt(hitbox.x + 2, hitbox.y + 4);
		cursorDrawPos = new Vector2DInt(hitbox.x + 2, hitbox.y + 4);
	}
	
	public int getCursorPosition()
	{
		return cursorPosition;
	}
	
	public String getDisplayText()
	{
		return new String(text.toString()).substring(textOffset, (text.length() - textOffset) <= maxRenderable() ? text.length() : textOffset + maxRenderable());
	}
	
	public String getText()
	{
		return text.toString();
	}
	
	private int isCursorOutsideBox(int xOrdinate)
	{
		if (xOrdinate + Font.CHAR_WIDTH + 2 > hitbox.getX() + hitbox.getWidth())
		{
			return 1;
		}
		else if (xOrdinate - 2 < hitbox.getX())
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}
	
	public boolean isFocussed()
	{
		return isFocussed;
	}
	
	public int maxRenderable()
	{
		double modded = (hitbox.getWidth() - 4) % Font.CHAR_WIDTH;
		return (int) (hitbox.getWidth() - modded) / Font.CHAR_WIDTH;
	}
	
	@Override
	protected void onPress(boolean isLeft)
	{
		this.setFocussed();
		
		ClientGame.instance().input().mouseLeft.isPressedFiltered();
		
		int x = ClientGame.instance().input().getMousePos().getX();
		for (int i = 0; i < maxRenderable(); i++)
		{
			if (x > (i * Font.CHAR_WIDTH) + textDrawPos.getX() && x <= ((i + 1) * Font.CHAR_WIDTH) + textDrawPos.getX())
			{
				// Finds what the offset should be
				if (textOffset + i <= text.length())
				{
					setCursorPosition(textOffset + i);
				}
				else
				{
					// If it can't be found, then that's probably because it's to the right of all the letter.
					// set the position to the end
					setCursorPosition(text.length());
				}
				pauseBlinker();
				return;
			}
		}
	}
	
	@Override
	protected void onRelease(boolean isLeft)
	{
		
	}
	
	private void pauseBlinker()
	{
		blinkCounter = 0;
		drawBlinker = true;
	}
	
	@Override
	public void render(Texture drawTo)
	{
		super.render(drawTo);
		
		// Draw blinker
		if (drawBlinker && text.length() < maxLength)
		{
			font.render(drawTo, cursorDrawPos.clone(), "_");
		}
		
		font.render(drawTo, textDrawPos, getDisplayText());
	}
	
	/**
	 * @param pos
	 * @return whether the cursor's position was set successfully or not.
	 */
	public boolean setCursorPosition(int pos)
	{
		if (pos >= 0 && pos <= text.length())
		{
			cursorPosition = pos;
			updateCursorDrawPos();
			// this.screen.keyTyped();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void setFocussed()
	{
		this.setFocussed(true);
	}
	
	public void setFocussed(boolean isFocussed)
	{
		if (isFocussed)
		{
			// When becoming focussed, will automatically go to the end
			cursorPosition = text.length();
			updateCursorDrawPos();
			
			screen.unfocusTextFields();
		}
		this.isFocussed = isFocussed;
	}
	
	public void setMaxLength(int newMax)
	{
		maxLength = newMax;
	}
	
	public void setText(String newText)
	{
		if (newText.length() > maxLength)
		{
			newText = newText.substring(0, maxLength);
		}
		
		text = new StringBuilder();
		text.append(newText);
	}
	
	public void setTypingMode(TypingMode newMode)
	{
		mode = newMode;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (isFocussed)
		{
			// Times the blinking of the line at the end
			blinkCounter++;
			if (blinkCounter > BLINK_SPEED)
			{
				blinkCounter = 0;
				drawBlinker = !drawBlinker;
			}
			
			InputHandler input = ClientGame.instance().input();
			boolean isShifting = input.shift.isPressed() || input.capsLock.isToggled();
			
			if (input.backspace.isPressed())
			{
				pauseBlinker();
			}
			if (input.left.isPressed())
			{
				pauseBlinker();
			}
			if (input.right.isPressed())
			{
				pauseBlinker();
			}
			if (input.control.isPressed() && input.v.isPressedFiltered())
			{
				typeChar(InputHandler.getClipboard());
			}
			
			for (TypingKeyWrapper t : TypingMode.getKeyModes())
			{
				if (t.getKey().isPressedTyping())
				{
					if (typeChar(t.getTypedChar(isShifting, mode)))
					{
						pauseBlinker();
					}
					this.screen.keyTyped();
				}
			}
			
			if (text.length() > 0)
			{
				if (input.backspace.isPressedTyping())
				{
					if (setCursorPosition(cursorPosition - 1))
					{
						text.deleteCharAt(cursorPosition);
						this.screen.keyTyped();
					}
				}
				if (input.left_arrow.isPressedTyping())
				{
					setCursorPosition(cursorPosition - 1);
				}
				if (input.right_arrow.isPressedTyping())
				{
					setCursorPosition(cursorPosition + 1);
				}
			}
		}
		else
		{
			blinkCounter = 0;
			drawBlinker = false;
		}
	}
	
	/**
	 * @param character
	 * @return whether the character was successfully typed or not.
	 */
	private boolean typeChar(String character)
	{
		if (character.length() > 0)
		{
			if (text.length() < maxLength)
			{
				text.insert(cursorPosition, character);
				
				if (text.length() > maxLength)
				{
					text = new StringBuilder(text.substring(0, maxLength));
					setCursorPosition(text.length());
				}
				else
				{
					setCursorPosition(cursorPosition + character.length());
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	private Vector2DInt updateCursorDrawPos()
	{
		cursorDrawPos = textDrawPos.clone().add((cursorPosition - textOffset) * Font.CHAR_WIDTH, 1);
		
		switch (isCursorOutsideBox(cursorDrawPos.getX()))
		{
			default:
				return cursorDrawPos;
			case 1:
				textOffset += 1;
				return updateCursorDrawPos();
			case -1:
				textOffset -= 1;
				return updateCursorDrawPos();
		}
	}
}
