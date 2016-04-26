package ca.afroman.input;

public class MouseScroll extends InputType
{
	/**
	 * Does the same as the <b>isPressed()</b> method, but reverts to false as soon as it's used once.
	 */
	@Override
	public boolean isPressedFiltered()
	{
		boolean toReturn = filteredPress;
		filteredPress = false;
		
		setPressed(false);
		
		return toReturn;
	}
}
