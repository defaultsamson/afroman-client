package ca.afroman.client;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JFrame;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.AudioClip;
import ca.afroman.assets.Texture;
import ca.afroman.entity.ClientPlayerEntity;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMapState;
import ca.afroman.gui.GuiClickNotification;
import ca.afroman.gui.GuiConnectToServer;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.gui.GuiScreen;
import ca.afroman.input.InputHandler;
import ca.afroman.level.ClientLevel;
import ca.afroman.level.LevelType;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.packet.PacketRequestConnection;
import ca.afroman.server.ServerGame;
import ca.afroman.server.ServerSocketManager;
import ca.afroman.thread.DynamicThread;
import ca.afroman.thread.DynamicTickRenderThread;

public class ClientGame extends DynamicTickRenderThread
{
	public static final int WIDTH = 240;
	public static final int HEIGHT = WIDTH / 16 * 9;
	public static final int SCALE = 3;
	public static final String NAME = "Cancer: The Adventures of Afro Man";
	public static final int VERSION = 27;
	public static final BufferedImage ICON = Texture.fromResource(AssetType.INVALID, "icon/32x.png").getImage();
	
	private static ClientGame game;
	
	public static ClientGame instance()
	{
		return game;
	}
	
	private static ThreadGroup newDefaultThreadGroupInstance()
	{
		return new ThreadGroup("Client");
	}
	
	private static long startLoadTime;
	
	private JFrame frame;
	private Canvas canvas;
	private Texture screen;
	
	private boolean fullscreen = false;
	private boolean hudDebug = false; // Shows debug information on the hud
	private boolean hitboxDebug = false; // Shows all hitboxes
	private LightMapState lightingDebug = LightMapState.ON; // Turns off the lighting engine
	private boolean buildMode = false; // Turns off the lighting engine
	private boolean consoleDebug = false; // Shows a console window
	
	public boolean updatePlayerList = false; // Tells if the player list has been updated within the last tick
	
	private InputHandler input;
	
	private List<ClientLevel> levels;
	private ClientLevel currentLevel = null;
	private List<ClientPlayerEntity> players;
	private HashMap<Role, FlickeringLight> lights;
	
	private String username = "";
	private String password = "";
	private String typedIP = "";
	
	private ClientSocketManager socketManager;
	
	private GuiScreen currentScreen = null;
	
	private AudioClip music;
	
	public ClientGame()
	{
		super(newDefaultThreadGroupInstance(), "Game", 60);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		canvas = new Canvas();
		frame = new JFrame(NAME);
		
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
		
		canvas.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		frame.setIconImage(ICON);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		
		frame.getContentPane().setBackground(Color.black);
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		
		canvas.repaint();
		
		// Sets the minimum size to that of the JFrame to that of the JFrame while it has the smallest canvas drawn on it
		frame.setMinimumSize(frame.getSize());
		
		// The width and height added by the JFrame that is not included in the Canvas
		int eccessWidth = frame.getWidth() - WIDTH;
		int eccessHeight = frame.getHeight() - HEIGHT;
		
		canvas.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		frame.setSize(new Dimension(eccessWidth + (WIDTH * SCALE), eccessHeight + (HEIGHT * SCALE)));
		frame.setLocationRelativeTo(null);
		
		// Loading screen
		final Texture loading = Texture.fromResource(AssetType.INVALID, "loading.png");
		DynamicThread renderLoading = new DynamicThread(this.getThreadGroup(), "Loading-Display")
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
					logger().log(ALogType.CRITICAL, "Thread failed to sleep.", e);
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
		renderLoading.startThis();
		
		// DO THE LOADING
		
		// Allows key listens for TAB and such
		canvas.setFocusTraversalKeysEnabled(false);
		
		// Initialises the console output.
		ConsoleOutput.instance();
		ALogger.initStreams(); // Initialises the console and log output etc
		
		Assets.load();
		
		screen = new Texture(AssetType.INVALID, new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB));
		input = new InputHandler(this);
		levels = new ArrayList<ClientLevel>();
		
		players = new ArrayList<ClientPlayerEntity>();
		getPlayers().add(new ClientPlayerEntity(Role.PLAYER1, 0, 0));
		getPlayers().add(new ClientPlayerEntity(Role.PLAYER2, 0, 0));
		
