package ca.afroman.client;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JFrame;

import ca.afroman.assets.AssetType;
import ca.afroman.assets.Assets;
import ca.afroman.assets.AudioClip;
import ca.afroman.assets.Font;
import ca.afroman.assets.Texture;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.entity.api.Direction;
import ca.afroman.events.Event;
import ca.afroman.game.Game;
import ca.afroman.game.Role;
import ca.afroman.game.SocketManager;
import ca.afroman.gui.GuiClickNotification;
import ca.afroman.gui.GuiCommand;
import ca.afroman.gui.GuiConnectToServer;
import ca.afroman.gui.GuiInGameMenu;
import ca.afroman.gui.GuiJoinServer;
import ca.afroman.gui.GuiLobby;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.gui.GuiOptionsMenu;
import ca.afroman.gui.GuiScreen;
import ca.afroman.gui.GuiSendingLevels;
import ca.afroman.input.InputHandler;
import ca.afroman.level.api.Level;
import ca.afroman.level.api.LevelType;
import ca.afroman.light.FlickeringLight;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.network.IncomingPacketWrapper;
import ca.afroman.option.Options;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketLogin;
import ca.afroman.packet.PacketPlayerDisconnect;
import ca.afroman.resource.IDCounter;
import ca.afroman.resource.ModulusCounter;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.resource.Vector2DInt;
import ca.afroman.server.DenyJoinReason;
import ca.afroman.server.ServerGame;
import ca.afroman.thread.DynamicThread;
import ca.afroman.util.ByteUtil;
import ca.afroman.util.IPUtil;
import ca.afroman.util.UpdateUtil;
import ca.afroman.util.VersionUtil;
import samson.stream.Console;

public class ClientGame extends Game
{
	public static final int WIDTH = 240;
	public static final int HEIGHT = WIDTH / 16 * 9;
	public static final int DEFAULT_SCALE = 3;
	public static final String NAME = "The Adventures of Afro Man";
	public static final BufferedImage ICON = Texture.fromResource(AssetType.INVALID, "icon/32x.png").getImage();
	
	public static final int RECEIVE_PACKET_BUFFER_LIMIT = 128;
	
	private static ClientGame game;
	
	private static long startLoadTime;
	
	public static ClientGame instance()
	{
		return game;
	}
	
	public static void main(String[] args)
	{
		startLoadTime = System.currentTimeMillis();
		
		boolean serverOnly = false;
		for (String s : args)
		{
			if (s.equals("-server"))
			{
				serverOnly = true;
				break;
			}
		}
		
		if (serverOnly)
		{
			new ServerGame(true, Options.instance().serverIP, Options.instance().serverPassword, Options.instance().serverPort);
		}
		else
		{
			game = new ClientGame();
			game.startThis();
		}
	}
	
	private static ThreadGroup newDefaultThreadGroupInstance()
	{
		return new ThreadGroup("Client");
	}
	
	private JFrame frame;
	private Canvas canvas;
	
	private Texture screen;
	private boolean hudDebug = false; // Shows debug information on the hud
	private boolean hitboxDebug = false; // Shows all hitboxes
	private boolean buildMode = false;
	private boolean consoleDebug = false; // Shows a console window
	public boolean updatePlayerList = false; // Tells if the player list has been updated within the last tick
	
	private InputHandler input;
	private Level currentLevel = null;
	private HashMap<Role, FlickeringLight> lights;
	
	private Role role;
	private Role spectatingRole = Role.PLAYER1;
	private short id = -1;
	
	/** Keeps track of the amount of ticks passed to time memory usage updates. */
	private ModulusCounter updateMem;
	private long totalMemory = 0;
	private long usedMemory = 0;
	
	private Font debugFont;
	private Cursor blankCursor;
	private byte hideCursor = 0;
	
	/** Whether or not to exit from the game and go to the main menu. */
	private boolean exitGame = false;
	private boolean hasStartedUpdateList = false;
	
