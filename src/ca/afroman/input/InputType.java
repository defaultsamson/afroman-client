package ca.afroman.input;

public class InputType
{
	protected boolean pressed = false;
	protected boolean filteredPress = false;
	protected boolean filteredRelease = false;
	
	protected void setPressed(boolean isPressed)
	{
		filteredPress = (!pressed && isPressed);
		filteredRelease = (pressed && !isPressed);
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
	
	/**
	 * Does the same as the <b>isReleased()</b> method, but reverts to false as soon as it's used once.
	 */
	public boolean isReleasedFiltered()
	{
		boolean toReturn = filteredRelease;
		filteredRelease = false;
		return toReturn;
	}
	
	public boolean isPressed()
	{
		return pressed;
	}
	
	public boolean isReleased()
	{
		return !pressed;
	}
}