		lights = new HashMap<Role, FlickeringLight>();
		lights.put(Role.PLAYER1, new FlickeringLight(-1, null, 0, 0, 50, 47, 4));
		lights.put(Role.PLAYER2, new FlickeringLight(-1, null, 0, 0, 50, 47, 4));
		
		setCurrentScreen(new GuiMainMenu());
		
		// WHEN FINISHED LOADING
		
		// End the loading screen
		renderLoading.stopThis();
		frame.setResizable(true);
		canvas.repaint();
		
		double loadTime = (System.currentTimeMillis() - startLoadTime) / 1000.0D;
		
		logger().log(ALogType.DEBUG, "Game Loaded. Took " + loadTime + " seconds.");
		
		music = Assets.getAudioClip(AssetType.AUDIO_MENU_MUSIC);
		music.startLoop();
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
		
		for (Entry<Role, FlickeringLight> light : lights.entrySet())
		{
			ClientPlayerEntity player = getPlayer(light.getKey());
			
			light.getValue().addToLevel(player.getLevel());
			light.getValue().setX(player.getX() + (player.getWidth() / 2));
			light.getValue().setY(player.getY() + (player.getHeight() / 2));
		}
		
		if (input.consoleDebug.isReleasedFiltered())
		{
			consoleDebug = !consoleDebug;
			
			ConsoleOutput.instance().setGuiVisible(consoleDebug);
			
			logger().log(ALogType.DEBUG, "Show Console: " + consoleDebug);
		}
		
		if (input.full_screen.isPressedFiltered())
		{
			setFullScreen(!fullscreen);
		}
		
		if (input.hudDebug.isPressedFiltered())
		{
			hudDebug = !hudDebug;
			
			logger().log(ALogType.DEBUG, "Debug Hud: " + hudDebug);
		}
		
		if (input.hitboxDebug.isPressedFiltered())
		{
			hitboxDebug = !hitboxDebug;
			
			logger().log(ALogType.DEBUG, "Show Hitboxes: " + hitboxDebug);
		}
		if (input.lightingDebug.isPressedFiltered())
		{
			int currentOrdinal = lightingDebug.ordinal();
			
			currentOrdinal++;
			
			// Roll over
			if (currentOrdinal >= LightMapState.values().length)
			{
				currentOrdinal = 0;
			}
			
			lightingDebug = LightMapState.fromOrdinal(currentOrdinal);
			
			logger().log(ALogType.DEBUG, "Lighting: " + lightingDebug.toString());
		}
		if (input.saveLevel.isPressedFiltered())
		{
			if (getCurrentLevel() != null) getCurrentLevel().toSaveFile();
			logger().log(ALogType.DEBUG, "Copied current level save data to clipboard");
		}
		
		if (input.levelBuilder.isPressedFiltered())
		{
			buildMode = !buildMode;
			
			logger().log(ALogType.DEBUG, "Build Mode: " + buildMode);
			
			this.getPlayer(sockets().getConnectedPlayer().getRole()).setCameraToFollow(!buildMode);
		}
		
		if (currentScreen != null)
		{
			currentScreen.tick();
		}
		