	private GuiScreen currentScreen = null;
	private AudioClip music;
	
	public ClientGame()
	{
		super(newDefaultThreadGroupInstance(), "Game", false, 60);
	}
	
	public void exitFromGame(ExitGameReason reason)
	{
		// TODO let the server know that the client has disconnected
		if (sockets() != null && reason == ExitGameReason.DISCONNECT)
		{
			sockets().sender().sendPacket(new PacketPlayerDisconnect());
		}
		
		setIsInGame(false);
		
		switch (reason)
		{
			default:
				break;
			case SERVER_CLOSED:
				new GuiClickNotification(getCurrentScreen(), -1, "Server", "closed");
				break;
			case KICKED:
				new GuiClickNotification(getCurrentScreen(), -1, "Kicked", "from server");
				break;
			case BANNED:
				new GuiClickNotification(getCurrentScreen(), -1, "Banned", "from server");
				break;
		}
	}
	
	private boolean waitingForOthersToLoad = false;
	
	@Override
	public void setIsInGame(boolean isInGame)
	{
		super.setIsInGame(isInGame);
		
		if (isInGame)
		{
			getLevels().clear();
			
			if (!(getCurrentScreen() instanceof GuiSendingLevels))
			{
				setCurrentScreen(new GuiSendingLevels(null));
			}
			
			loadLevels();
			
			waitingForOthersToLoad = true;
			
			if (music.isRunning())
			{
				music.stop();
			}
		}
		else
		{
			waitingForOthersToLoad = false;
			
			stopSocket();
			
			id = -1;
			
			getLevels().clear();
			setCurrentLevel(null);
			
			IDCounter.resetAll();
			
			// resets the player entities
			for (PlayerEntity e : getPlayers())
			{
				e.reset();
			}
			
			setCurrentScreen(new GuiMainMenu());
			
			if (!music.isRunning())
			{
				music.startLoop();
			}
		}
	}
	
	public Canvas getCanvas()
	{
		return canvas;
	}
	
	public Level getCurrentLevel()
	{
		return currentLevel;
	}
	
	public GuiScreen getCurrentScreen()
	{
		return currentScreen;
	}
	
	public JFrame getFrame()
	{
		return frame;
	}
	
	public short getID()
	{
		return id;
	}
	
	public Role getRole()
	{
		return role;
	}
	
	public Role getSpectatingRole()
	{
		return spectatingRole;
	}
	
	public PlayerEntity getThisPlayer()
	{
		if (role != Role.SPECTATOR) return getPlayer(role);
		else
			return null;
	}
	
	public boolean hasServerListBeenUpdated()
	{
		return updatePlayerList && hasStartedUpdateList;
	}
	
	public InputHandler input()
	{
		return input;
	}
	
	public boolean isBuildMode()
	{
		return buildMode;
	}
	
	public boolean isHitboxDebugging()
	{
		return hitboxDebug;
	}
	
	public boolean isHostingServer()
	{
		return getID() == 0;
	}
	
	public boolean isHudDebugging()
	{
		return hudDebug;
	}
	
	public void joinServer(String ip, String port, String username, String password)
	{
		setCurrentScreen(new GuiConnectToServer(getCurrentScreen()));
		render();
		
		int vPort = SocketManager.validatedPort(port);
		
		// Sets the port to whatever is now set
		
		boolean successful = startSocket(ip, vPort);
		
		if (successful)
		{
			sockets().sender().sendPacket(new PacketLogin(username, password));
		}
	}
	
