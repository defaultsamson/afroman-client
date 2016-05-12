package ca.afroman;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.Texture;
import ca.afroman.console.ConsoleOutput;
import ca.afroman.entity.ClientPlayerEntity;
import ca.afroman.gui.GuiConnectToServer;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.gui.GuiScreen;
import ca.afroman.input.InputHandler;
import ca.afroman.level.ClientLevel;
import ca.afroman.level.LevelType;
import ca.afroman.packet.PacketRequestConnection;
import ca.afroman.player.Role;
import ca.afroman.server.ServerGame;
import ca.afroman.thread.DynamicThread;
import ca.afroman.thread.DynamicTickRenderThread;

public class ClientGame extends DynamicTickRenderThread // implements Runnable
{
	private static ClientGame game;
	
	public static ClientGame instance()
	{
		return game;
	}
	
	public static final int WIDTH = 240;
	public static final int HEIGHT = WIDTH / 16 * 9;
	public static final int SCALE = 3;
	public static final String NAME = "Cancer: The Adventures of Afro Man";
	
	private JFrame frame;
	private Canvas canvas;
	private Texture screen;
	
	private boolean fullscreen = false;
	private boolean hudDebug = false; // Shows debug information on the hud
	private boolean hitboxDebug = false; // Shows all hitboxes
	private boolean lightingDebug = false; // Turns off the lighting engine
	private boolean buildMode = false; // Turns off the lighting engine
	private boolean consoleDebug = false; // Shows a console window
	
	public boolean isHosting = false;
	public boolean updatePlayerList = false; // Tells if the player list has been updated within the last tick
	
	public InputHandler input;
	
	public List<ClientLevel> levels;
	private ClientLevel currentLevel = null;
	private List<ClientPlayerEntity> players;
	
	private String username = "";
	private String password = "";
	private String typedIP = "";
	
	private ClientSocket socketClient = null;
	
	private GuiScreen currentScreen = null;
	
