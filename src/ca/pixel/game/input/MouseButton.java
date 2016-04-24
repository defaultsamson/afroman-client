package ca.pixel.game.input;

import java.util.List;

public class MouseButton extends InputType
{
	private int buttonID;
	
	public MouseButton(List<MouseButton> container, int buttonID)
	{
		this.buttonID = buttonID;
		
		container.add(this);
	}
	
	public void update(int buttonID, boolean isPressed)
	{
		if (buttonID == this.buttonID)
		{
			setPressed(isPressed);
		}
	}
}