	@Override
	public void parsePacket(IncomingPacketWrapper inPack)
	{
		try
		{
			BytePacket packet = inPack.getPacket();
			
			if (sockets() != null)
			{
				// If is the server sending the packet
				if (IPUtil.equals(inPack.getIPAddress(), inPack.getPort(), sockets().getServerConnection()))
				{
					switch (packet.getType())
					{
						default:
						case INVALID:
							logger().log(ALogType.WARNING, "[CLIENT] INVALID PACKET");
							break;
						case DENY_JOIN:
						{
							setCurrentScreen(new GuiJoinServer(new GuiMainMenu()));
							
							DenyJoinReason reason = DenyJoinReason.fromOrdinal(ByteUtil.shortFromBytes(new byte[] { packet.getContent()[0], packet.getContent()[1] }));
							
							switch (reason)
							{
								default:
									new GuiClickNotification(getCurrentScreen(), -1, "CAN'T CONNECT", "TO SERVER");
									break;
								case DUPLICATE_USERNAME:
									new GuiClickNotification(getCurrentScreen(), -1, "DUPLICATE", "USERNAME");
									break;
								case FULL_SERVER:
									new GuiClickNotification(getCurrentScreen(), -1, "SERVER", "FULL");
									break;
								case NEED_PASSWORD:
									new GuiClickNotification(getCurrentScreen(), -1, "INVALID", "PASSWORD");
									break;
								case OLD_CLIENT:
									new GuiClickNotification(getCurrentScreen(), -1, "CLIENT", "OUTDATED");
									break;
								case OLD_SERVER:
									new GuiClickNotification(getCurrentScreen(), -1, "SERVER", "OUTDATED");
									break;
							}
						}
							break;
						case ASSIGN_CLIENTID:
							id = ByteUtil.shortFromBytes(new byte[] { packet.getContent()[0], packet.getContent()[1] });
							
							sockets().initServerTCPConnection();
							break;
						case UPDATE_PLAYERLIST:
						{
							updatePlayerList();
							List<ConnectedPlayer> players = new ArrayList<ConnectedPlayer>();
							ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
							
							// If there's still remaining bytes and they aren't the signal
							while (buf.hasRemaining() && !ByteUtil.isSignal(buf.array(), buf.position(), Byte.MIN_VALUE, Byte.MAX_VALUE, Byte.MAX_VALUE, Byte.MIN_VALUE))
							{
								// !(packet.getContent()[buf.position()] == Byte.MIN_VALUE && packet.getContent()[buf.position() + 1] == Byte.MAX_VALUE && packet.getContent()[buf.position() + 2] == Byte.MAX_VALUE && packet.getContent()[buf.position() + 3] == Byte.MIN_VALUE)
								short id = buf.getShort();
								Role role = Role.fromOrdinal(buf.get());
								String name = new String(ByteUtil.extractBytes(buf, Byte.MIN_VALUE, Byte.MAX_VALUE));
								
								players.add(new ConnectedPlayer(id, role, name));
							}
							
							sockets().updateConnectedPlayers(players);
							
							if (getCurrentScreen() instanceof GuiConnectToServer)
							{
								setCurrentScreen(new GuiLobby(null));
							}
						}
							break;
						case STOP_SERVER:
						{
							exitGame = true;
						}
							break;
						case SEND_LEVELS:
						{
							boolean sendingLevels = (packet.getContent()[0] == 1);
							
							if (sendingLevels)
							{
								setIsInGame(true);
							}
							else
							{
								waitingForOthersToLoad = false;
								
								// Stop displaying the loading level screen
								if (getCurrentScreen() instanceof GuiSendingLevels)
								{
									setCurrentScreen(null);
								}
								
								if (getRole() == Role.SPECTATOR)
								{
									PlayerEntity pe = getPlayer(spectatingRole);
									
									if (pe != null)
									{
										setCurrentLevel(pe.getLevel());
									}
									else
									{
										logger().log(ALogType.WARNING, "[CLIENT] Player with type " + spectatingRole + " is null");
									}
								}
							}
						}
							break;
						case ADD_LEVEL_PLAYER:
						{
							Role role = Role.fromOrdinal(packet.getContent()[0]);
							
							PlayerEntity player = getPlayer(role);
							
							if (player != null)
							{
								LevelType levelType = LevelType.fromOrdinal(ByteUtil.shortFromBytes(Arrays.copyOfRange(packet.getContent(), 1, 3)));
								Level level = getLevel(levelType);
								
								player.addToLevel(level);
								
								// If it's adding the player that this player is, center the camera on them
								if (player.getRole() == getRole())
								{
									setCurrentLevel(level);
									player.setCameraToFollow(true);
								}
							}
						}
							break;
						case SET_PLAYER_LOCATION:
						{
							ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
							Role role = Role.fromOrdinal(buf.get());
							
							PlayerEntity player = getPlayer(role);
							
							if (player != null)
							{
								player.setDirection(Direction.fromOrdinal(buf.get()));
								player.setLastDirection(Direction.fromOrdinal(buf.get()));
								player.setPosition(new Vector2DDouble(buf.getInt(), buf.getInt()));
							}
							else
							{
								logger().log(ALogType.WARNING, "No player with role " + role);
							}
						}
							break;
						case ACTIVATE_TRIGGER:
						{
							ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
							
							LevelType levelType = LevelType.fromOrdinal(buf.getShort());
							Level level = getLevel(levelType);
							
							if (level != null)
							{
								int id = buf.getInt();
								
								Event eHitbox = level.getEvent(id);
								
								if (eHitbox != null)
								{
									byte hnng = buf.get();
									Role role = Role.fromOrdinal(hnng);
									
									if (role != null)
									{
										PlayerEntity player = getPlayer(role);
										
										if (player != null)
										{
											eHitbox.trigger(player);
										}
										else
										{
											logger().log(ALogType.WARNING, "No player found with role " + role);
										}
									}
									else
									{
										logger().log(ALogType.WARNING, "No role found with ordinal " + hnng);
									}
								}
								else
								{
									logger().log(ALogType.WARNING, "No event with ID " + id);
								}
							}
							else
							{
								logger().log(ALogType.WARNING, "No level with type " + levelType);
							}
						}
							break;
					}
				}
				else
				{
					logger().log(ALogType.WARNING, "A server (" + IPUtil.asReadable(inPack.getIPAddress(), inPack.getPort()) + ") is tring to send a packet " + packet.getType() + " to this unlistening client");
				}
			}
			else
			{
				try
				{
					logger().log(ALogType.WARNING, "Client sockets are closed (" + IPUtil.asReadable(inPack.getIPAddress(), inPack.getPort()) + ") is tring to send a packet " + packet.getType() + " to this unlistening client");
				}
				catch (Exception e)
				{
					// Unable to print who was sending this packet
				}
			}
		}
		catch (Exception e)
		{
			// logger().log(ALogType.IMPORTANT, "Exception upon packet parsing", e);
		}
	}
	
