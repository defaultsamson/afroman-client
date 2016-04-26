package ca.afroman.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.game.Game;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	private List<MouseButton> mouseButtons = new ArrayList<MouseButton>();
	
	public MouseButton mouseLeft = new MouseButton(mouseButtons, MouseEvent.BUTTON1);
	public MouseButton mouseMiddle = new MouseButton(mouseButtons, MouseEvent.BUTTON2);
	public MouseButton mouseRight = new MouseButton(mouseButtons, MouseEvent.BUTTON3);
	public MouseScroll mouseWheelUp = new MouseScroll();
	public MouseScroll mouseWheelDown = new MouseScroll();
	private int mouseX = 0;
	private int mouseY = 0;
	
	private List<Key> keys = new ArrayList<Key>();
	
	public Key up = new Key(keys, KeyEvent.VK_UP, KeyEvent.VK_W);
	public Key down = new Key(keys, KeyEvent.VK_DOWN, KeyEvent.VK_S);
	public Key left = new Key(keys, KeyEvent.VK_LEFT, KeyEvent.VK_A);
	public Key right = new Key(keys, KeyEvent.VK_RIGHT, KeyEvent.VK_D);
	
	public Key full_screen = new Key(keys, KeyEvent.VK_F11);
	public Key hudDebug = new Key(keys, KeyEvent.VK_F1);
	public Key hitboxDebug = new Key(keys, KeyEvent.VK_F2);
	public Key lightingDebug = new Key(keys, KeyEvent.VK_F3);
	public Key saveLevel = new Key(keys, KeyEvent.VK_F4);
	public Key levelBuilder = new Key(keys, KeyEvent.VK_F12);
	public Key consoleDebug = new Key(keys, KeyEvent.VK_F10);
	
	public Key e = new Key(keys, KeyEvent.VK_E);
	public Key q = new Key(keys, KeyEvent.VK_Q);
	
	public Key shift = new Key(keys, KeyEvent.VK_SHIFT);
	
	public InputHandler(Game game)
	{
		game.addKeyListener(this);
		game.addMouseListener(this);
		game.addMouseMotionListener(this);
		game.addMouseWheelListener(this);
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
	
	public void toggleMouseButton(int buttonID, boolean isPressed)
	{
		for (MouseButton butt : mouseButtons)
		{
			butt.update(buttonID, isPressed);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		mouseMoved(e);
	}
	
	@Override
	public void mouseMoved(MouseEvent e)
	{
		// The Game resolution width / canvas width (ONLY the canvas, no black borders or JFrame)
		double xRatio = (double) Game.WIDTH / (double) Game.instance().getWidth();
		mouseX = (int) (e.getX() * xRatio);
		
		// The Game resolution height / canvas height (ONLY the canvas, no black borders or JFrame)
		double yRatio = (double) Game.HEIGHT / (double) Game.instance().getHeight();
		mouseY = (int) (e.getY() * yRatio);
		
		// System.out.println(mouseX + ", " + mouseY);
	}
	
	/**
	 * @return gets the mouse x position of in-game coordinates
	 */
	public int getMouseX()
	{
		return mouseX;
	}
	
	/**
	 * @return gets the mouse x position of in-game coordinates
	 */
	public int getMouseY()
	{
		return mouseY;
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
		toggleMouseButton(e.getButton(), true);
	}
	
	@Override
	public void mouseReleased(MouseEvent e)
	{
		toggleMouseButton(e.getButton(), false);
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		// System.out.println(e.getWheelRotation());
		
		mouseWheelDown.setPressed((e.getWheelRotation() > 0));
		mouseWheelUp.setPressed((e.getWheelRotation() < 0));
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
		mouseMoved(e);
	}
	
	@Override
	public void mouseExited(MouseEvent e)
	{
		mouseMoved(e);
	}
	
	@Override
	public void keyTyped(KeyEvent e)
	{
		// Not needed
	}
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		// Not needed
	}
}
