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
	
	private int spot = 0;
	private static final int INTERVAL = 30;
	
	/**
	 * Filters this key for typing purposes. Does the same as isPressedFiltered(),
	 * except it will spam after being held down for an extended period of time.
	 */
	public boolean isPressedTyping()
	{
		// If it's just pressed for the first time
		if (isPressedFiltered())
		{
			// Return the filtered touch
			return true;
		}
		// Else if it's not the first time, but it is still being pressed
		else if (isPressed())
		{
			spot++;
			
			if (spot > INTERVAL)
			{
				spot = 28;
				
				return true;
			}
			
			return false;
		}
		// Else if the key is completely released, reset the times and such
		else
		{
			spot = 0;
			return false;
		}
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
