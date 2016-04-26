package ca.afroman.input;

import java.util.List;

public class Key extends InputType
{
	private int[] keyEvents;
	
	public Key(List<Key> container, int... keyEvents)
	{
		this.keyEvents = keyEvents;
		
		container.add(this);
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
