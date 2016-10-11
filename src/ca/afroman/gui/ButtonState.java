package ca.afroman.gui;

public enum ButtonState
{
	NONE(0),
	HOVERING(1),
	LEFT_PRESSED(2),
	RIGHT_PRESSED(2);
	
	private int drawIndex;
	
	ButtonState(int drawIdex)
	{
		this.drawIndex = drawIdex;
	}
	
	public int getDrawIndex()
	{
		return drawIndex;
	}
}
