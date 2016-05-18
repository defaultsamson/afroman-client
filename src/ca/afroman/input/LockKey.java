package ca.afroman.input;

import java.awt.Toolkit;
import java.util.List;

public class LockKey extends Key
{
	public LockKey(List<Key> container, int... keyEvents)
	{
		super(container, keyEvents);
	}
	
	/**
	 * @return if this lock key is currently in a toggled state or not.
	 */
	public boolean isToggled()
	{
		for (int event : keyEvents)
		{
			if (Toolkit.getDefaultToolkit().getLockingKeyState(event)) return true;
		}
		return false;
	}
}
