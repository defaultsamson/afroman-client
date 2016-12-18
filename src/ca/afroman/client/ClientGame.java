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
import ca.afroman.gui.build.GuiLevelSelect;
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
import ca.afroman.packet.PacketLoadLevels;
import ca.afroman.packet.PacketLogin;
import ca.afroman.packet.PacketPingClientServer;
import ca.afroman.packet.PacketPingServerClient;
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
	
	public static final int RECEIVE_PACKET_BUFFER_LIMIT = 64;
	
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
	private short ping = 0;
	private short ping1 = 0;
	private short ping2 = 0;
	
	/** Keeps track of the amount of ticks passed to time memory usage updates. */
	private String memDisplay = "";
	private ModulusCounter updateMem;
	private long totalMemory = 0;
	private long usedMemory = 0;
	
	private Font debugFontWhite;
	private Font debugFontBlack;
	private Cursor blankCursor;
	private byte hideCursor = 0;
	
	/** Whether or not to exit from the game and go to the main menu. */
	private boolean exitGame = false;
	private boolean hasStartedUpdateList = false;
	
	private GuiScreen currentScreen = null;
	private AudioClip music;
	
	private boolean waitingForOthersToLoad = false;
	
	public ClientGame()
	{
		super(false, newDefaultThreadGroupInstance(), "Game", 60);
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
	
	private String getPingDisplay(String tag, int ping)
	{
		if (ping != PacketPingServerClient.NONE)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(tag);
			sb.append(": ");
			
			if (ping1 == PacketPingServerClient.OVER_MAX)
			{
				sb.append('>');
				sb.append(PacketPingServerClient.MAX_SENDABLE);
			}
			else
			{
				sb.append(ping);
			}
			
			return sb.toString();
		}
		return null;
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
						case TEST_PING:
							ping = (short) (packet.getContent().get() + Byte.MAX_VALUE);
							ping1 = (short) (packet.getContent().get() + Byte.MAX_VALUE);
							ping2 = (short) (packet.getContent().get() + Byte.MAX_VALUE);
							
							sockets().sender().sendPacket(new PacketPingClientServer());
							break;
						case DENY_JOIN:
						{
							setCurrentScreen(new GuiJoinServer(new GuiMainMenu()));
							
							DenyJoinReason reason = DenyJoinReason.fromOrdinal(packet.getContent().get());
							
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
							id = packet.getContent().getShort();
							
							sockets().initServerTCPConnection();
							break;
						case PLAYER_MOVE:
						{
							Role role = Role.fromOrdinal(packet.getContent().get());
							if (role != Role.SPECTATOR && role != getRole())
							{
								PlayerEntity player = getPlayer(role);
								if (player != null)
								{
									byte x = packet.getContent().get();
									byte y = packet.getContent().get();
									
									player.autoMove(x, y);
								}
							}
						}
							break;
						case UPDATE_PLAYERLIST:
						{
							updatePlayerList();
							List<ConnectedPlayer> players = new ArrayList<ConnectedPlayer>();
							ByteBuffer buf = packet.getContent();
							
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
						case START_SERVER:
						{
							boolean serverStarted = packet.getContent().get() == 1;
							
							// Server was started
							if (serverStarted)
							{
								
							}
							// Server was stopped
							else
							{
								exitGame = true;
							}
						}
							break;
						case LOAD_LEVELS:
						{
							boolean sendingLevels = packet.getContent().get() == 1;
							
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
								
								if (music.isRunning())
								{
									music.stop();
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
						case SET_PLAYER_LEVEL:
						{
							Role role = Role.fromOrdinal(packet.getContent().get());
							
							PlayerEntity player = getPlayer(role);
							
							if (player != null)
							{
								LevelType levelType = LevelType.fromOrdinal(packet.getContent().getShort());
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
						case SET_PLAYER_POSITION:
						{
							boolean forcePos = false;
							byte roleOrd = packet.getContent().get();
							
							if (roleOrd >= Role.values().length)
							{
								forcePos = true;
								roleOrd -= Role.values().length;
							}
							
							Role role = Role.fromOrdinal(roleOrd);
							
							PlayerEntity player = getPlayer(role);
							
							if (player != null)
							{
								Vector2DDouble pos = new Vector2DDouble(packet.getContent().getInt(), packet.getContent().getInt());
								
								// If force the position, then force it.
								// Else, if the player is outside a given range of the server position force it into positionThe ho
								if (forcePos || player.getPosition().isDistanceGreaterThan(pos, 10D))
								{
									player.setPosition(pos);
								}
							}
							else
							{
								logger().log(ALogType.WARNING, "No player with role " + role);
							}
						}
							break;
						case ACTIVATE_TRIGGER:
						{
							LevelType levelType = LevelType.fromOrdinal(packet.getContent().getShort());
							Level level = getLevel(levelType);
							
							if (level != null)
							{
								int id = packet.getContent().getInt();
								
								Event event = level.getEvent(id);
								
								if (event != null)
								{
									byte ord = packet.getContent().get();
									Role role = Role.fromOrdinal(ord);
									
									if (role != null)
									{
										PlayerEntity player = getPlayer(role);
										
										if (player != null)
										{
											event.trigger(player);
										}
										else
										{
											logger().log(ALogType.WARNING, "No player found with role " + role);
										}
									}
									else
									{
										logger().log(ALogType.WARNING, "No role found with ordinal " + ord);
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
					// TODO Unable to print who was sending this packet
				}
			}
		}
		catch (Exception e)
		{
			// TODO logger().log(ALogType.IMPORTANT, "Exception upon packet parsing", e);
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
				// Displays hud info
				{
					Vector2DInt hud = new Vector2DInt(1, 0);
					Vector2DInt shadow = hud.clone().add(1, 1);
					
					debugFontBlack.render(screen, shadow, memDisplay);
					debugFontWhite.render(screen, hud, memDisplay);
					String t = "TPS: " + tps;
					debugFontBlack.render(screen, shadow.add(0, 10), t);
					debugFontWhite.render(screen, hud.add(0, 10), t);
					String f = "FPS: " + fps;
					debugFontBlack.render(screen, shadow.add(0, 10), f);
					debugFontWhite.render(screen, hud.add(0, 10), f);
					
					Vector2DInt version = new Vector2DInt(2, HEIGHT - 8);
					Vector2DInt versionShadow = new Vector2DInt(1, HEIGHT - 9);
					debugFontBlack.render(screen, version, "V");
					debugFontWhite.render(screen, versionShadow, "V");
					
					String ver = new StringBuilder().append(VersionUtil.VERSION_STRING).toString();
					debugFontBlack.render(screen, version.add(8, 0), ver);
					debugFontWhite.render(screen, versionShadow.add(8, 0), ver);
				}
				
				PlayerEntity player = getThisPlayer();
				
				if (player != null && player.getLevel() != null)
				{
					String x = "x: " + player.getPosition().getX();
					debugFontBlack.render(screen, new Vector2DInt(2, 51), x);
					debugFontWhite.render(screen, new Vector2DInt(1, 50), x);
					String y = "y: " + player.getPosition().getY();
					debugFontBlack.render(screen, new Vector2DInt(2, 61), y);
					debugFontWhite.render(screen, new Vector2DInt(1, 60), y);
				}
			}
			
			// Displays pings
			if ((hudDebug || input.tab.isPressed()) && sockets() != null)
			{
				String you = getPingDisplay("You", ping);
				String p1p = getPingDisplay("P1", ping1);
				String p2p = getPingDisplay("P2", ping2);
				
				if (you != null || p1p != null || p2p != null)
				{
					// Distance from the right to draw
					Vector2DInt text = new Vector2DInt(WIDTH - 2, 0);
					Vector2DInt shadow = text.clone().add(1, 1);
					
					debugFontBlack.renderRight(screen, shadow, "PING");
					debugFontWhite.renderRight(screen, text, "PING");
					
					shadow.add(0, 10);
					text.add(0, 10);
					
					if (you != null)
					{
						debugFontBlack.renderRight(screen, shadow, you);
						debugFontWhite.renderRight(screen, text, you);
						
						// Moves text down to next line
						// in the 2 lines above, CHAR_WIDTH moves the the left slightly so that the colons line up
						// So this moves it back right
						shadow.add(0, 10);
						text.add(0, 10);
					}
					
					if (p1p != null && role != Role.PLAYER1)
					{
						debugFontBlack.renderRight(screen, shadow, p1p);
						debugFontWhite.renderRight(screen, text, p1p);
						
						// Moves text down to next line
						shadow.add(0, 10);
						text.add(0, 10);
					}
					
					if (p2p != null && role != Role.PLAYER2)
					{
						debugFontBlack.renderRight(screen, shadow, p2p);
						debugFontWhite.renderRight(screen, text, p2p);
						
						// Moves text down to next line
						shadow.add(0, 10);
						text.add(0, 10);
					}
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
	
	public void setID(short id)
	{
		this.id = id;
	}
	
	public void setIsBuildMode(boolean isBuild)
	{
		buildMode = isBuild;
	}
	
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
			
			if (isBuildMode())
			{
				ClientGame.instance().setCurrentScreen(new GuiLevelSelect(null, false));
			}
			else
			{
				waitingForOthersToLoad = true;
				
				sockets().sender().sendPacket(new PacketLoadLevels(false));
			}
		}
		else
		{
			buildMode = false;
			waitingForOthersToLoad = false;
			id = -1;
			
			stopSocket();
			
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
		
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		
		// LOAD SOME SHITE
		logger().log(ALogType.DEBUG, "Loading game...");
		logger().log(ALogType.DEBUG, "Loading options...");
		Options.instance(); // Loads options
		resizeGame(WIDTH * Options.instance().scale, HEIGHT * Options.instance().scale, true); // Sets the window size that of which was found from the options
		
		// Loading screen
		final Texture loading = Texture.fromResource(AssetType.INVALID, "loading.png");
		DynamicThread renderLoading = new DynamicThread(false, this.getThread().getThreadGroup(), "Loading-Display")
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
		
		// DO MORE LOADING
		
		logger().log(ALogType.DEBUG, "Loading assets...");
		
		Assets.load();
		AudioClip.updateVolumesFromOptions();
		
		logger().log(ALogType.DEBUG, "Initializing game variables...");
		
		updateMem = new ModulusCounter((int) ticksPerSecond);
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		debugFontWhite = Assets.getFont(AssetType.FONT_WHITE);
		debugFontBlack = Assets.getFont(AssetType.FONT_BLACK);
		screen = new Texture(AssetType.INVALID, new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB), 0);
		input = new InputHandler(this);
		
		getPlayers().add(new PlayerEntity(false, Role.PLAYER1, new Vector2DDouble(0, 0)));
		getPlayers().add(new PlayerEntity(false, Role.PLAYER2, new Vector2DDouble(0, 0)));
		
		lights = new HashMap<Role, FlickeringLight>(2);
		lights.put(Role.PLAYER1, new FlickeringLight(true, new Vector2DDouble(0, 0), 50, 45, 6));
		lights.put(Role.PLAYER2, new FlickeringLight(true, new Vector2DDouble(0, 0), 50, 45, 6));
		
		// WHEN FINISHED LOADING
		
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
		
		setIsInGame(false);
		
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
			totalMemory = rt.totalMemory();
			usedMemory = rt.totalMemory() - rt.freeMemory();
			
			memDisplay = "MEM: " + ((double) Math.round(((double) usedMemory / (double) totalMemory) * 10) / 10) + "% (" + (usedMemory / 1024 / 1024) + "MB)";
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
				if (getCurrentLevel() != null)
				{
					getCurrentLevel().copyToClipboard();
				}
				logger().log(ALogType.DEBUG, "Copied current level save data to clipboard");
			}
			
			if (input.zero.isPressedFiltered())
			{
				if (isInGame())
				{
					toggleLevelSelect();
				}
				else
				{
					if (getCurrentScreen() instanceof GuiMainMenu)
					{
						((GuiMainMenu) getCurrentScreen()).toggleBuildModeButton();
					}
				}
			}
			
			if (input.shift.isPressed() && input.delete.isPressedFiltered())
			{
				quit();
			}
		}
		
		if (isInGame() && !(getCurrentScreen() instanceof GuiInGameMenu) && !(getCurrentScreen() instanceof GuiOptionsMenu) && input.escape.isReleasedFiltered())
		{
			if (isBuildMode() || getCurrentScreen() instanceof GuiLevelSelect)
			{
				toggleLevelSelect();
			}
			else
			{
				setCurrentScreen(new GuiInGameMenu(getCurrentScreen()));
			}
		}
		
		if (getCurrentScreen() != null)
		{
			getCurrentScreen().tick();
		}
		
		if (!waitingForOthersToLoad) // && getCurrentLevel() != null
		{
			for (Level l : getLevels())
			{
				l.tick();
			}
			// getCurrentLevel().tick();
		}
		
		if (hasStartedUpdateList)
		{
			hasStartedUpdateList = false;
			updatePlayerList = false;
		}
	}
	
	private void toggleLevelSelect()
	{
		// Go back to level selection
		if (isBuildMode())
		{
			if (getCurrentLevel() != null)
			{
				getCurrentLevel().cleanupBuildMode(getCurrentLevel().getBuildMode());
			}
			setIsBuildMode(false);
			
			setCurrentScreen(new GuiLevelSelect(null, getCurrentLevel() != null)); // getCurrentLevel() != null
		}
		// Go out to
		else if (getCurrentScreen() instanceof GuiLevelSelect)
		{
			if (getCurrentLevel() != null)
			{
				setCurrentScreen(null);
				setIsBuildMode(true);
				getCurrentLevel().loadBuildMode(getCurrentLevel().getBuildMode());
			}
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