	/**
	 * Quits the game.
	 */
	public void quit()
	{
		quit(false);
	}
	
	/**
	 * Quits the game.
	 * 
	 * @param update whether to apply updates or not
	 */
	public void quit(boolean update)
	{
		stopThis();
		if (update)
		{
			UpdateUtil.applyUpdate();
		}
		System.exit(0);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render()
	{
		if (input().isGameInFocus() || Options.instance().renderOffFocus)
		{
			// Clears the canvas
			screen.getGraphics().setColor(Color.WHITE);
			screen.getGraphics().fillRect(0, 0, screen.getWidth(), screen.getHeight());
			
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
				debugFont.render(screen, new Vector2DInt(1, 0), "MEM: " + ((double) Math.round(((double) usedMemory / (double) totalMemory) * 10) / 10) + "% (" + (usedMemory / 1024 / 1024) + "MB)");
				debugFont.render(screen, new Vector2DInt(1, 10), "TPS: " + tps);
				debugFont.render(screen, new Vector2DInt(1, 20), "FPS: " + fps);
				debugFont.render(screen, new Vector2DInt(1, HEIGHT - 9), "V");
				debugFont.render(screen, new Vector2DInt(9, HEIGHT - 9), "" + VersionUtil.VERSION_STRING);
				
				PlayerEntity player = getThisPlayer();
				
				if (player != null && player.getLevel() != null)
				{
					debugFont.render(screen, new Vector2DInt(1, 30), "x: " + player.getPosition().getX());
					debugFont.render(screen, new Vector2DInt(1, 40), "y: " + player.getPosition().getY());
				}
			}
			
			// Renders everything that was just drawn
			BufferStrategy bs = canvas.getBufferStrategy();
			if (bs == null)
			{
				canvas.createBufferStrategy(1);
				return;
			}
			
			Graphics2D g = ((Graphics2D) bs.getDrawGraphics());
			g.drawImage(screen.getImage(), 0, 0, canvas.getWidth(), canvas.getHeight(), null);
			g.dispose();
			bs.show();
		}
	}
	
