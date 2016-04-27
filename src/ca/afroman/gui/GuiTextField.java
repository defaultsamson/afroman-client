package ca.afroman.gui;

import ca.afroman.Game;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;

public class GuiTextField extends GuiButton
{
	private Font font;
	private int maxLength = 32;
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
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (isFocussed)
		{
			if (Game.instance().input.backspace.isPressedFiltered())
			{
				if (text.length() > 0)
				{
					text = text.substring(0, text.length() - 1);
				}
			}
			
			if (Game.instance().input.space.isPressedFiltered())
			{
				if (text.length() < maxLength) text += " ";
			}
			if (Game.instance().input.a.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "a";
			}
			if (Game.instance().input.b.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "b";
			}
			if (Game.instance().input.c.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "c";
			}
			if (Game.instance().input.d.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "d";
			}
			if (Game.instance().input.e.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "e";
			}
			if (Game.instance().input.f.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "f";
			}
			if (Game.instance().input.g.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "g";
			}
			if (Game.instance().input.h.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "h";
			}
			if (Game.instance().input.i.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "i";
			}
			if (Game.instance().input.j.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "j";
			}
			if (Game.instance().input.k.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "k";
			}
			if (Game.instance().input.l.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "l";
			}
			if (Game.instance().input.m.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "m";
			}
			if (Game.instance().input.n.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "n";
			}
			if (Game.instance().input.o.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "o";
			}
			if (Game.instance().input.p.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "p";
			}
			if (Game.instance().input.q.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "q";
			}
			if (Game.instance().input.r.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "r";
			}
			if (Game.instance().input.s.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "s";
			}
			if (Game.instance().input.t.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "t";
			}
			if (Game.instance().input.u.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "u";
			}
			if (Game.instance().input.v.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "v";
			}
			if (Game.instance().input.w.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "w";
			}
			if (Game.instance().input.x.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "x";
			}
			if (Game.instance().input.y.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "y";
			}
			if (Game.instance().input.z.isPressedFiltered())
			{
				if (text.length() < maxLength) text += "z";
			}
		}
	}
	
	public void setFocussed()
	{
		this.setFocussed(true);
	}
	
	public void setFocussed(boolean isFocussed)
	{
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
		screen.unfocusTextFields();
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
		font.render(drawTo, hitbox.x + 2, hitbox.y + 4, text);
	}
}
