package ca.afroman.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.events.Event;
import ca.afroman.events.HitboxToggle;
import ca.afroman.events.HitboxTrigger;
import ca.afroman.events.TPTrigger;
import ca.afroman.events.TriggerType;
import ca.afroman.game.Game;
import ca.afroman.game.Role;
import ca.afroman.game.SocketManager;
import ca.afroman.level.Level;
import ca.afroman.level.LevelObjectType;
import ca.afroman.level.LevelType;
import ca.afroman.light.FlickeringLight;
import ca.afroman.light.PointLight;
import ca.afroman.log.ALogType;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.network.IncomingPacketWrapper;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketAddFlickeringLight;
import ca.afroman.packet.PacketAddHitbox;
import ca.afroman.packet.PacketAddHitboxToggle;
import ca.afroman.packet.PacketAddLevel;
import ca.afroman.packet.PacketAddPointLight;
import ca.afroman.packet.PacketAddTPTrigger;
import ca.afroman.packet.PacketAddTile;
import ca.afroman.packet.PacketAddTrigger;
import ca.afroman.packet.PacketDenyJoin;
import ca.afroman.packet.PacketEditHitboxToggle;
import ca.afroman.packet.PacketEditTPTrigger;
import ca.afroman.packet.PacketEditTrigger;
import ca.afroman.packet.PacketRemoveLevelObject;
import ca.afroman.packet.PacketSendLevels;
import ca.afroman.packet.PacketType;
import ca.afroman.resource.IDCounter;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ByteUtil;
import ca.afroman.util.VersionUtil;

public class ServerGame extends Game
{
	private static ServerGame game = null;
	
	public static ServerGame instance()
	{
		return game;
	}
	
	private static ThreadGroup newDefaultThreadGroupInstance()
	{
		return new ThreadGroup("Server");
	}
	
	private boolean isSendingLevels = false;
	
	private String password;
	
	private boolean stopServer = false;
	
	private ConsoleListener commandInput;
	