	/**
	 * Operation to perform to resize the game, keeping the aspect ratio.
	 * 
	 * @param windowWidth the new desired width.
	 * @param windowHeight the new desired height.
	 */
	public void resizeGame(int windowWidth, int windowHeight, boolean wrapCanvas)
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
		
		int oldWidth = canvas.getWidth();
		int oldHeight = canvas.getHeight();
		
		// Resizes the canvas to match the new window size, keeping it centred.
		canvas.setBounds((windowWidth - newWidth) / 2, (windowHeight - newHeight) / 2, newWidth, newHeight);
		
		if (wrapCanvas)
		{
			int deltaWidth = oldWidth - canvas.getWidth();
			int deltaHeight = oldHeight - canvas.getHeight();
			
			canvas.setPreferredSize(new Dimension(newWidth, newHeight));
			frame.setSize(frame.getWidth() - deltaWidth, frame.getHeight() - deltaHeight);
			frame.pack();
			
			frame.setLocationRelativeTo(null);
		}
	}
	
	public void setCurrentLevel(Level newLevel)
	{
		currentLevel = newLevel;
		
		updateCursorHiding();
	}
	
	public void setCurrentScreen(GuiScreen screen)
	{
		currentScreen = screen;
		
		updateCursorHiding();
	}
	
	public void setFullScreen(boolean isFullScreen)
	{
		if (Options.instance().fullscreen != isFullScreen)
		{
			Options.instance().fullscreen = isFullScreen;
			
			frame.setVisible(false);
			// frame.getContentPane().remove(canvas);
			JFrame old = frame;
			
			frame = new JFrame(NAME);
			
			frame.setIconImage(ICON);
			// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			frame.setUndecorated(isFullScreen);
			
			frame.getContentPane().setBackground(Color.black);
			try
			{
				frame.getContentPane().add(canvas, BorderLayout.CENTER);
			}
			catch (IllegalArgumentException e)
			{
				logger().log(ALogType.CRITICAL, "Fullscreen not supported on the desired monitor, aborting", e);
				
				frame.removeAll();
				frame.getContentPane().removeAll();
				frame = old;
				frame.setVisible(true);
				Options.instance().fullscreen = !Options.instance().fullscreen;
				return;
			}
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
					logger().log(ALogType.DEBUG, "Fullscreening on current GraphicsDevice...");
					frame.getGraphicsConfiguration().getDevice().setFullScreenWindow(frame);
				}
				catch (Exception e1)
				{
					logger().log(ALogType.WARNING, "Error fullscreening current GraphicsDevice", e1);
					
					try
					{
						logger().log(ALogType.DEBUG, "Fullscreening on default GraphicsDevice...");
						GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(frame);
					}
					catch (Exception e)
					{
						logger().log(ALogType.CRITICAL, "Error fullscreening default GraphicsDevice", e);
						
						GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
						
						boolean didIt = false;
						
						for (int i = 0; i < gds.length; i++)
						{
							try
							{
								logger().log(ALogType.CRITICAL, "Fullscreening on GraphicsDevice[" + i + "]...");
								gds[i].setFullScreenWindow(frame);// Makes it full screen
								
								// TODO test on Ben's Mac
								// if (System.getProperty("os.name").indexOf("Mac OS X") >= 0)
								// {
								// this.setVisible(false);
								// this.setVisible(true);
								// }
								didIt = true;
								break;
							}
							catch (Exception e2)
							{
								logger().log(ALogType.CRITICAL, "Error fullscreening GraphicsDevice[" + i + "]", e2);
							}
						}
						
						if (!didIt)
						{
							logger().log(ALogType.CRITICAL, "Fullscreen not supported, attempting to revert back");
							setFullScreen(false);
							return;
						}
					}
				}
				
				logger().log(ALogType.DEBUG, "Successfully switched to fullscreen mode");
			}
			else
			{
				logger().log(ALogType.DEBUG, "Successfully switched to windowed mode");
			}
			
			canvas.requestFocus();
			
			old.removeAll();
			old.getContentPane().removeAll();
			
			updateCursorHiding();
		}
		else
		{
			logger().log(ALogType.DEBUG, "Game is already in " + (Options.instance().fullscreen ? "fullscreen" : "windowed") + " mode");
		}
	}
	
	// public void setFullScreen(boolean isFullscreen)
	// {
	// GraphicsDevice device = frame.getGraphicsConfiguration().getDevice();
	//
	// if (device.isFullScreenSupported())
	// {
	// if (isFullscreen)
	// {
	// frame.setUndecorated(true);
	// frame.setResizable(true);
	//
	// frame.addFocusListener(new FocusListener()
	// {
	//
	// @Override
	// public void focusGained(FocusEvent arg0)
	// {
	// frame.setAlwaysOnTop(true);
	// }
	//
	// @Override
	// public void focusLost(FocusEvent arg0)
	// {
	// frame.setAlwaysOnTop(false);
	// }
	// });
	//
	// frame.pack();
	//
	// device.setFullScreenWindow(frame);
	// }
	// else
	// {
	// device.setFullScreenWindow(null);
	// }
	// }
	// else
	// {
	// logger().log(ALogType.WARNING, "Fullscreen mode not supported");
	// }
	// }
	
	public void setID(short id)
	{
		this.id = id;
	}
	
	public void setRole(Role role)
	{
		this.role = role;
	}
	
	public void setSpectatingRole(Role role)
	{
		this.spectatingRole = role;
	}
	
	@Override
	public void startThis()
	{
		// Initialises the console output.
		logger().log(ALogType.DEBUG, "Initializing external console output...");
		Console.initialize();
		
		logger().log(ALogType.DEBUG, "Initializing logging streams...");
		ALogger.initStreams();
		
		logger().log(ALogType.DEBUG, "Creating environment... (" + VersionUtil.VERSION_STRING + ")");
		
		canvas = new Canvas();
		frame = new JFrame(NAME);
		
		// Makes it so that when the window is resized, this ClientGame will resize the canvas accordingly
		canvas.addComponentListener(new ComponentListener()
		{
			private int lastWidth = 0;
			private int lastHeight = 0;
			
			@Override
			public void componentHidden(ComponentEvent e)
			{
			
			}
			
			@Override
			public void componentMoved(ComponentEvent e)
			{
				
			}
			
			@Override
			public void componentResized(ComponentEvent e)
			{
				// Stops it from detecting the resizeGame method from resizing its bounds.
				if (lastWidth != canvas.getWidth() || lastHeight != canvas.getHeight())
				{
					resizeGame(canvas.getWidth(), canvas.getHeight(), false);
					
					lastWidth = canvas.getWidth();
					lastHeight = canvas.getHeight();
				}
			}
			
			@Override
			public void componentShown(ComponentEvent e)
			{
				
			}
		});
			
		canvas.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		canvas.setPreferredSize(new Dimension(WIDTH * DEFAULT_SCALE, HEIGHT * DEFAULT_SCALE));
		
		frame.setIconImage(ICON);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.getContentPane().setBackground(Color.black);
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		frame.pack();
		frame.setResizable(false);
		
		frame.setVisible(true);
		frame.setResizable(true);
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
					logger().log(ALogType.CRITICAL, "Thread failed to sleep", e);
				}
			}
			
			@Override
			public void stopThis()
			{
				super.stopThis();
				loading.getImage().flush();
			}
		};
		renderLoading.startThis();
		
		// Allows key listens for TAB and such
		canvas.setFocusTraversalKeysEnabled(false);
		
		// DO THE LOADING
		
		logger().log(ALogType.DEBUG, "Loading game...");
		
		logger().log(ALogType.DEBUG, "Loading options...");
		
		Options.instance();
		
		logger().log(ALogType.DEBUG, "Loading assets...");
		
		Assets.load();
		AudioClip.updateVolumesFromOptions();
		
		logger().log(ALogType.DEBUG, "Initializing game variables...");
		
		updateMem = new ModulusCounter((int) ticksPerSecond);
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		debugFont = Assets.getFont(AssetType.FONT_BLACK);
		screen = new Texture(AssetType.INVALID, new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB));
		input = new InputHandler(this);
		
		getPlayers().add(new PlayerEntity(false, Role.PLAYER1, new Vector2DDouble(0, 0)));
		getPlayers().add(new PlayerEntity(false, Role.PLAYER2, new Vector2DDouble(0, 0)));
		
		lights = new HashMap<Role, FlickeringLight>(2);
		lights.put(Role.PLAYER1, new FlickeringLight(false, -1, new Vector2DDouble(0, 0), 50, 47, 4));
		lights.put(Role.PLAYER2, new FlickeringLight(false, -1, new Vector2DDouble(0, 0), 50, 47, 4));
		
		setCurrentScreen(new GuiMainMenu());
		
		// WHEN FINISHED LOADING
		
		resizeGame(WIDTH * Options.instance().scale, HEIGHT * Options.instance().scale, true);
		
		if (Options.instance().fullscreen)
		{
			// Need to do this to trick game into thinking that it isn't in fullscreen already
			Options.instance().fullscreen = false;
			setFullScreen(true);
		}
		
		// End the loading screen
		logger().log(ALogType.DEBUG, "Disposing of loading screen...");
		renderLoading.stopThis();
		canvas.repaint();
		
		double loadTime = (System.currentTimeMillis() - startLoadTime) / 1000.0D;
		
		logger().log(ALogType.DEBUG, "Game loaded. Took " + loadTime + " seconds");
		
		music = Assets.getAudioClip(AssetType.AUDIO_MENU_MUSIC);
		
		updateCursorHiding();
		
		super.startThis();
	}
	
	@Override
	public void stopThis()
	{
		super.stopThis();
		
		getLevels().clear();
		setCurrentLevel(null);
		setCurrentScreen(null);
		
		Options.instance().save();
		
		Assets.dispose();
		
		game = null;
		
		if (ServerGame.instance() != null) ServerGame.instance().stopThis();
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (updateMem.isAtInterval())
		{
			Runtime rt = Runtime.getRuntime();
			totalMemory = rt.freeMemory();
			usedMemory = rt.totalMemory() - rt.freeMemory();
		}
		
		if (hideCursor > 0)
		{
			if (hideCursor == 1)
			{
				updateCursorHiding();
			}
			hideCursor--;
		}
		
		if (exitGame)
		{
			exitFromGame(ExitGameReason.SERVER_CLOSED);
			exitGame = false;
		}
		
		if (updatePlayerList) hasStartedUpdateList = true;
		
		for (Entry<Role, FlickeringLight> light : lights.entrySet())
		{
			PlayerEntity player = getPlayer(light.getKey());
			
			light.getValue().addToLevel(player.getLevel());
			light.getValue().setPosition(new Vector2DDouble(player.getPosition().getX() + (16 / 2), player.getPosition().getY() + (16 / 2)));
		}
		
		if (input.f11.isPressedFiltered())
		{
			setFullScreen(!Options.instance().fullscreen);
		}
		
		// Debug keys
		if (input.control.isPressed())
		{
			if (input.slash.isPressedFiltered())
			{
				setCurrentScreen(new GuiCommand(getCurrentScreen()));
			}
			if ((!Console.instance().getJFrame().isVisible() && input.nine.isReleasedFiltered()) || (Console.instance().getJFrame().isVisible() && input.nine.isPressedFiltered()))
			{
				consoleDebug = !consoleDebug;
				
				// Prevents keys from getting stuck
				input.control.setPressed(false);
				input.nine.setPressed(false);
				
				Console.setVisible(consoleDebug);
				
				logger().log(ALogType.DEBUG, "Show Console: " + consoleDebug);
			}
			if (input.one.isPressedFiltered())
			{
				hudDebug = !hudDebug;
				
				logger().log(ALogType.DEBUG, "Debug Hud: " + hudDebug);
			}
			if (input.two.isPressedFiltered() && isInGame())
			{
				hitboxDebug = !hitboxDebug;
				
				logger().log(ALogType.DEBUG, "Show Hitboxes: " + hitboxDebug);
			}
			if (input.four.isPressedFiltered() && isInGame())
			{
				// TODO saving levels?
				logger().log(ALogType.DEBUG, "Copied current level save data to clipboard");
			}
			
			// Can no longer access build mode from in-game
			// if (input.zero.isPressedFiltered() && isInGame())
			// {
			// buildMode = !buildMode;
			//
			// logger().log(ALogType.DEBUG, "Build Mode: " + buildMode);
			//
			// if (getThisPlayer() != null) getThisPlayer().setCameraToFollow(!buildMode);
			//
			// updateCursorHiding();
			// }
			
			if (input.shift.isPressed() && input.delete.isPressedFiltered())
			{
				quit();
			}
		}
		
		if (isInGame() && !(getCurrentScreen() instanceof GuiInGameMenu) && !(getCurrentScreen() instanceof GuiOptionsMenu) && input.escape.isReleasedFiltered())
		{
			setCurrentScreen(new GuiInGameMenu(getCurrentScreen()));
		}
		
		if (getCurrentScreen() != null)
		{
			getCurrentScreen().tick();
		}
		
		if (!waitingForOthersToLoad && getCurrentLevel() != null)
		{
			getCurrentLevel().tick();
		}
		
		if (hasStartedUpdateList)
		{
			hasStartedUpdateList = false;
			updatePlayerList = false;
		}
	}
	
	public void updateCursorHiding()
	{
		updateCursorHiding(false);
	}
	
	/**
	 * @deprecated only use updateCursorHiding() unless this is being updated from mouse movement
	 * 
	 * @param updateFromMouse
	 */
	@Deprecated
	public void updateCursorHiding(boolean updateFromMouse)
	{
		boolean isFocused = input().isGameInFocus();
		
		if (updateFromMouse)
		{
			if (isFocused)
			{
				hideCursor = Byte.MAX_VALUE;
				frame.setCursor(Cursor.getDefaultCursor());
			}
			else
			{
				hideCursor = 1;
			}
		}
		else
		{
			// Hides the cursor if in game, no GUI, no build mode, and the game is focused
			if (getCurrentScreen() == null && getCurrentLevel() != null && !isBuildMode() && isFocused)
			{
				frame.setCursor(blankCursor);
			}
			else
			{
				frame.setCursor(Cursor.getDefaultCursor());
			}
		}
	}
	
	public void updatePlayerList()
	{
		this.updatePlayerList = true;
	}
}
