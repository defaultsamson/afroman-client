package ca.afroman.input;

import java.util.List;

public class LockKey extends Key
{
	private boolean isToggled;
	
	public LockKey(List<Key> container, int... keyEvents)
	{
		super(container, keyEvents);
		
		isToggled = false;
	}
	
	/**
	 * @return if this lock key is currently in a toggled state or not.
	 */
	public boolean isToggled()
	{
		// for (int event : keyEvents)
		// {
		// if (Toolkit.getDefaultToolkit().getLockingKeyState(event)) return true;
		// }
		return isToggled;
	}
	
	@Override
	public void update(int keyCode, boolean isPressed)
	{
		super.update(keyCode, isPressed);
		
		if (isPressedFiltered())
		{
			isToggled = !isToggled;
		}
	}
}
