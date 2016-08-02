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
import ca.afroman.assets.Texture;
import ca.afroman.entity.ClientPlayerEntity;
import ca.afroman.entity.api.ClientAssetEntity;
import ca.afroman.entity.api.Direction;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.events.HitboxTrigger;
import ca.afroman.events.IEvent;
import ca.afroman.events.TriggerType;
import ca.afroman.gfx.FlickeringLight;
import ca.afroman.gfx.LightMapState;
import ca.afroman.gfx.PointLight;
import ca.afroman.gui.GuiClickNotification;
import ca.afroman.gui.GuiConnectToServer;
import ca.afroman.gui.GuiJoinServer;
import ca.afroman.gui.GuiLobby;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.gui.GuiScreen;
import ca.afroman.gui.GuiSendingLevels;
import ca.afroman.input.InputHandler;
import ca.afroman.level.ClientLevel;
import ca.afroman.level.LevelObjectType;
import ca.afroman.level.LevelType;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketConfirmReceive;
import ca.afroman.packet.PacketLogin;
import ca.afroman.server.DenyJoinReason;
import ca.afroman.server.ServerGame;
import ca.afroman.server.ServerSocketManager;
import ca.afroman.thread.DynamicThread;
import ca.afroman.thread.DynamicTickRenderThread;
import ca.afroman.util.ByteUtil;
import ca.afroman.util.IDCounter;
import samson.stream.Console;

public class ClientGame extends DynamicTickRenderThread
{
	public static final int WIDTH = 240;
	public static final int HEIGHT = WIDTH / 16 * 9;
	public static final int SCALE = 3;
	public static final String NAME = "Cancer: The Adventures of Afro Man";
	public static final short VERSION = 30;
	public static final BufferedImage ICON = Texture.fromResource(AssetType.INVALID, "icon/32x.png").getImage();
	
	public static final int RECEIVE_PACKET_BUFFER_LIMIT = 128;
	
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
	private String port = "";
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
					logger().log(ALogType.CRITICAL, "Thread failed to sleep", e);
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
		Console.initialize();
		ALogger.initStreams();
		Assets.load();
		
