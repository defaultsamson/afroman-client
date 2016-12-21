package ca.afroman.input;

import java.util.List;

public class Key extends InputType
{
	protected int[] keyEvents;
	
	public Key(List<Key> container, int... keyEvents)
	{
		this.keyEvents = keyEvents;
		
		container.add(this);
	}
	
	public int getKeyEvent()
	{
		return keyEvents[0];
	}
	
	public void setKeyEvents(int key)
	{
		keyEvents[0] = key;
	}
	
	public void update(int keyCode, boolean isPressed)
	{
		for (int key : keyEvents)
		{
			if (keyCode == key)
			{
				setPressed(isPressed);
				return;
			}
		}
	}
}
