package ca.pixel.game.input;

public class InputType
{
	protected boolean pressed = false;
	protected boolean filteredPress = false;
	
	protected void setPressed(boolean isPressed)
	{
		filteredPress = (!pressed && isPressed);
		pressed = isPressed;
	}
	
	/**
	 * Does the same as the <b>isPressed()</b> method, but reverts to false as soon as it's used once.
	 */
	public boolean isPressedFiltered()
	{
		boolean toReturn = filteredPress;
		filteredPress = false;
		return toReturn;
	}
	
	public boolean isPressed()
	{
		return pressed;
	}
}
