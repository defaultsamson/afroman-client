package ca.pixel.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener
{
	public Key up = new Key();
	public Key down = new Key();
	public Key left = new Key();
	public Key right = new Key();
	
	public Key full_screen = new Key();
	
	public InputHandler(Game game)
	{
		game.addKeyListener(this);
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
		
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		toggleKey(e.getKeyCode(), true);
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		toggleKey(e.getKeyCode(), false);
	}
	
	public void toggleKey(int keyCode, boolean isPressed)
	{
		switch (keyCode)
		{
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				up.setPressed(isPressed);
				break;
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				down.setPressed(isPressed);
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				left.setPressed(isPressed);
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				right.setPressed(isPressed);
				break;
			case KeyEvent.VK_F11:
				full_screen.setPressed(isPressed);
				break;
		}
	}
}

class Key
{
	private boolean pressed = false;
	private boolean filteredPress = false;
	
	/**
	 * Does the same as the <b>isPressed()</b> method, but reverts to false as soon as it's used once.
	 */
	public boolean isPressedFiltered()
	{
		boolean toReturn = filteredPress;
		filteredPress= false;
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
