package ca.afroman;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import ca.afroman.assets.Texture;
import ca.afroman.console.ConsoleOutput;
import ca.afroman.entity.Level;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.gui.GuiScreen;
import ca.afroman.input.InputHandler;
import ca.afroman.network.GameClient;
import ca.afroman.server.GameServer;

public class Game extends Canvas implements Runnable
{
	private static final long serialVersionUID = 1L;
	
	private static Game game;
	
	public static Game instance()
	{
		return game;
	}
	
	public static final int WIDTH = 240;
	public static final int HEIGHT = WIDTH / 16 * 9;
	public static final int SCALE = 3;
	public static final String NAME = "Cancer: The Adventures of Afro Man";
	
	private JFrame frame;
	
	private Texture screen = new Texture(new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB));
	
	private boolean fullscreen = false;
	private boolean hudDebug = false; // Shows debug information on the hud
	private boolean hitboxDebug = false; // Shows all hitboxes
	private boolean lightingDebug = false; // Turns off the lighting engine
	private boolean buildMode = false; // Turns off the lighting engine
	private boolean consoleDebug = false; // Shows a console window
	
	public boolean isHosting = false;
	
	public boolean running = false;
	public int tickCount = 0;
	public int tps = 0;
	public int fps = 0;
	
	public InputHandler input = new InputHandler(this);
	
	public Level blankLevel;
	public PlayerEntity player;
	
	private String username = "";
	
	public GameClient socketClient;
	public GameServer socketServer;
	
	private GuiScreen currentScreen = null;
	
	public Game()
	{
		setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		
		frame = new JFrame(NAME);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.getContentPane().setBackground(Color.black);
		
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		this.setFocusTraversalKeysEnabled(false);
		
		ConsoleOutput.createGui();
		ConsoleOutput.showGui();
		ConsoleOutput.hideGui();
	}
	
	public void init()
	{
		socketClient = new GameClient();
		socketClient.start();
		
		setCurrentScreen(new GuiMainMenu(this));
		
		// TODO this stuff is fully functional. Add to the gui
		// String ip = "localhost";
		// String pass = "";
		//
		// if (JOptionPane.showConfirmDialog(this, "Do you want to run the server?") == 0)
		// {
		// pass = JOptionPane.showInputDialog(this, "Please create a password (Leave blank for no password)");
		//
		// socketServer = new GameServer(pass);
		// socketServer.start();
		// isHosting = true;
		// }
		// else
		// {
		// ip = JOptionPane.showInputDialog("What is the server's IP");
		// }
		//
		// socketClient = new GameClient();
		// socketClient.start();
		//
		// socketClient.setServerIP(ip);
		// socketClient.sendPacket(new PacketRequestConnection(pass));
		//
		
		/*
		 * blankLevel = Level.fromFile("/level1.txt");
		 * String ip = "localhost";
		 * String pass = "hooplah";
		 * if (JOptionPane.showConfirmDialog(this, "Do you want to run the server?") == 0)
		 * {
		 * socketServer = new GameServer(this);
		 * socketServer.start();
		 * isHosting = true;
		 * }
		 * else
		 * {
		 * ip = JOptionPane.showInputDialog("What is the server's IP");
		 * pass = JOptionPane.showInputDialog("What is the server's password?");
		 * }
		 * socketClient = new GameClient(this, ip);
		 * socketClient.start();
		 * player = new PlayerMPEntity(100, 120, 1, input, null, -1);
		 * player.addToLevel(blankLevel);
		 * player.setCameraToFollow(true);
		 * PacketLogin login = new PacketLogin(pass);
		 * login.writeData(socketClient);
		 * // player = new PlayerMPEntity(blankLevel, 100, 100, 1, input, null, -1);
		 * // player.setCameraToFollow(true);
		 * // blankLevel.putPlayer();
		 */
	}
	
	@Override
	public void validate()
	{
		super.validate();
		
		resizeGame(this.getWidth(), this.getHeight());
	}
	
	/**
	 * Operation to perform to resize the game, keeping the aspect ratio.
	 * 
	 * @param windowWidth the new desired width.
	 * @param windowHeight the new desired height.
	 */
	public void resizeGame(int windowWidth, int windowHeight)
	{
		int newWidth = 0;
		int newHeight = 0;
		
		// If what the drawn height should be based on the width goes off screen
		if (windowWidth / 16 * 9 > windowHeight)
		{
			newWidth = windowHeight / 9 * 16;
			newHeight = windowHeight;
		}
		else // Else do the height based on the width
		{
			newWidth = windowWidth;
			newHeight = windowWidth / 16 * 9;
		}
		
		// Resizes the canvas to match the new window size, keeping it centred.
		setBounds((windowWidth - newWidth) / 2, (windowHeight - newHeight) / 2, newWidth, newHeight);
	}
	
	public synchronized void start()
	{
		init();
		running = true;
		new Thread(this).start();
	}
	
	public synchronized void stop()
	{
		running = false;
	}
	
	@Override
	public void run()
	{
		long lastTime = System.nanoTime();
		double ticksPerSecond = 60D;
		double nsPerTick = 1000000000D / ticksPerSecond;
		
		int ticks = 0;
		int frames = 0;
		
		long lastTimer = System.currentTimeMillis();
		double delta = 0;
		
		while (running)
		{
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldRender = true; // true for unlimited frames, false for limited to tick rate
			
			while (delta >= 1)
			{
				ticks++;
				tick();
				delta--;
				shouldRender = true;
			}
			
			// Stops system from overloading the CPU. Gives other threads a chance to run.
			try
			{
				Thread.sleep((tps < 60 ? 1 : 3));
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			// Only render when something has been updated
			if (shouldRender)
			{
				frames++;
				render();
			}
			
			// If current time - the last time we updated is >= 1 second
			if (System.currentTimeMillis() - lastTimer >= 1000)
			{
				tps = ticks;
				fps = frames;
				lastTimer += 1000;
				frames = 0;
				ticks = 0;
			}
		}
	}
	
	public void tick()
	{
		tickCount++;
		
		if (currentScreen != null)
		{
			currentScreen.tick();
		}
		
		if (input.full_screen.isPressedFiltered())
		{
			// Toggles Fullscreen Mode
			setFullScreen(!fullscreen);
		}
		// TODO
		// if (input.hudDebug.isPressedFiltered())
		// {
		// hudDebug = !hudDebug;
		//
		// System.out.println("Debug Hud: " + hudDebug);
		// }
		//
		// if (input.hitboxDebug.isPressedFiltered())
		// {
		// hitboxDebug = !hitboxDebug;
		//
		// System.out.println("Show Hitboxes: " + hitboxDebug);
		// }
		//
		// if (input.lightingDebug.isPressedFiltered())
		// {
		// lightingDebug = !lightingDebug;
		//
		// System.out.println("Show Lighting: " + !lightingDebug);
		// }
		//
		// if (input.saveLevel.isPressedFiltered())
		// {
		// this.player.getLevel().toSaveFile();
		// System.out.println("Copied current level save data to clipboard");
		// }
		//
		//
		// if (input.levelBuilder.isPressedFiltered())
		// {
		// buildMode = !buildMode;
		//
		// System.out.println("Build Mode: " + buildMode);
		//
		// this.player.setCameraToFollow(!buildMode);
		// this.player.getLevel().toSaveFile();
		// }
		
		if (input.consoleDebug.isReleasedFiltered())
		{
			consoleDebug = !consoleDebug;
			
			ConsoleOutput.setGuiVisible(consoleDebug);
			
			System.out.println("Show Console: " + consoleDebug);
			
			// this.requestFocus();
		}
		
		// TODO blankLevel.tick();
	}
	
	public void render()
	{
		// Clears the canvas
		screen.getGraphics().setColor(Color.WHITE);
		screen.getGraphics().fillRect(0, 0, screen.getWidth(), screen.getHeight());
		
		/*
		 * TODO add back once working with server-side
		 * blankLevel.render(screen);
		 * if (this.tickCount < 240)
		 * {
		 * int xPos = (WIDTH / 2);
		 * int yPos = 120;
		 * Assets.getFont(Assets.FONT_WHITE).renderCentered(screen, WIDTH - xPos, HEIGHT - yPos, "CANCER:");
		 * Assets.getFont(Assets.FONT_WHITE).renderCentered(screen, WIDTH - xPos, HEIGHT + 15 - yPos, "The Adventures of");
		 * Assets.getFont(Assets.FONT_WHITE).renderCentered(screen, WIDTH - xPos, HEIGHT + 25 - yPos, "Afro Man");
		 * }
		 * if (hudDebug)
		 * {
		 * Assets.getFont(Assets.FONT_NORMAL).render(screen, 1, 0, "TPS: " + tps);
		 * Assets.getFont(Assets.FONT_NORMAL).render(screen, 1, 10, "FPS: " + fps);
		 * Assets.getFont(Assets.FONT_NORMAL).render(screen, 1, 20, "x: " + player.getX());
		 * Assets.getFont(Assets.FONT_NORMAL).render(screen, 1, 30, "y: " + player.getY());
		 * }
		 */
		
		if (currentScreen != null)
		{
			currentScreen.render(screen);
		}
		
		// Renders everything that was just drawn
		BufferStrategy bs = getBufferStrategy();
		if (bs == null)
		{
			createBufferStrategy(2);
			return;
		}
		Graphics2D g = ((Graphics2D) bs.getDrawGraphics());
		// g.rotate(Math.toRadians(1), WIDTH /2, HEIGHT/2);
		g.drawImage(screen.getImage(), 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}
	
	public void setFullScreen(boolean isFullScreen)
	{
		fullscreen = isFullScreen;
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		
		System.out.println("Setting Fullscreen: " + isFullScreen);
		
		/*
		 * This StackOverFlow thread was EXTREMELY helpful in getting this to work properly
		 * http://stackoverflow.com/questions/13064607/fullscreen-swing-components-fail-to-receive-keyboard-input-on-java-7-on-mac-os-x
		 */
		if (isFullScreen)
		{
			frame.dispose();// Restarts the JFrame
			frame.setResizable(false);// Disables resizing else causes bugs
			frame.setUndecorated(true);
			frame.setVisible(true);
			frame.revalidate();
			this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
			try
			{
				gd.setFullScreenWindow(frame);// Makes it full screen
				
				// if (System.getProperty("os.name").indexOf("Mac OS X") >= 0)
				// {
				// this.setVisible(false);
				// this.setVisible(true);
				// }
				
				this.repaint();
				this.revalidate();
			}
			catch (Exception e)
			{
				setFullScreen(false);
				System.err.println("Fullscreen Mode not supported.");
				e.printStackTrace();
			}
		}
		else
		{
			frame.dispose();// Restarts the JFrame
			frame.setVisible(false);
			frame.setResizable(true);
			frame.setUndecorated(false);
			frame.setVisible(true);// Shows restarted JFrame
			frame.pack();
			frame.setExtendedState(frame.getExtendedState() | JFrame.NORMAL);// Returns to normal state
		}
		
		this.requestFocus();
	}
	
	public String getPassword()
	{
		// TODO implement password setter
		return "hooplah";
	}
	
	public boolean isFullScreen()
	{
		return fullscreen;
	}
	
	public boolean isHudDebugging()
	{
		return hudDebug;
	}
	
	public boolean isHitboxDebugging()
	{
		return hitboxDebug;
	}
	
	public boolean isLightingDebugging()
	{
		return lightingDebug;
	}
	
	public boolean isBuildMode()
	{
		return buildMode;
	}
	
	public boolean isHostingServer()
	{
		return isHosting;
	}
	
	public void setCurrentScreen(GuiScreen screen)
	{
		this.currentScreen = screen;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String newName)
	{
		this.username = newName;
	}
	
	public static void main(String[] args)
	{
		game = new Game();
		game.start();
	}
}
