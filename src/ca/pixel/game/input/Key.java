package ca.pixel.game.input;

public class Key
{
	private boolean pressed = false;
	private boolean filteredPress = false;
	
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
	
	public void setPressed(boolean isPressed)
	{
		filteredPress = (!pressed && isPressed);
		pressed = isPressed;
	}
}
