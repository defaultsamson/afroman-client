package ca.afroman.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.client.ClientGame;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, WindowListener
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
	
	public Key escape = new Key(keys, KeyEvent.VK_ESCAPE);
	public Key tab = new Key(keys, KeyEvent.VK_TAB);
	public Key delete = new Key(keys, KeyEvent.VK_DELETE);
	public Key backspace = new Key(keys, KeyEvent.VK_BACK_SPACE);
	public Key space = new Key(keys, KeyEvent.VK_SPACE);
	public Key period = new Key(keys, KeyEvent.VK_PERIOD);
	public Key comma = new Key(keys, KeyEvent.VK_COMMA);
	public Key slash = new Key(keys, KeyEvent.VK_SLASH);
	public Key backslash = new Key(keys, KeyEvent.VK_BACK_SLASH);
	public Key semicolon = new Key(keys, KeyEvent.VK_SEMICOLON);
	public Key hyphen = new Key(keys, KeyEvent.VK_MINUS);
	public Key equals = new Key(keys, KeyEvent.VK_EQUALS);
	
	public Key zero = new Key(keys, KeyEvent.VK_0);
	public Key one = new Key(keys, KeyEvent.VK_1);
	public Key two = new Key(keys, KeyEvent.VK_2);
	public Key three = new Key(keys, KeyEvent.VK_3);
	public Key four = new Key(keys, KeyEvent.VK_4);
	public Key five = new Key(keys, KeyEvent.VK_5);
	public Key six = new Key(keys, KeyEvent.VK_6);
	public Key seven = new Key(keys, KeyEvent.VK_7);
	public Key eight = new Key(keys, KeyEvent.VK_8);
	public Key nine = new Key(keys, KeyEvent.VK_9);
	
	public Key a = new Key(keys, KeyEvent.VK_A);
	public Key b = new Key(keys, KeyEvent.VK_B);
	public Key c = new Key(keys, KeyEvent.VK_C);
	public Key d = new Key(keys, KeyEvent.VK_D);
	public Key e = new Key(keys, KeyEvent.VK_E);
	public Key f = new Key(keys, KeyEvent.VK_F);
	public Key g = new Key(keys, KeyEvent.VK_G);
	public Key h = new Key(keys, KeyEvent.VK_H);
	public Key i = new Key(keys, KeyEvent.VK_I);
	public Key j = new Key(keys, KeyEvent.VK_J);
	public Key k = new Key(keys, KeyEvent.VK_K);
	public Key l = new Key(keys, KeyEvent.VK_L);
	public Key m = new Key(keys, KeyEvent.VK_M);
	public Key n = new Key(keys, KeyEvent.VK_N);
	public Key o = new Key(keys, KeyEvent.VK_O);
	public Key p = new Key(keys, KeyEvent.VK_P);
	public Key q = new Key(keys, KeyEvent.VK_Q);
	public Key r = new Key(keys, KeyEvent.VK_R);
	public Key s = new Key(keys, KeyEvent.VK_S);
	public Key t = new Key(keys, KeyEvent.VK_T);
	public Key u = new Key(keys, KeyEvent.VK_U);
	public Key v = new Key(keys, KeyEvent.VK_V);
	public Key w = new Key(keys, KeyEvent.VK_W);
	public Key x = new Key(keys, KeyEvent.VK_X);
	public Key y = new Key(keys, KeyEvent.VK_Y);
	public Key z = new Key(keys, KeyEvent.VK_Z);
	
	public Key shift = new Key(keys, KeyEvent.VK_SHIFT);
	public Key control = new Key(keys, KeyEvent.VK_CONTROL);
	
	public InputHandler(ClientGame game)
	{
		game.getCanvas().addKeyListener(this);
		game.getCanvas().addMouseListener(this);
		game.getCanvas().addMouseMotionListener(this);
		game.getCanvas().addMouseWheelListener(this);
		game.getFrame().addWindowListener(this);
	}
	
	@Override
	public void windowClosing(WindowEvent e)
	{
		windowClosed(e);
	}
	
	@Override
	public void windowClosed(WindowEvent e)
	{
		// Stops the client properly when being closed
		ClientGame.instance().stopThread();
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
		if (ClientGame.instance() == null) return;
		
		// The Game resolution width / canvas width (ONLY the canvas, no black borders or JFrame)
		double xRatio = ClientGame.WIDTH / (double) ClientGame.instance().getCanvas().getWidth();
		mouseX = (int) (e.getX() * xRatio);
		
		// The Game resolution height / canvas height (ONLY the canvas, no black borders or JFrame)
		double yRatio = ClientGame.HEIGHT / (double) ClientGame.instance().getCanvas().getHeight();
		mouseY = (int) (e.getY() * yRatio);
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
	
	@Override
	public void windowOpened(WindowEvent e)
	{
		
	}
	
	@Override
	public void windowIconified(WindowEvent e)
	{
		
	}
	
	@Override
	public void windowDeiconified(WindowEvent e)
	{
		
	}
	
	@Override
	public void windowActivated(WindowEvent e)
	{
		
	}
	
	@Override
	public void windowDeactivated(WindowEvent e)
	{
		
	}
}