		// TODO Have it not run the main game code. Leave that to the server
		if (getCurrentLevel() != null)
		{
			getCurrentLevel().tick();
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
		screen.getGraphics().fillRect(0, 0, (int) screen.getWidth(), (int) screen.getHeight());
		
		if (getCurrentLevel() != null)
		{
			getCurrentLevel().render(screen);
		}
		
		if (getCurrentScreen() != null)
		{
			getCurrentScreen().render(screen);
		}
		
		if (hudDebug)
		{
			Assets.getFont(AssetType.FONT_BLACK).render(screen, 1, 0, "TPS: " + tps);
			Assets.getFont(AssetType.FONT_BLACK).render(screen, 1, 10, "FPS: " + fps);
			Assets.getFont(AssetType.FONT_BLACK).render(screen, 1, HEIGHT - 9, "V");
			Assets.getFont(AssetType.FONT_BLACK).render(screen, 9, HEIGHT - 9, "" + VERSION);
			// TODO Assets.getFont(Assets.FONT_BLACK).render(screen, 1, 20, "x: " + player.getX() );
			// TODO Assets.getFont(Assets.FONT_BLACK).render(screen, 1, 30, "y: " + player.getY());
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
		
		logger().log(ALogType.DEBUG, "Setting Fullscreen: " + isFullScreen);
		
		frame.setVisible(false);
		// frame.getContentPane().remove(canvas);
		JFrame old = frame;
		
		frame = new JFrame(NAME);
		
		frame.setIconImage(ICON);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setUndecorated(isFullScreen);
		
		frame.getContentPane().setBackground(Color.black);
		frame.getContentPane().add(canvas, BorderLayout.CENTER); // TODO This crashes the game if the window isn't on the primary screen
		frame.pack();
		frame.setResizable(!isFullScreen);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		/*
		 * This StackOverFlow thread was EXTREMELY helpful in getting this to work properly
		 * http://stackoverflow.com/questions/13064607/fullscreen-swing-components-fail-to-receive-keyboard-input-on-java-7-on-mac-os-x
		 */
		if (isFullScreen)
		{
			try
			{
				GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				
				gd.setFullScreenWindow(frame);// Makes it full screen
				
				// TODO test on Ben's Mac
				// if (System.getProperty("os.name").indexOf("Mac OS X") >= 0)
				// {
				// this.setVisible(false);
				// this.setVisible(true);
				// }
			}
			catch (Exception e)
			{
				setFullScreen(false);
				logger().log(ALogType.CRITICAL, "Fullscreen Mode not supported.", e);
			}
		}
		
		canvas.requestFocus();
		
		old.removeAll();
		old.getContentPane().removeAll();
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
	
	public LightMapState getLightingState()
	{
		return lightingDebug;
	}
	
	public boolean isLightingOn()
	{
		return lightingDebug != LightMapState.OFF;
	}
	
	public boolean isBuildMode()
	{
		return buildMode;
	}
	
	public boolean isHostingServer()
	{
		return ServerGame.instance() != null;
	}
	
	public void setCurrentScreen(GuiScreen screen)
	{
		logger().log(ALogType.DEBUG, "Setting screen: " + screen.getClass().getName());
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
		return updatePlayerList && hasStartedUpdateList;
	}
	
	public void exitFromGame(ExitGameReason reason)
	{
		music.startLoop();
		getLevels().clear();
		setCurrentLevel(null);
		setCurrentScreen(new GuiMainMenu());
		
		switch (reason)
		{
			default:
				break;
			case SERVER_CLOSED:
				new GuiClickNotification(ClientGame.instance().getCurrentScreen(), "SERVER", "CLOSED");
				break;
		}
		
		socketManager.stopThis();
		
		if (this.isHostingServer())
		{
			ServerGame.instance().stopThis();
		}
	}
	
	public void joinServer()
	{
		music.stop();
		setCurrentScreen(new GuiConnectToServer(getCurrentScreen()));
		render();
		
		socketManager = new ClientSocketManager();
		socketManager.setServerIP(getServerIP(), ServerSocketManager.PORT); // TODO allow selectable port
		socketManager.startThis();
		socketManager.sender().sendPacket(new PacketRequestConnection(getUsername(), getPassword()));
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
		for (ClientLevel level : getLevels())
		{
			if (level.getType() == type) return level;
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		startLoadTime = System.currentTimeMillis();
		
		game = new ClientGame();
		game.startThis();
	}
	
	public Canvas getCanvas()
	{
		return canvas;
	}
	
	public JFrame getFrame()
	{
		return frame;
	}
	
	public ClientSocketManager sockets()
	{
		return socketManager;
	}
	
	public InputHandler input()
	{
		return input;
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
		if (this.isHostingServer()) ServerGame.instance().stopThis();
		if (sockets() != null) sockets().stopThis();
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
	public ClientPlayerEntity getPlayer(Role role)
	{
		for (ClientPlayerEntity entity : getPlayers())
		{
			if (entity.getRole() == role) return entity;
		}
		return null;
	}
	
	public List<ClientPlayerEntity> getPlayers()
	{
		return players;
	}
	
	public List<ClientLevel> getLevels()
	{
		return levels;
	}
	
	public FlickeringLight getLight(Role role)
	{
		return lights.get(role);
	}
}