		screen = new Texture(AssetType.INVALID, new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB));
		input = new InputHandler(this);
		levels = new ArrayList<ClientLevel>();
		receivedPackets = new ArrayList<Integer>();
		packets = new ArrayList<BytePacket>();
		
		players = new ArrayList<ClientPlayerEntity>();
		getPlayers().add(new ClientPlayerEntity(Role.PLAYER1, 0, 0));
		getPlayers().add(new ClientPlayerEntity(Role.PLAYER2, 0, 0));
		
		lights = new HashMap<Role, FlickeringLight>();
		lights.put(Role.PLAYER1, new FlickeringLight(false, -1, 0, 0, 50, 47, 4));
		lights.put(Role.PLAYER2, new FlickeringLight(false, -1, 0, 0, 50, 47, 4));
		
		setCurrentScreen(new GuiMainMenu());
		
		// WHEN FINISHED LOADING
		
		// End the loading screen
		renderLoading.stopThis();
		frame.setResizable(true);
		canvas.repaint();
		
		double loadTime = (System.currentTimeMillis() - startLoadTime) / 1000.0D;
		
		logger().log(ALogType.DEBUG, "Game Loaded. Took " + loadTime + " seconds");
		
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
	
	@SuppressWarnings("deprecation")
	@Override
	public void tick()
	{
		tickCount++;
		
		synchronized (packets)
		{
			for (BytePacket pack : packets)
			{
				parsePacket(pack);
			}
			
			packets.clear();
		}
		
		if (exitGame)
		{
			ClientGame.instance().exitFromGame(ExitGameReason.SERVER_CLOSED);
		}
		
		if (updatePlayerList) hasStartedUpdateList = true;
		
		for (Entry<Role, FlickeringLight> light : lights.entrySet())
		{
			ClientPlayerEntity player = getPlayer(light.getKey());
			
			light.getValue().addToLevel(player.getLevel());
			light.getValue().setX(player.getX() + (16 / 2));
			light.getValue().setY(player.getY() + (16 / 2));
		}
		
		if (input.consoleDebug.isReleasedFiltered())
		{
			consoleDebug = !consoleDebug;
			
			Console.setVisible(consoleDebug);
			
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
			
			this.getPlayer(sockets().getServerConnection().getRole()).setCameraToFollow(!buildMode);
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
	
	private List<Integer> receivedPackets; // The ID's of all the packets that have been received
	private List<BytePacket> packets;
	
	private boolean exitGame = false;
	
	@SuppressWarnings("deprecation")
	public void parsePacket(BytePacket packet)
	{
		IPConnection sender = packet.getConnections().get(0);
		
		// If is the server sending the packet
		if (sockets().getServerConnection().getConnection().equals(sender))
		{
			if (packet.getID() != -1)
			{
				for (int packID : receivedPackets)
				{
					if (packID == packet.getID())
					{
						logger().log(ALogType.DEBUG, "Received packet already: " + packID);
						
						// If the packet with this ID has already been received, tell the server to stop sending it, and don't parse it
						sockets().sender().sendPacket(new PacketConfirmReceive(packet.getID()));
						return;
					}
				}
				
				// If the packet with the ID has not already been received
				sockets().sender().sendPacket(new PacketConfirmReceive(packet.getID()));
				// Add it to the list
				receivedPackets.add(packet.getID());
			}
			
			switch (packet.getType())
			{
				default:
				case INVALID:
					logger().log(ALogType.WARNING, "[CLIENT] INVALID PACKET");
					break;
				case DENY_JOIN:
				{
					ClientGame.instance().setCurrentScreen(new GuiJoinServer(new GuiMainMenu()));
					
					DenyJoinReason reason = DenyJoinReason.fromOrdinal(ByteUtil.shortFromBytes(new byte[] { packet.getContent()[0], packet.getContent()[1] }));
					
					switch (reason)
					{
						default:
							new GuiClickNotification(ClientGame.instance().getCurrentScreen(), "CAN'T CONNECT", "TO SERVER");
							break;
						case DUPLICATE_USERNAME:
							new GuiClickNotification(ClientGame.instance().getCurrentScreen(), "DUPLICATE", "USERNAME");
							break;
						case FULL_SERVER:
							new GuiClickNotification(ClientGame.instance().getCurrentScreen(), "SERVER", "FULL");
							break;
						case NEED_PASSWORD:
							new GuiClickNotification(ClientGame.instance().getCurrentScreen(), "INVALID", "PASSWORD");
							break;
						case OLD_CLIENT:
							new GuiClickNotification(ClientGame.instance().getCurrentScreen(), "CLIENT", "OUTDATED");
							break;
						case OLD_SERVER:
							new GuiClickNotification(ClientGame.instance().getCurrentScreen(), "SERVER", "OUTDATED");
							break;
					}
				}
					break;
				case ASSIGN_CLIENTID:
					ClientGame.instance().sockets().getServerConnection().setID(ByteUtil.shortFromBytes(new byte[] { packet.getContent()[0], packet.getContent()[1] }));
					break;
				case UPDATE_PLAYERLIST:
				{
					ClientGame.instance().updatePlayerList();
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
					
					ClientGame.instance().sockets().updateConnectedPlayer(players);
					
					if (ClientGame.instance().getCurrentScreen() instanceof GuiConnectToServer)
					{
						ClientGame.instance().setCurrentScreen(new GuiLobby(null));
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
						// Prepare the level storage for new levels to be sent
						ClientGame.instance().getLevels().clear();
						
						// Display the loading level screen
						if (!(ClientGame.instance().getCurrentScreen() instanceof GuiSendingLevels))
						{
							ClientGame.instance().setCurrentScreen(new GuiSendingLevels(null));
						}
					}
					else
					{
						// Stop displaying the loading level screen
						if (ClientGame.instance().getCurrentScreen() instanceof GuiSendingLevels)
						{
							ClientGame.instance().setCurrentScreen(ClientGame.instance().getCurrentScreen().getParent());
						}
					}
				}
					break;
				case INSTANTIATE_LEVEL:
				{
					LevelType levelType = LevelType.fromOrdinal(ByteUtil.shortFromBytes(new byte[] { packet.getContent()[0], packet.getContent()[1] }));
					
					if (ClientGame.instance().getLevelByType(levelType) == null)
					{
						ClientGame.instance().getLevels().add(new ClientLevel(levelType));
					}
					else
					{
						logger().log(ALogType.WARNING, "[CLIENT] Level with type " + levelType + " already exists");
					}
				}
					break;
				case REMOVE_LEVEL_OBJECT:
				{
					ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
					
					LevelType levelType = LevelType.fromOrdinal(buf.getShort());
					ClientLevel level = ClientGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						LevelObjectType objType = LevelObjectType.fromOrdinal(buf.getShort());
						
						int id = buf.getInt();
						
						boolean removed = false;
						
						switch (objType)
						{
							default:
								break;
							case TILE:
								Entity tile = level.getTile(id);
								
								if (tile != null)
								{
									tile.removeTileFromLevel();
									removed = true;
								}
								break;
							case HITBOX:
								Hitbox box = level.getHitbox(id);
								
								if (box != null)
								{
									box.removeFromLevel();
									removed = true;
								}
								break;
							case POINT_LIGHT:
								PointLight light = level.getLight(id);
								
								if (light != null)
								{
									light.removeFromLevel();
									removed = true;
								}
								break;
							case HITBOX_TRIGGER:
								IEvent event = level.getScriptedEvent(id);
								
								if (event != null)
								{
									event.removeFromLevel();
									removed = true;
								}
								break;
						}
						
						if (!removed)
						{
							logger().log(ALogType.WARNING, "Could not remove object id " + id + " of type " + objType);
						}
					}
					else
					{
						logger().log(ALogType.WARNING, "No level with type " + levelType);
					}
				}
				case ADD_LEVEL_TILE:
				{
					ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
					
					LevelType levelType = LevelType.fromOrdinal(buf.getShort());
					ClientLevel level = ClientGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						byte layer = buf.get();
						int id = buf.getInt();
						AssetType asset = AssetType.fromOrdinal(buf.getInt());
						
						double x = buf.getInt();
						double y = buf.getInt();
						
						ClientAssetEntity tile = new ClientAssetEntity(id, asset, x, y);
						tile.addTileToLevel(level, layer);
					}
					else
					{
						logger().log(ALogType.WARNING, "No level with type " + levelType);
					}
				}
					break;
				case ADD_LEVEL_HITBOX:
				{
					ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
					
					LevelType levelType = LevelType.fromOrdinal(buf.getShort());
					ClientLevel level = ClientGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						int id = buf.getInt();
						double x = buf.getInt();
						double y = buf.getInt();
						double width = buf.getInt();
						double height = buf.getInt();
						
						Hitbox box = new Hitbox(id, x, y, width, height);
						box.addToLevel(level);
					}
					else
					{
						logger().log(ALogType.WARNING, "No level with type " + levelType);
					}
				}
					break;
				case ADD_LEVEL_POINTLIGHT:
				{
					ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
					
					LevelType levelType = LevelType.fromOrdinal(buf.getShort());
					ClientLevel level = ClientGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						int id = buf.getInt();
						double x = buf.getInt();
						double y = buf.getInt();
						double radius = buf.getInt();
						
						PointLight light = new PointLight(false, id, x, y, radius);
						light.addToLevel(level);
					}
					else
					{
						logger().log(ALogType.WARNING, "No level with type " + levelType);
					}
				}
					break;
				case ADD_LEVEL_PLAYER:
				{
					Role role = Role.fromOrdinal(packet.getContent()[0]);
					
					ClientPlayerEntity player = ClientGame.instance().getPlayer(role);
					
					if (player != null)
					{
						LevelType levelType = LevelType.fromOrdinal(ByteUtil.shortFromBytes(Arrays.copyOfRange(packet.getContent(), 1, 3)));
						ClientLevel level = ClientGame.instance().getLevelByType(levelType);
						
						player.addToLevel(level);
						
						// If it's adding the player that this player is, center the camera on them
						if (player.getRole() == ClientGame.instance().sockets().getServerConnection().getRole())
						{
							ClientGame.instance().setCurrentLevel(level);
							player.setCameraToFollow(true);
						}
					}
				}
					break;
				case SET_PLAYER_LOCATION:
				{
					ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
					
					ClientPlayerEntity player = ClientGame.instance().getPlayer(Role.fromOrdinal(buf.get()));
					
					if (player != null)
					{
						player.setDirection(Direction.fromOrdinal(buf.get()));
						player.setLastDirection(Direction.fromOrdinal(buf.get()));
						player.setX(buf.getInt());
						player.setY(buf.getInt());
					}
				}
					break;
				case CONFIRM_RECEIVED:
				{
					int sentID = ByteUtil.intFromBytes(new byte[] { packet.getContent()[0], packet.getContent()[1], packet.getContent()[2], packet.getContent()[3] });
					
					sockets().sender().removePacket(sentID);
				}
					break;
				case ADD_EVENT_HITBOX_TRIGGER:
				{
					ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
					
					LevelType levelType = LevelType.fromOrdinal(buf.getShort());
					ClientLevel level = ClientGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						int id = buf.getInt();
						int x = buf.getInt();
						int y = buf.getInt();
						int width = buf.getInt();
						int height = buf.getInt();
						
						HitboxTrigger trig = new HitboxTrigger(false, id, x, y, width, height, null, null, null);
						trig.addToLevel(level);
					}
					else
					{
						logger().log(ALogType.WARNING, "No level with type " + levelType);
					}
				}
					break;
				case EDIT_EVENT_HITBOX_TRIGGER:
				{
					ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
					
					LevelType levelType = LevelType.fromOrdinal(buf.getShort());
					ClientLevel level = ClientGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						int id = buf.getInt();
						
						IEvent eHitbox = level.getScriptedEvent(id);
						
						if (eHitbox != null)
						{
							if (eHitbox instanceof HitboxTrigger)
							{
								HitboxTrigger hitbox = (HitboxTrigger) eHitbox;
								
								List<TriggerType> triggers = new ArrayList<TriggerType>();
								
								for (byte b : ByteUtil.extractBytes(buf, Byte.MIN_VALUE, Byte.MAX_VALUE))
									triggers.add(TriggerType.fromOrdinal(b));
								
								hitbox.setTriggerTypes(triggers);
								hitbox.setInTriggers(ByteUtil.extractIntList(buf, Byte.MIN_VALUE, Byte.MAX_VALUE));
								hitbox.setOutTriggers(ByteUtil.extractIntList(buf, Byte.MIN_VALUE, Byte.MAX_VALUE));
							}
							else
							{
								logger().log(ALogType.WARNING, "Event found is not an instance of HitboxTrigger");
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
			logger().log(ALogType.WARNING, "A server (" + sender.asReadable() + ") is tring to send a packet " + packet.getType().toString() + " to this unlistening client");
		}
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
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
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
				logger().log(ALogType.CRITICAL, "Fullscreen Mode not supported", e);
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
	
	public void setPort(String newPort)
	{
		this.port = newPort;
	}
	
	public String getPort()
	{
		return port;
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
		
		synchronized (receivedPackets)
		{
			receivedPackets.clear();
		}
		
		synchronized (packets)
		{
			packets.clear();
		}
		
		IDCounter.resetAll();
		
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
		
		int thyPortholio = ServerSocketManager.DEFAULT_PORT;
		
		if (port.length() > 0)
		{
			try
			{
				int newPort = Integer.parseInt(port);
				
				// Checks if the given port is out of range
				if (!(newPort < 0 || newPort > 0xFFFF)) thyPortholio = newPort;
			}
			catch (NumberFormatException e)
			{
				logger().log(ALogType.WARNING, "Failed to parse port", e);
			}
		}
		
		// Sets the port to whatever is now set
		setPort("" + thyPortholio);
		
		socketManager.setServerIP(getServerIP(), thyPortholio);
		socketManager.startThis();
		socketManager.sender().sendPacket(new PacketLogin(getUsername(), getPassword()));
	}
	
	public ClientLevel getCurrentLevel()
	{
		return currentLevel;
	}
	
	public void setCurrentLevel(ClientLevel newLevel)
	{
		// System.out.println("Setting Current Level: " + (newLevel == null ? "null" : newLevel.getType()));
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
		
		receivedPackets.clear();
	}
	
	public void updatePlayerList()
	{
		this.updatePlayerList = true;
	}
	
	public ClientPlayerEntity getThisPlayer()
	{
		Role role = sockets().getServerConnection().getRole();
		
		if (role != Role.SPECTATOR) return getPlayer(role);
		else
			return null;
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
	
	public static void main(String[] args)
	{
		startLoadTime = System.currentTimeMillis();
		
		game = new ClientGame();
		game.startThis();
	}
	
	public void addPacket(BytePacket pack)
	{
		synchronized (packets)
		{
			packets.add(pack);
		}
	}
}
