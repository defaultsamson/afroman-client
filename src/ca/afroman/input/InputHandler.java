package ca.afroman.input;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.client.ClientGame;
import ca.afroman.gui.GuiOptionsMenu;
import ca.afroman.option.Options;
import ca.afroman.resource.Vector2DInt;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, WindowListener, FocusListener
{
	public static String getClipboard()
	{
		try
		{
			return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
		}
		catch (HeadlessException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedFlavorException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return "";
	}
	
	private List<MouseButton> mouseButtons = new ArrayList<MouseButton>();
	public MouseButton mouseLeft = new MouseButton(mouseButtons, MouseEvent.BUTTON1);
	public MouseButton mouseMiddle = new MouseButton(mouseButtons, MouseEvent.BUTTON2);
	public MouseButton mouseRight = new MouseButton(mouseButtons, MouseEvent.BUTTON3);
	public MouseScroll mouseWheelUp = new MouseScroll();
	public MouseScroll mouseWheelDown = new MouseScroll();
	
	private Vector2DInt mousePos = new Vector2DInt(0, 0);
	
	private List<Key> keys = new ArrayList<Key>();
	public Key up = new Key(keys, Options.instance().inputUp);
	public Key down = new Key(keys, Options.instance().inputDown);
	public Key left = new Key(keys, Options.instance().inputLeft);
	public Key right = new Key(keys, Options.instance().inputRight);
	
	public Key nextItem = new Key(keys, Options.instance().inputNextItem);
	public Key prevItem = new Key(keys, Options.instance().inputPrevItem);
	
	public Key dropItem = new Key(keys, Options.instance().inputDropItem);
	public Key useItem = new Key(keys, Options.instance().inputUseItem);
	
	public Key interact = new Key(keys, Options.instance().inputInteract);
	
	public Key up_arrow = new Key(keys, KeyEvent.VK_UP);
	public Key down_arrow = new Key(keys, KeyEvent.VK_DOWN);
	public Key left_arrow = new Key(keys, KeyEvent.VK_LEFT);
	public Key right_arrow = new Key(keys, KeyEvent.VK_RIGHT);
	
	public Key escape = new Key(keys, KeyEvent.VK_ESCAPE);
	public Key enter = new Key(keys, KeyEvent.VK_ENTER);
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
	public Key shift = new Key(keys, KeyEvent.VK_SHIFT);
	public Key control = new Key(keys, KeyEvent.VK_CONTROL);
	public LockKey capsLock = new LockKey(keys, KeyEvent.VK_CAPS_LOCK);
	
	public Key zero = new Key(keys, KeyEvent.VK_0, KeyEvent.VK_NUMPAD0);
	public Key one = new Key(keys, KeyEvent.VK_1, KeyEvent.VK_NUMPAD1);
	public Key two = new Key(keys, KeyEvent.VK_2, KeyEvent.VK_NUMPAD2);
	public Key three = new Key(keys, KeyEvent.VK_3, KeyEvent.VK_NUMPAD3);
	public Key four = new Key(keys, KeyEvent.VK_4, KeyEvent.VK_NUMPAD4);
	public Key five = new Key(keys, KeyEvent.VK_5, KeyEvent.VK_NUMPAD5);
	public Key six = new Key(keys, KeyEvent.VK_6, KeyEvent.VK_NUMPAD6);
	public Key seven = new Key(keys, KeyEvent.VK_7, KeyEvent.VK_NUMPAD7);
	public Key eight = new Key(keys, KeyEvent.VK_8, KeyEvent.VK_NUMPAD8);
	public Key nine = new Key(keys, KeyEvent.VK_9, KeyEvent.VK_NUMPAD9);
	
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
	
	public Key f1 = new Key(keys, KeyEvent.VK_F1);
	public Key f2 = new Key(keys, KeyEvent.VK_F2);
	public Key f3 = new Key(keys, KeyEvent.VK_F3);
	public Key f4 = new Key(keys, KeyEvent.VK_F4);
	public Key f5 = new Key(keys, KeyEvent.VK_F5);
	public Key f6 = new Key(keys, KeyEvent.VK_F6);
	public Key f7 = new Key(keys, KeyEvent.VK_F7);
	public Key f8 = new Key(keys, KeyEvent.VK_F8);
	public Key f9 = new Key(keys, KeyEvent.VK_F9);
	public Key f10 = new Key(keys, KeyEvent.VK_F10);
	public Key f11 = new Key(keys, KeyEvent.VK_F11);
	public Key f12 = new Key(keys, KeyEvent.VK_F12);
	
	private boolean isGameFocused = true;
	
	private GuiOptionsMenu pendingMenu = null;
	
	public InputHandler(ClientGame game)
	{
		game.getCanvas().addKeyListener(this);
		game.getCanvas().addMouseListener(this);
		game.getCanvas().addMouseMotionListener(this);
		game.getCanvas().addMouseWheelListener(this);
		game.getFrame().addWindowListener(this);
		game.getCanvas().addFocusListener(this);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void focusGained(FocusEvent arg0)
	{
		// TODO pause
		isGameFocused = true;
		ClientGame.instance().updateCursorHiding(true);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void focusLost(FocusEvent arg0)
	{
		// TODO pause
		isGameFocused = false;
		ClientGame.instance().updateCursorHiding(true);
	}
	
	/**
	 * Used for setting keybinds and such
	 * 
	 * @param guiOptionsMenu
	 */
	public void getKeyPress(GuiOptionsMenu guiOptionsMenu)
	{
		pendingMenu = guiOptionsMenu;
	}
	
	public List<Key> getKeys()
	{
		return keys;
	}
	
	public Vector2DInt getMousePos()
	{
		return mousePos;
	}
	
	public boolean isGameInFocus()
	{
		return isGameFocused;
	}
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		toggleKey(e.getKeyCode(), true);
		
		setKeyPress(e.getKeyCode());
	}
	
	@Override
	public void keyReleased(KeyEvent e)
	{
		toggleKey(e.getKeyCode(), false);
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
	public void mouseDragged(MouseEvent e)
	{
		mouseMoved(e);
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
	
	@SuppressWarnings("deprecation")
	@Override
	public void mouseMoved(MouseEvent e)
	{
		if (ClientGame.instance() == null) return;
		
		// The Game resolution width / canvas width (ONLY the canvas, no black borders or JFrame)
		double xRatio = ClientGame.WIDTH / (double) ClientGame.instance().getCanvas().getWidth();
		
		// The Game resolution height / canvas height (ONLY the canvas, no black borders or JFrame)
		double yRatio = ClientGame.HEIGHT / (double) ClientGame.instance().getCanvas().getHeight();
		
		mousePos.setVector((int) (e.getX() * xRatio), (int) (e.getY() * yRatio));
		
		ClientGame.instance().updateCursorHiding(true);
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
	
	public void setClipboard(List<String> toAdd)
	{
		String[] arr = new String[toAdd.size()];
		
		for (int i = 0; i < arr.length; i++)
			arr[i] = toAdd.get(i);
		
		setClipboard(arr);
	}
	
	public void setClipboard(String... toAdd)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < toAdd.length - 1; i++)
		{
			sb.append(toAdd[i]);
			sb.append("\n");
		}
		sb.append(toAdd[toAdd.length - 1]);
		
		StringSelection stringSelection = new StringSelection(sb.toString());
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}
	
	/**
	 * Used for setting keybinds and such
	 * 
	 * @param guiOptionsMenu
	 */
	public void setKeyPress(int readKey)
	{
		if (pendingMenu != null)
		{
			pendingMenu.setReadKey(readKey);
			pendingMenu = null;
		}
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
	public void windowActivated(WindowEvent e)
	{
		
	}
	
	@Override
	public void windowClosed(WindowEvent e)
	{
		if (ClientGame.instance() != null)
		{
			ClientGame.instance().quit();
			try
			{
				Thread.sleep(5000);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			System.exit(0);
		}
	}
	
	@Override
	public void windowClosing(WindowEvent e)
	{
		windowClosed(e);
	}
	
	@Override
	public void windowDeactivated(WindowEvent e)
	{
		
	}
	
	@Override
	public void windowDeiconified(WindowEvent e)
	{
		
	}
	
	@Override
	public void windowIconified(WindowEvent e)
	{
		
	}
	
	@Override
	public void windowOpened(WindowEvent e)
	{
		
	}
}