	public ServerGame(boolean commandLine, String ip, String password, String port)
	{
		super(newDefaultThreadGroupInstance(), "Game", true, 60);
		
		if (game == null) game = this;
		
		if (commandLine)
		{
			commandInput = new ConsoleListener();
			commandInput.startThis();
		}
		else
		{
			commandInput = null;
		}
		
		this.password = password;
		
		int valPort = SocketManager.validatedPort(port);
		boolean started = startSocket(ip, valPort);
		
		if (started)
		{
			logger().log(ALogType.DEBUG, "Server connected on " + sockets().getServerConnection().asReadable());
			super.startThis();
		}
		else
		{
			logger().log(ALogType.DEBUG, "Failed to connected server on " + ip + ":" + valPort + ", aborting startup");
		}
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public boolean isCommandLine()
	{
		return commandInput != null;
	}
	
	// TODO add server-wide build mode? probably not
	// public boolean isBuildMode()
	// {
	// return buildMode;
	// }
	
	public boolean isSendingLevels()
	{
		return isSendingLevels;
	}
	
	public void loadGame()
	{
		isSendingLevels = true;
		
		sockets().sender().sendPacketToAllClients(new PacketSendLevels(true));
		
		for (LevelType type : LevelType.values())
		{
			if (type != LevelType.NULL) getLevels().add(Level.fromFile(true, type));
		}
		
		// Sends the levels to everyone else
		for (Level level : getLevels())
		{
			PacketAddLevel levelPack = new PacketAddLevel(level.getType());
			
			sockets().sender().sendPacketToAllClients(levelPack);
			
			byte layer = 0;
			for (List<Entity> tileList : level.getTiles())
			{
				for (Entity tile : tileList)
				{
					sockets().sender().sendPacketToAllClients(new PacketAddTile(layer, level.getType(), tile));
				}
				
				layer++;
			}
			
			for (Hitbox box : level.getHitboxes())
			{
				if (!box.isMicroManaged()) sockets().sender().sendPacketToAllClients(new PacketAddHitbox(level.getType(), box));
			}
			
			for (PointLight light : level.getLights())
			{
				if (light instanceof FlickeringLight)
				{
					FlickeringLight flight = (FlickeringLight) light;
					sockets().sender().sendPacketToAllClients(new PacketAddFlickeringLight(level.getType(), flight.getID(), flight.getPosition(), flight.getRadius(), flight.getRadius2(), flight.getTicksPerFrame()));
				}
				else
				{
					sockets().sender().sendPacketToAllClients(new PacketAddPointLight(level.getType(), light));
				}
			}
			
			for (Event event : level.getScriptedEvents())
			{
				if (event instanceof HitboxTrigger)
				{
					HitboxTrigger e = (HitboxTrigger) event;
					sockets().sender().sendPacketToAllClients(new PacketAddTrigger(level.getType(), e));
					sockets().sender().sendPacketToAllClients(new PacketEditTrigger(level.getType(), e.getID(), e.getTriggerTypes(), e.getInTriggers(), e.getOutTriggers()));
				}
				else if (event instanceof HitboxToggle)
				{
					HitboxToggle e = (HitboxToggle) event;
					sockets().sender().sendPacketToAllClients(new PacketAddHitboxToggle(level.getType(), e));
					sockets().sender().sendPacketToAllClients(new PacketEditHitboxToggle(level.getType(), e.isEnabled(), e.getID(), e.getInTriggers(), e.getOutTriggers()));
				}
				else if (event instanceof TPTrigger)
				{
					TPTrigger e = (TPTrigger) event;
					
					sockets().sender().sendPacketToAllClients(new PacketAddTPTrigger(level.getType(), e));
					sockets().sender().sendPacketToAllClients(new PacketEditTPTrigger(level.getType(), e.getID(), e.getLevelTypeToTPTo(), e.getTPX(), e.getTPY(), e.getInTriggers(), e.getOutTriggers()));
				}
			}
		}
		
		players.add(new PlayerEntity(true, Role.PLAYER1, new Vector2DDouble(80, 50)));
		players.add(new PlayerEntity(true, Role.PLAYER2, new Vector2DDouble(20, 20)));
		
		for (int i = 0; i < players.size(); i++)
		{
			PlayerEntity player = players.get(i);
			player.addToLevel(getLevel(LevelType.MAIN));// TODO make the save files specify this
			player.setPosition(new Vector2DDouble(10 + (i * 18), 20));
		}
		
		// TODO only start ticking once the game has loaded for all clients
		
		setIsInGame(true);
		isSendingLevels = false;
		
		sockets().sender().sendPacketToAllClients(new PacketSendLevels(false));
	}
	
	/**
	 * Reads a packet's data and acts accordingly.
	 * 
	 * @param data the of the packet to parse
	 * @param connection the connection that the packet is being sent from
	 */
	@Override
	public void parsePacket(IncomingPacketWrapper inPack)
	{
		try
		{
			BytePacket packet = inPack.getPacket();
			PacketType type = packet.getType();
			
			// Finds if this packet was send by a connected player and.or the host
			IPConnectedPlayer sender = sockets().getPlayerConnection(inPack.getIPAddress(), inPack.getPort());
			boolean sentByConnected = sender != null;
			boolean sentByHost = (sentByConnected ? (sender.getID() == 0) : false);
			
			if (sentByConnected)
			{
				switch (type)
				{
					default:
					case INVALID:
						logger().log(ALogType.CRITICAL, "INVALID PACKET");
						break;
					case SETROLE:
					{
						if (sentByHost)
						{
							ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
							
							ConnectedPlayer player = sockets().getPlayerConnection(buf.getShort());
							
							// The player who is currently holding that role
							Role newRole = Role.fromOrdinal(buf.get());
							ConnectedPlayer currentPlayerWithRole = sockets().getPlayerConnection(newRole);
							
							if (currentPlayerWithRole != null)
							{
								currentPlayerWithRole.setRole(Role.SPECTATOR);
							}
							
							// As long as their role isn't trying to be set to the same thing, give their previous role to the next spectator
							if (player.getRole() != newRole)
							{
								switch (player.getRole())
								{
									default:
									case SPECTATOR:
										player.setRole(newRole);
										break;
									case PLAYER1:
										// If the player already had a critical role, set it to the next spectator
										ConnectedPlayer newForRole1 = sockets().getPlayerConnection(Role.SPECTATOR);
										if (newForRole1 != null) newForRole1.setRole(Role.PLAYER1);
										break;
									case PLAYER2:
										ConnectedPlayer newForRole2 = sockets().getPlayerConnection(Role.SPECTATOR);
										if (newForRole2 != null) newForRole2.setRole(Role.PLAYER2);
										break;
								}
								
								player.setRole(newRole);
								sockets().updateClientsPlayerList();
							}
						}
						else
						{
							logger().log(ALogType.CRITICAL, "A non-host user was trying to change the roles: " + sender.getConnection().asReadable());
						}
					}
						break;
					case PLAYER_DISCONNECT:
					{
						if (sender != null)
						{
							sockets().removeConnection(sender);
						}
					}
						break;
					case STOP_SERVER:
						if (sentByHost)
						{
							if (ServerGame.instance() != null)
							{
								stopThis();
							}
							else
							{
								logger().log(ALogType.IMPORTANT, "Tried to stop a null instance of a ServerGame: " + sender.getConnection().asReadable());
							}
						}
						else
						{
							logger().log(ALogType.CRITICAL, "A non-host user was trying to stop the server: " + sender.getConnection().asReadable());
						}
						break;
					case START_SERVER:
						if (sentByHost)
						{
							loadGame();
						}
						else
						{
							logger().log(ALogType.CRITICAL, "A non-host user was trying to start the server: " + sender.getConnection().asReadable());
						}
						break;
					case REQUEST_PLAYER_MOVE:
					{
						Role role = sender.getRole();
						if (role != Role.SPECTATOR)
						{
							PlayerEntity player = getPlayer(role);
							if (player != null)
							{
								byte x = packet.getContent()[0];
								byte y = packet.getContent()[1];
								
								player.move(x, y);
							}
						}
					}
						break;
					case ADD_LEVEL_TILE:
					{
						ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
						
						LevelType levelType = LevelType.fromOrdinal(buf.getShort());
						Level level = getLevel(levelType);
						
						if (level != null)
						{
							byte layer = buf.get();
							
							buf.position(buf.position() + ByteUtil.INT_BYTE_COUNT);
							// int id = buf.getInt();
							AssetType asset = AssetType.fromOrdinal(buf.getInt());
							Vector2DDouble pos = new Vector2DDouble(buf.getInt(), buf.getInt());
							
							// Create entity with next available ID. Ignore any sent ID, and it isn't trusted
							Entity tile = new Entity(true, Entity.getIDCounter().getNext(), asset, pos);
							tile.addTileToLevel(level, layer);
							sockets().sender().sendPacketToAllClients(new PacketAddTile(layer, level.getType(), tile));
						}
						else
						{
							logger().log(ALogType.WARNING, "No level with type " + levelType);
						}
					}
						break;
					case PLAYER_INTERACT:
						PlayerEntity pe = getPlayer(sender.getRole());
						
						if (pe != null)
						{
							if (pe.getLevel() != null)
							{
								Level level = pe.getLevel();
								
								level.tryInteract(pe);
							}
							else
							{
								logger().log(ALogType.WARNING, "Player is not in a level");
							}
						}
						else
						{
							logger().log(ALogType.WARNING, "No PlayerEntity with role " + sender.getRole());
						}
						break;
					case REMOVE_LEVEL_OBJECT:
					{
						ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
						
						LevelType levelType = LevelType.fromOrdinal(buf.getShort());
						Level level = getLevel(levelType);
						
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
								case FLICKERING_LIGHT:
									PointLight light = level.getLight(id);
									
									if (light != null)
									{
										light.removeFromLevel();
										removed = true;
									}
									break;
								case HITBOX_TRIGGER:
								case TP_TRIGGER:
									Event event = level.getScriptedEvent(id);
									
									if (event != null)
									{
										event.removeFromLevel();
										removed = true;
									}
									break;
							}
							
							if (removed)
							{
								// TODO use the previous byte content so it doens't need to reformat to bytes
								PacketRemoveLevelObject pack = new PacketRemoveLevelObject(id, levelType, objType);
								sockets().sender().sendPacketToAllClients(pack);
							}
							else
							{
								logger().log(ALogType.WARNING, "Could not remove object id " + id + " of type " + objType);
							}
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
						Level level = getLevel(levelType);
						
						if (level != null)
						{
							buf.position(buf.position() + ByteUtil.INT_BYTE_COUNT);
							// int id = buf.getInt();
							double x = buf.getInt();
							double y = buf.getInt();
							double width = buf.getInt();
							double height = buf.getInt();
							
							// Create entity with next available ID. Ignore any sent ID, and it isn't trusted
							Hitbox box = new Hitbox(Hitbox.getIDCounter().getNext(), x, y, width, height);
							box.addToLevel(level);
							sockets().sender().sendPacketToAllClients(new PacketAddHitbox(levelType, box));
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
						Level level = getLevel(levelType);
						
						if (level != null)
						{
							buf.position(buf.position() + ByteUtil.INT_BYTE_COUNT);
							// int id = buf.getInt();
							Vector2DDouble pos = new Vector2DDouble(buf.getInt(), buf.getInt());
							double radius = buf.getInt();
							
							// Create entity with next available ID. Ignore any sent ID, and it isn't trusted
							PointLight light = new PointLight(true, PointLight.getIDCounter().getNext(), pos, radius);
							light.addToLevel(level);
							sockets().sender().sendPacketToAllClients(new PacketAddPointLight(level.getType(), light));
						}
						else
						{
							logger().log(ALogType.WARNING, "No level with type " + levelType);
						}
					}
					case ADD_LEVEL_FLICKERINGLIGHT:
					{
						ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
						
						LevelType levelType = LevelType.fromOrdinal(buf.getShort());
						Level level = getLevel(levelType);
						
						if (level != null)
						{
							buf.position(buf.position() + ByteUtil.INT_BYTE_COUNT);
							// int id = buf.getInt();
							double x = buf.getInt();
							double y = buf.getInt();
							double radius1 = buf.getInt();
							double radius2 = buf.getInt();
							int tpf = buf.getInt();
							
							FlickeringLight light = new FlickeringLight(false, PointLight.getIDCounter().getNext(), new Vector2DDouble(x, y), radius1, radius2, tpf);
							light.addToLevel(level);
							sockets().sender().sendPacketToAllClients(new PacketAddFlickeringLight(level.getType(), light.getID(), light.getPosition(), light.getRadius(), light.getRadius2(), light.getTicksPerFrame()));
						}
						else
						{
							logger().log(ALogType.WARNING, "No level with type " + levelType);
						}
					}
						break;
					case ADD_EVENT_HITBOX_TRIGGER:
					{
						ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
						
						LevelType levelType = LevelType.fromOrdinal(buf.getShort());
						Level level = getLevel(levelType);
						
						if (level != null)
						{
							int id = Event.getIDCounter().getNext();
							buf.position(buf.position() + ByteUtil.INT_BYTE_COUNT);
							int x = buf.getInt();
							int y = buf.getInt();
							int width = buf.getInt();
							int height = buf.getInt();
							
							HitboxTrigger trig = new HitboxTrigger(true, id, x, y, width, height, null, null, null);
							trig.addToLevel(level);
							sockets().sender().sendPacketToAllClients(new PacketAddTrigger(levelType, id, x, y, width, height));
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
						Level level = getLevel(levelType);
						
						if (level != null)
						{
							int id = buf.getInt();
							
							Event eHitbox = level.getScriptedEvent(id);
							
							if (eHitbox != null)
							{
								if (eHitbox instanceof HitboxTrigger)
								{
									HitboxTrigger hitbox = (HitboxTrigger) eHitbox;
									
									List<TriggerType> triggers = new ArrayList<TriggerType>();
									
									for (byte b : ByteUtil.extractBytes(buf, Byte.MIN_VALUE, Byte.MAX_VALUE))
										triggers.add(TriggerType.fromOrdinal(b));
									
									List<Integer> triggersIn = ByteUtil.extractIntList(buf, Byte.MIN_VALUE, Byte.MAX_VALUE);
									List<Integer> triggersOut = ByteUtil.extractIntList(buf, Byte.MIN_VALUE, Byte.MAX_VALUE);
									
									hitbox.setTriggerTypes(triggers);
									hitbox.setInTriggers(triggersIn);
									hitbox.setOutTriggers(triggersOut);
									
									// TODO optimise by using the same byte data that was given so that it doesn't need to create an entirely new packet from scratch
									sockets().sender().sendPacketToAllClients(new PacketEditTrigger(levelType, id, triggers, triggersIn, triggersOut));
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
					case ADD_EVENT_TP_TRIGGER:
					{
						ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
						
						LevelType levelType = LevelType.fromOrdinal(buf.getShort());
						Level level = getLevel(levelType);
						
						if (level != null)
						{
							int id = Event.getIDCounter().getNext();
							buf.position(buf.position() + ByteUtil.INT_BYTE_COUNT);
							int x = buf.getInt();
							int y = buf.getInt();
							int width = buf.getInt();
							int height = buf.getInt();
							
							TPTrigger trig = new TPTrigger(true, id, x, y, width, height, null, null);
							trig.addToLevel(level);
							sockets().sender().sendPacketToAllClients(new PacketAddTPTrigger(levelType, id, x, y, width, height));
						}
						else
						{
							logger().log(ALogType.WARNING, "No level with type " + levelType);
						}
					}
						break;
					case EDIT_EVENT_TP_TRIGGER:
					{
						ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
						
						LevelType levelType = LevelType.fromOrdinal(buf.getShort());
						Level level = getLevel(levelType);
						
						if (level != null)
						{
							int id = buf.getInt();
							
							Event eHitbox = level.getScriptedEvent(id);
							
							if (eHitbox != null)
							{
								if (eHitbox instanceof TPTrigger)
								{
									TPTrigger hitbox = (TPTrigger) eHitbox;
									
									LevelType toTpTo = LevelType.fromOrdinal(buf.getShort());
									double x = buf.getInt();
									double y = buf.getInt();
									
									hitbox.setLevelToTPTo(toTpTo);
									hitbox.setLocationToTPTo(x, y);
									hitbox.setInTriggers(ByteUtil.extractIntList(buf, Byte.MIN_VALUE, Byte.MAX_VALUE));
									hitbox.setOutTriggers(ByteUtil.extractIntList(buf, Byte.MIN_VALUE, Byte.MAX_VALUE));
									
									// TODO optimise by using the same byte data that was given so that it doesn't need to create an entirely new packet from scratch
									sockets().sender().sendPacketToAllClients(new PacketEditTPTrigger(levelType, id, hitbox.getLevelTypeToTPTo(), hitbox.getTPX(), hitbox.getTPY(), hitbox.getInTriggers(), hitbox.getOutTriggers()));
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
					case ADD_EVENT_HITBOX_TOGGLE:
					{
						ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
						
						LevelType levelType = LevelType.fromOrdinal(buf.getShort());
						Level level = getLevel(levelType);
						
						if (level != null)
						{
							int id = Event.getIDCounter().getNext();
							buf.position(buf.position() + ByteUtil.INT_BYTE_COUNT);
							int x = buf.getInt();
							int y = buf.getInt();
							int width = buf.getInt();
							int height = buf.getInt();
							
							HitboxToggle trig = new HitboxToggle(true, id, x, y, width, height, null, null);
							trig.addToLevel(level);
							sockets().sender().sendPacketToAllClients(new PacketAddHitboxToggle(levelType, id, x, y, width, height));
						}
						else
						{
							logger().log(ALogType.WARNING, "No level with type " + levelType);
						}
					}
						break;
					case EDIT_EVENT_HITBOX_TOGGLE:
					{
						ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
						
						LevelType levelType = LevelType.fromOrdinal(buf.getShort());
						Level level = getLevel(levelType);
						
						if (level != null)
						{
							int id = buf.getInt();
							
							Event eHitbox = level.getScriptedEvent(id);
							
							if (eHitbox != null)
							{
								if (eHitbox instanceof HitboxToggle)
								{
									HitboxToggle hitbox = (HitboxToggle) eHitbox;
									
									boolean enabled = buf.get() == 1;
									List<Integer> triggersIn = ByteUtil.extractIntList(buf, Byte.MIN_VALUE, Byte.MAX_VALUE);
									List<Integer> triggersOut = ByteUtil.extractIntList(buf, Byte.MIN_VALUE, Byte.MAX_VALUE);
									
									hitbox.setEnabled(enabled);
									hitbox.setInTriggers(triggersIn);
									hitbox.setOutTriggers(triggersOut);
									
									// TODO optimise by using the same byte data that was given so that it doesn't need to create an entirely new packet from scratch
									sockets().sender().sendPacketToAllClients(new PacketEditHitboxToggle(levelType, enabled, id, triggersIn, triggersOut));
								}
								else
								{
									logger().log(ALogType.WARNING, "Event found is not an instance of HitboxToggleReceiver");
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
			else if (type == PacketType.REQUEST_CONNECTION)
			{
				IPConnection connection = new IPConnection(inPack.getIPAddress(), inPack.getPort(), null);
				
				int version = ByteUtil.intFromBytes(Arrays.copyOfRange(packet.getContent(), 0, ByteUtil.INT_BYTE_COUNT));
				
				String name = "";
				int passIndex = 0;
				
				for (int i = ByteUtil.INT_BYTE_COUNT; i < packet.getContent().length - 1; i++)
				{
					// The signal. @see PacketLogin
					if (packet.getContent()[i] == Byte.MIN_VALUE && packet.getContent()[i + 1] == Byte.MAX_VALUE)
					{
						name = new String(Arrays.copyOfRange(packet.getContent(), 2, i)).trim();
						passIndex = i + 2;
						break;
					}
				}
				
				int width = 0;
				
				for (int i = passIndex; i < packet.getContent().length - 1; i++)
				{
					// The signal. @see PacketLogin
					if (packet.getContent()[i] == Byte.MIN_VALUE && packet.getContent()[i + 1] == Byte.MAX_VALUE)
					{
						break;
					}
					width += 1;
				}
				
				String pass = new String(Arrays.copyOfRange(packet.getContent(), passIndex, passIndex + width)).trim();
				
				// Checks if there's space for the user on the server
				if (sockets().getConnectedPlayers().size() >= Game.MAX_PLAYERS)
				{
					PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.FULL_SERVER, connection);
					sockets().sender().sendPacket(passPacket);
					return;
				}
				
				// Checks that the client's game version is not above or below this version
				if (version > VersionUtil.SERVER_TEST_VERSION)
				{
					PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.OLD_SERVER, connection);
					sockets().sender().sendPacket(passPacket);
					return;
				}
				
				if (version < VersionUtil.SERVER_TEST_VERSION)
				{
					PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.OLD_CLIENT, connection);
					sockets().sender().sendPacket(passPacket);
					return;
				}
				
				// Checks that there's no duplicated usernames
				if (sockets().getPlayerConnection(name) != null)
				{
					PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.DUPLICATE_USERNAME, connection);
					sockets().sender().sendPacket(passPacket);
					return;
				}
				
				// If there's a password
				if (!password.equals(""))
				{
					// If got the correct password, allow the player to join
					if (pass.equals(password))
					{
						sockets().addConnection(connection, name);
					}
					// If got the wrong password, let the client know
					else
					{
						PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.NEED_PASSWORD, connection);
						sockets().sender().sendPacket(passPacket);
					}
				}
				else // Allow the player to join
				{
					sockets().addConnection(connection, name);
				}
			}
		}
		catch (Exception e)
		{
			// logger().log(ALogType.IMPORTANT, "Exception upon packet parsing", e);
		}
	}
	
	@Override
	public void render()
	{
		// Is never used because this is server side
	}
	
	/**
	 * Save the game state etc.
	 */
	private void safeStop()
	{
		super.stopThis();
		
		// TODO save levels?
		
		if (getLevels() != null) getLevels().clear();
		IDCounter.resetAll();
		
		game = null;
		
		if (commandInput != null) commandInput.stopThis();
		
		stopSocket();
	}
	
	@Override
	public void stopThis()
	{
		stopServer = true;
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		// Does this so that when a packet is sent telling the server to stop, it will not cause a concurrentmodificationexception
		if (stopServer) safeStop();
		
		if (isInGame())
		{
			if (getLevels() != null)
			{
				for (Level level : getLevels())
				{
					level.tick();
				}
			}
		}
	}
}