	public ClientGame()
	{
		super(60);
		canvas = new Canvas();
		frame = new JFrame(NAME);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		// Makes it so that when the window is resized, this ClientGame will resize the canvas accordingly
		canvas.addComponentListener(new ComponentListener()
		{
			private boolean doIt = true;
			
			@Override
			public void componentResized(ComponentEvent e)
			{
				if (doIt) // Stops it from detecting the resizeGame method from resizing its bounds.
				{
					doIt = false;
					ClientGame.instance().resizeGame(canvas.getWidth(), canvas.getHeight());
				}
				else
				{
					doIt = true;
				}
			}
			
			@Override
			public void componentMoved(ComponentEvent e)
			{
				
			}
			
			@Override
			public void componentShown(ComponentEvent e)
			{
				
			}
			
			@Override
			public void componentHidden(ComponentEvent e)
			{
				
			}
		});
		
		canvas.setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		canvas.setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		canvas.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.getContentPane().setBackground(Color.black);
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		// Loading screen
		long startTime = System.currentTimeMillis();
		canvas.repaint();
		final Texture loading = Texture.fromResource("/loading.png");
		DynamicThread renderLoading = new DynamicThread()
		{
			@Override
			public void onRun()
			{
				canvas.getGraphics().drawImage(loading.getImage(), 0, 0, canvas.getWidth(), canvas.getHeight(), null);
				
				try
				{
					Thread.sleep(200);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			@Override
			public void onStart()
			{
				
			}
			
			@Override
			public void onPause()
			{
				
			}
			
			@Override
			public void onUnpause()
			{
				
			}
			
			@Override
			public void onStop()
			{
				loading.getImage().flush();
			}
		};
		renderLoading.start();
		
		// DO THE LOADING
		
		// Allows key listens for TAB and such
		canvas.setFocusTraversalKeysEnabled(false);
		
		ConsoleOutput.createGui();
		ConsoleOutput.showGui();
		ConsoleOutput.hideGui();
		
		screen = new Texture(new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB));
		input = new InputHandler(this);
		levels = new ArrayList<ClientLevel>();
		players = new ArrayList<ClientPlayerEntity>();
		players.add(new ClientPlayerEntity(Role.PLAYER1, 0, 0));
		players.add(new ClientPlayerEntity(Role.PLAYER2, 0, 0));
		
		socketClient = new ClientSocket();
		
		setCurrentScreen(new GuiMainMenu());
		
		// WHEN FINISHED LOADING
		
		long loadTime = System.currentTimeMillis() - startTime;
		
		int forcedDisplayTime = 500;
		
		// Makes you see the loading screen for at least a half second
		if (loadTime < forcedDisplayTime)
		{
			try
			{
				Thread.sleep(forcedDisplayTime - loadTime);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		// End the loading screen
		renderLoading.stopThread();
		frame.setResizable(true);
		canvas.repaint();
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
		canvas.setBounds((windowWidth - newWidth) / 2, (windowHeight - newHeight) / 2, newWidth, newHeight);
	}
	
	@Override
	public void tick()
	{
		tickCount++;
		
		if (updatePlayerList) hasStartedUpdateList = true;
		
		if (input.consoleDebug.isReleasedFiltered())
		{
			consoleDebug = !consoleDebug;
			
			ConsoleOutput.setGuiVisible(consoleDebug);
			
			System.out.println("Show Console: " + consoleDebug);
		}
		
		if (input.full_screen.isPressedFiltered())
		{
			// Toggles Fullscreen Mode
			setFullScreen(!fullscreen);
		}
		
		if (input.hudDebug.isPressedFiltered())
		{
			hudDebug = !hudDebug;
			
			System.out.println("Debug Hud: " + hudDebug);
		}
		
		if (input.hitboxDebug.isPressedFiltered())
		{
			hitboxDebug = !hitboxDebug;
			
			System.out.println("Show Hitboxes: " + hitboxDebug);
		}
		
		// TODO
		//
		// if (input.lightingDebug.isPressedFiltered())
		// {
		// lightingDebug = !lightingDebug;
		//
		// System.out.println("Show Lighting: " + !lightingDebug);
		// }
		
		if (input.saveLevel.isPressedFiltered())
		{
			if (currentLevel != null) currentLevel.toSaveFile();
			System.out.println("Copied current level save data to clipboard");
		}
		
		if (input.levelBuilder.isPressedFiltered())
		{
			buildMode = !buildMode;
			
			System.out.println("Build Mode: " + buildMode);
			
			// TODO player shiz
			// this.player.setCameraToFollow(!buildMode);
			// this.player.getLevel().toSaveFile();
		}
		
		// TODO Have it not run the main game code. Leave that to the server
		if (currentLevel != null)
		{
			currentLevel.tick();
		}
		
		if (currentScreen != null)
		{
			currentScreen.tick();
		}
		
		if (hasStartedUpdateList)
		{
			hasStartedUpdateList = false;
			updatePlayerList = false;
		}
	}
	
	@Override
	public void render()
	{
		// Clears the canvas
		screen.getGraphics().setColor(Color.WHITE);
		screen.getGraphics().fillRect(0, 0, screen.getWidth(), screen.getHeight());
		
		if (currentLevel != null)
		{
			currentLevel.render(screen);
		}
		
		if (currentScreen != null)
		{
			currentScreen.render(screen);
		}
		
		if (hudDebug)
		{
			Assets.getFont(AssetType.FONT_BLACK).render(screen, 1, 0, "TPS: " + tps);
			Assets.getFont(AssetType.FONT_BLACK).render(screen, 1, 10, "FPS: " + fps);
			// Assets.getFont(Assets.FONT_BLACK).render(screen, 1, 20, "x: " + player.getX() );
			// Assets.getFont(Assets.FONT_BLACK).render(screen, 1, 30, "y: " + player.getY());
		}
		
		// Renders everything that was just drawn
		BufferStrategy bs = canvas.getBufferStrategy();
		if (bs == null)
		{
			canvas.createBufferStrategy(2);
			return;
		}
		Graphics2D g = ((Graphics2D) bs.getDrawGraphics());
		g.drawImage(screen.getImage(), 0, 0, canvas.getWidth(), canvas.getHeight(), null);
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
			canvas.setSize(Toolkit.getDefaultToolkit().getScreenSize());
			try
			{
				gd.setFullScreenWindow(frame);// Makes it full screen
				
				// if (System.getProperty("os.name").indexOf("Mac OS X") >= 0)
				// {
				// this.setVisible(false);
				// this.setVisible(true);
				// }
				
				canvas.repaint();
				canvas.revalidate();
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
		
		canvas.requestFocus();
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
	
	public GuiScreen getCurrentScreen()
	{
		return currentScreen;
	}
	
	public void setUsername(String newUsername)
	{
		this.username = newUsername;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setPassword(String newPassword)
	{
		this.password = newPassword;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setServerIP(String newIP)
	{
		this.typedIP = newIP;
	}
	
	public String getServerIP()
	{
		return typedIP;
	}
	
	private boolean hasStartedUpdateList = false;
	
	public boolean hasServerListBeenUpdated()
	{
		// TODO make it only run one tick, but throughout the entirety of the tick/
		// Currently it just runs throughout a random portion of the tick, so I make it
		// Run through twice just in case. THIS IS AN ISSUE
		return updatePlayerList && hasStartedUpdateList;
	}
	
	public synchronized void exitFromGame()
	{
		this.levels.clear();
		this.isHosting = false;
		setCurrentLevel(null);
		setCurrentScreen(new GuiMainMenu());
		socketClient.getPlayers().clear();
		socketClient.pauseThread();
	}
	
	public void joinServer()
	{
		socketClient.start();
		setCurrentScreen(new GuiConnectToServer(getCurrentScreen()));
		render();
		
		socketClient.setServerIP(getServerIP());
		socketClient.sendPacket(new PacketRequestConnection(getUsername(), getPassword()));
	}
	
	public ClientLevel getCurrentLevel()
	{
		return currentLevel;
	}
	
	public void setCurrentLevel(ClientLevel newLevel)
	{
		currentLevel = newLevel;
	}
	
	public ClientLevel getLevelByType(LevelType type)
	{
		for (ClientLevel level : levels)
		{
			if (level.getType() == type) return level;
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		game = new ClientGame();
		game.start();
	}
	
	public Canvas getCanvas()
	{
		return canvas;
	}
	
	public JFrame getFrame()
	{
		return frame;
	}
	
	public ClientSocket socket()
	{
		return socketClient;
	}
	
	@Override
	public void onPause()
	{
		
	}
	
	@Override
	public void onUnpause()
	{
		
	}
	
	@Override
	public void onStop()
	{
		if (ServerGame.instance() != null) ServerGame.instance().stopThread();
		socketClient.stopThread();
	}
	
	public void updatePlayerList()
	{
		this.updatePlayerList = true;
	}
	
	/**
	 * Gets the player with the given role.
	 * 
	 * @param role whether it's player 1 or 2
	 * @return the player.
	 */
	public synchronized ClientPlayerEntity getPlayer(Role role)
	{
		for (ClientPlayerEntity entity : players)
		{
			if (entity.getRole() == role) return entity;
		}
		return null;
	}
	
	public Role getRole()
	{
		return this.socketClient.thisPlayer().getRole();
	}
}
