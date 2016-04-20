package ca.pixel.game.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import ca.pixel.game.Game;

public class InputHandler implements KeyListener
{
	private List<Key> keys = new ArrayList<Key>();
	
	public Key up = new Key(KeyEvent.VK_UP, KeyEvent.VK_W);
	public Key down = new Key(KeyEvent.VK_DOWN, KeyEvent.VK_S);
	public Key left = new Key(KeyEvent.VK_LEFT, KeyEvent.VK_A);
	public Key right = new Key(KeyEvent.VK_RIGHT, KeyEvent.VK_D);
	
	public Key full_screen = new Key(KeyEvent.VK_F11);
	public Key debug = new Key(KeyEvent.VK_F12);
	
	public Key e = new Key(KeyEvent.VK_E);
	public Key q = new Key(KeyEvent.VK_Q);
	
	public InputHandler(Game game)
	{
		game.addKeyListener(this);
		keys.add(up);
		keys.add(down);
		keys.add(left);
		keys.add(right);
		keys.add(full_screen);
		keys.add(debug);
		keys.add(e);
		keys.add(q);
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
		for (Key key : keys)
		{
			key.update(keyCode, isPressed);
		}
	}
}
