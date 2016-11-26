package ca.afroman.input;

public class InputType
{
	private static final int INTERVAL = 30;
	protected boolean pressed = false;
	protected boolean filteredPress = false;
	protected boolean filteredRelease = false;
	
	private int spot = 0;
	
	public boolean isPressed()
	{
		return pressed;
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
				spot = INTERVAL - 2;
				
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
	
	public boolean isReleased()
	{
		return !pressed;
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
	
	public void setPressed(boolean isPressed)
	{
		filteredPress = (!pressed && isPressed);
		filteredRelease = (pressed && !isPressed);
		pressed = isPressed;
	}
}
