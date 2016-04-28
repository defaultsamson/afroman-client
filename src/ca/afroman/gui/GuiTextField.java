package ca.afroman.gui;

import ca.afroman.Game;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;
import ca.afroman.input.InputHandler;

public class GuiTextField extends GuiButton
{
	private Font font;
	private int maxLength = 18;
	private String text = "";
	private boolean isFocussed = false;
	
	public GuiTextField(GuiScreen screen, int x, int y)
	{
		this(screen, Assets.getTexture(Assets.TEXT_FIELD), Assets.getFont(Assets.FONT_WHITE), x, y);
	}
	
	public GuiTextField(GuiScreen screen, Texture field, Font font, int x, int y)
	{
		super(screen, field, field, field, -1, x, y);
		
		this.font = font;
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
			
			InputHandler input = Game.instance().input;
			boolean isShifting = input.shift.isPressed();
			
			if (input.backspace.isPressedTyping())
			{
				if (text.length() > 0)
				{
					text = text.substring(0, text.length() - 1);
					
					letterTyped = true;
				}
			}
			if (input.backspace.isPressed())
			{
				letterTyped = true;
			}
			
			if (input.space.isPressedTyping())
			{
				typeChar(" ");
			}
			if (input.period.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar(">");
				}
				else
				{
					typeChar(".");
				}
			}
			if (input.comma.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("<");
				}
				else
				{
					typeChar(",");
				}
			}
			if (input.slash.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("?");
				}
				else
				{
					typeChar("/");
				}
			}
			if (input.backslash.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("|");
				}
				else
				{
					typeChar("\\");
				}
			}
			if (input.semicolon.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar(":");
				}
				else
				{
					typeChar(";");
				}
			}
			if (input.hyphen.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("_");
				}
				else
				{
					typeChar("-");
				}
			}
			if (input.equals.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("+");
				}
				else
				{
					typeChar("=");
				}
			}
			
			if (input.zero.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar(")");
				}
				else
				{
					typeChar("0");
				}
			}
			if (input.one.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("!");
				}
				else
				{
					typeChar("1");
				}
			}
			if (input.two.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("@");
				}
				else
				{
					typeChar("2");
				}
			}
			if (input.three.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("#");
				}
				else
				{
					typeChar("3");
				}
			}
			if (input.four.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("$");
				}
				else
				{
					typeChar("4");
				}
			}
			if (input.five.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("%");
				}
				else
				{
					typeChar("5");
				}
			}
			if (input.six.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("^");
				}
				else
				{
					typeChar("6");
				}
			}
			if (input.seven.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("&");
				}
				else
				{
					typeChar("7");
				}
			}
			if (input.eight.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("*");
				}
				else
				{
					typeChar("8");
				}
			}
			if (input.nine.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("(");
				}
				else
				{
					typeChar("9");
				}
			}
			
			if (input.a.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("A");
				}
				else
				{
					typeChar("a");
				}
			}
			if (input.b.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("B");
				}
				else
				{
					typeChar("b");
				}
			}
			if (input.c.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("C");
				}
				else
				{
					typeChar("c");
				}
			}
			if (input.d.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("D");
				}
				else
				{
					typeChar("d");
				}
			}
			if (input.e.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("E");
				}
				else
				{
					typeChar("e");
				}
			}
			if (input.f.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("F");
				}
				else
				{
					typeChar("f");
				}
			}
			if (input.g.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("G");
				}
				else
				{
					typeChar("g");
				}
			}
			if (input.h.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("H");
				}
				else
				{
					typeChar("h");
				}
			}
			if (input.i.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("I");
				}
				else
				{
					typeChar("i");
				}
			}
			if (input.j.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("J");
				}
				else
				{
					typeChar("j");
				}
			}
			if (input.k.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("K");
				}
				else
				{
					typeChar("k");
				}
			}
			if (input.l.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("L");
				}
				else
				{
					typeChar("l");
				}
			}
			if (input.m.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("M");
				}
				else
				{
					typeChar("m");
				}
			}
			if (input.n.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("N");
				}
				else
				{
					typeChar("n");
				}
			}
			if (input.o.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("O");
				}
				else
				{
					typeChar("o");
				}
			}
			if (input.p.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("P");
				}
				else
				{
					typeChar("p");
				}
			}
			if (input.q.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("Q");
				}
				else
				{
					typeChar("q");
				}
			}
			if (input.r.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("R");
				}
				else
				{
					typeChar("r");
				}
			}
			if (input.s.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("S");
				}
				else
				{
					typeChar("s");
				}
			}
			if (input.t.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("T");
				}
				else
				{
					typeChar("t");
				}
			}
			if (input.u.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("U");
				}
				else
				{
					typeChar("u");
				}
			}
			if (input.v.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("V");
				}
				else
				{
					typeChar("v");
				}
			}
			if (input.w.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("W");
				}
				else
				{
					typeChar("w");
				}
			}
			if (input.x.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("X");
				}
				else
				{
					typeChar("x");
				}
			}
			if (input.y.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("Y");
				}
				else
				{
					typeChar("y");
				}
			}
			if (input.z.isPressedTyping())
			{
				if (isShifting)
				{
					typeChar("Z");
				}
				else
				{
					typeChar("z");
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
	
	private void typeChar(String character)
	{
		letterTyped = true;
		if (text.length() < maxLength) text += character;
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
		return text;
	}
	
	public void setText(String newText)
	{
		if (newText.length() > maxLength)
		{
			newText = newText.substring(0, maxLength);
		}
		
		this.text = newText;
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
