package ca.afroman.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.client.ClientGame;
import ca.afroman.client.Role;
import ca.afroman.entity.ServerPlayerEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.events.HitboxTrigger;
import ca.afroman.events.IEvent;
import ca.afroman.events.TriggerType;
import ca.afroman.gfx.PointLight;
import ca.afroman.level.Level;
import ca.afroman.level.LevelObjectType;
import ca.afroman.level.LevelType;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketAddHitbox;
import ca.afroman.packet.PacketAddPointLight;
import ca.afroman.packet.PacketAddTile;
import ca.afroman.packet.PacketAddTrigger;
import ca.afroman.packet.PacketConfirmReceive;
import ca.afroman.packet.PacketDenyJoin;
import ca.afroman.packet.PacketEditTrigger;
import ca.afroman.packet.PacketRemoveLevelObject;
import ca.afroman.packet.PacketType;
import ca.afroman.thread.DynamicThread;
import ca.afroman.util.ByteUtil;

public class ServerSocketReceive extends DynamicThread
{
	private String password;
	private HashMap<IPConnection, List<Integer>> receivedPackets; // The ID's of all the packets that have been received
	
	private ServerSocketManager manager;
	
	/**
	 * A new server instance.
	 * 
	 * @param password the password for the server. Enter "" for no password.
	 */
	public ServerSocketReceive(ServerSocketManager manager, String password)
	{
		super(ServerGame.instance().getThreadGroup(), "Receive");
		
		this.manager = manager;
		
		receivedPackets = new HashMap<IPConnection, List<Integer>>();
		
		this.password = password;
	}
	
	@Override
	public void onRun()
	{
		byte[] buffer = new byte[ClientGame.RECEIVE_PACKET_BUFFER_LIMIT];
		
		// Loads up the buffer with incoming data
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		try
		{
			manager.socket().receive(packet);
			
			BytePacket pack = new BytePacket(packet.getData(), new IPConnection(packet.getAddress(), packet.getPort()));
			if (ALogger.tracePackets) logger().log(ALogType.DEBUG, "[" + pack.getConnections().get(0).asReadable() + "] " + pack.getType());
			this.parsePacket(pack);
		}
		catch (IOException e)
		{
			logger().log(ALogType.CRITICAL, "I/O error while receiving", e);
		}
	}
	
	/**
	 * Reads a packet's data and acts accordingly.
	 * 
	 * @param data the of the packet to parse
	 * @param connection the connection that the packet is being sent from
	 */
	public void parsePacket(BytePacket packet)
	{
		PacketType type = packet.getType();
		
		// Finds if this packet was send by a connected player and.or the host
		IPConnectedPlayer sender = manager.getPlayerByConnection(packet.getConnections().get(0));
		boolean sentByConnected = sender != null;
		boolean sentByHost = (sentByConnected ? (sender.getID() == 0) : false);
		
		if (sentByConnected)
		{
			int packetID = packet.getID();
			
			if (packetID != -1)
			{
				List<Integer> receivedBySender = receivedPackets.get(sender.getConnection());
				
				// Gets the packet ID's received from the sender
				for (Integer packID : receivedBySender)
				{
					if (packID == packetID)
					{
						// If the packet with this ID has already been received, tell the client to stop sending it, and don't parse it
						manager.sender().sendPacket(new PacketConfirmReceive(packetID, sender.getConnection()));
						return;
					}
				}
				
				manager.sender().sendPacket(new PacketConfirmReceive(packetID, sender.getConnection()));
				receivedBySender.add(packetID);
			}
			
			switch (type)
			{
				default:
				case INVALID:
					logger().log(ALogType.CRITICAL, "INVALID PACKET");
					break;
				case CONFIRM_RECEIVED:
				{
					int sentID = ByteUtil.intFromBytes(new byte[] { packet.getContent()[0], packet.getContent()[1], packet.getContent()[2], packet.getContent()[3] });
					
					manager.sender().removePacket(sender.getConnection(), sentID);
				}
					break;
				case SETROLE:
				{
					if (sentByHost)
					{
						ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
						
						IPConnectedPlayer player = manager.getPlayerByID(buf.getShort());
						
						// The player who is currently holding that role
						Role newRole = Role.fromOrdinal(buf.get());
						IPConnectedPlayer currentPlayerWithRole = manager.getPlayerByRole(newRole);
						
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
									IPConnectedPlayer newForRole1 = manager.getPlayerByRole(Role.SPECTATOR);
									if (newForRole1 != null) newForRole1.setRole(Role.PLAYER1);
									break;
								case PLAYER2:
									IPConnectedPlayer newForRole2 = manager.getPlayerByRole(Role.SPECTATOR);
									if (newForRole2 != null) newForRole2.setRole(Role.PLAYER2);
									break;
							}
							
							player.setRole(newRole);
							manager.updateClientsPlayerList();
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
						manager.removeConnection(sender);
					}
				}
					break;
				case STOP_SERVER:
					if (sentByHost)
					{
						if (ServerGame.instance() != null)
						{
							ServerGame.instance().stopThis();
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
						ServerGame.instance().loadGame();
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
						ServerPlayerEntity player = ServerGame.instance().getPlayer(role);
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
					Level level = ServerGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						byte layer = buf.get();
						
						buf.position(buf.position() + ByteUtil.INT_BYTE_COUNT);
						// int id = buf.getInt();
						AssetType asset = AssetType.fromOrdinal(buf.getInt());
						double x = buf.getInt();
						double y = buf.getInt();
						
						// Create entity with next available ID. Ignore any sent ID, and it isn't trusted
						Entity tile = new Entity(true, Entity.getIDCounter().getNext(), asset, x, y);
						tile.addTileToLevel(level, layer);
						manager.sender().sendPacketToAllClients(new PacketAddTile(layer, level.getType(), tile));
					}
					else
					{
						logger().log(ALogType.WARNING, "No level with type " + levelType);
					}
				}
					break;
				case REMOVE_LEVEL_OBJECT:
				{
					ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
					
					LevelType levelType = LevelType.fromOrdinal(buf.getShort());
					Level level = ServerGame.instance().getLevelByType(levelType);
					
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
						
						if (removed)
						{
							// TODO use the previous byte content so it doens't need to reformat to bytes
							PacketRemoveLevelObject pack = new PacketRemoveLevelObject(id, levelType, objType);
							manager.sender().sendPacketToAllClients(pack);
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
					Level level = ServerGame.instance().getLevelByType(levelType);
					
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
						manager.sender().sendPacketToAllClients(new PacketAddHitbox(levelType, box));
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
					Level level = ServerGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						buf.position(buf.position() + ByteUtil.INT_BYTE_COUNT);
						// int id = buf.getInt();
						double x = buf.getInt();
						double y = buf.getInt();
						double radius = buf.getInt();
						
						// Create entity with next available ID. Ignore any sent ID, and it isn't trusted
						PointLight light = new PointLight(true, PointLight.getIDCounter().getNext(), x, y, radius);
						light.addToLevel(level);
						manager.sender().sendPacketToAllClients(new PacketAddPointLight(level.getType(), light));
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
					Level level = ServerGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						int id = HitboxTrigger.getIDCounter().getNext();
						buf.position(buf.position() + ByteUtil.INT_BYTE_COUNT);
						int x = buf.getInt();
						int y = buf.getInt();
						int width = buf.getInt();
						int height = buf.getInt();
						
						HitboxTrigger trig = new HitboxTrigger(true, id, x, y, width, height, null, null, null);
						trig.addToLevel(level);
						manager.sender().sendPacketToAllClients(new PacketAddTrigger(levelType, id, x, y, width, height));
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
					Level level = ServerGame.instance().getLevelByType(levelType);
					
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
								
								List<Integer> triggersIn = ByteUtil.extractIntList(buf, Byte.MIN_VALUE, Byte.MAX_VALUE);
								List<Integer> triggersOut = ByteUtil.extractIntList(buf, Byte.MIN_VALUE, Byte.MAX_VALUE);
								
								hitbox.setTriggerTypes(triggers);
								hitbox.setInTriggers(triggersIn);
								hitbox.setOutTriggers(triggersOut);
								
								// TODO optimise by using the same byte data that was given so that it doesn't need to create an entirely new packet from scratch
								manager.sender().sendPacketToAllClients(new PacketEditTrigger(levelType, id, triggers, triggersIn, triggersOut));
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
		else if (type == PacketType.REQUEST_CONNECTION)
		{
			IPConnection connection = packet.getConnections().get(0);
			
			short version = ByteUtil.shortFromBytes(Arrays.copyOfRange(packet.getContent(), 0, 2));
			
			String name = "";
			int passIndex = 0;
			
			for (int i = 2; i < packet.getContent().length - 1; i++)
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
			if (manager.clientConnections().size() >= ServerSocketManager.MAX_PLAYERS)
			{
				PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.FULL_SERVER, connection);
				manager.sender().sendPacket(passPacket);
				return;
			}
			
			// Checks that the client's game version is not above or below this version
			if (version > ClientGame.VERSION)
			{
				PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.OLD_SERVER, connection);
				manager.sender().sendPacket(passPacket);
				return;
			}
			
			if (version < ClientGame.VERSION)
			{
				PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.OLD_CLIENT, connection);
				manager.sender().sendPacket(passPacket);
				return;
			}
			
			// Checks that there's no duplicated usernames
			if (manager.getPlayerByUsername(name) != null)
			{
				PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.DUPLICATE_USERNAME, connection);
				manager.sender().sendPacket(passPacket);
				return;
			}
			
			// If there's a password
			if (!password.equals(""))
			{
				// If got the correct password, allow the player to join
				if (pass.equals(password))
				{
					manager.addConnection(connection, name);
				}
				// If got the wrong person, let the client know
				else
				{
					PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.NEED_PASSWORD, connection);
					manager.sender().sendPacket(passPacket);
				}
			}
			else // Allow the player to join
			{
				manager.addConnection(connection, name);
			}
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
	
	public void addConnection(IPConnection connection)
	{
		HashMap<IPConnection, List<Integer>> packs = receivedPackets;
		
		synchronized (packs)
		{
			packs.put(connection, new ArrayList<Integer>());
		}
	}
	
	public void removeConnection(IPConnection connection)
	{
		HashMap<IPConnection, List<Integer>> packs = receivedPackets;
		
		synchronized (packs)
		{
			packs.remove(connection);
		}
	}
	
	/**
	 * Safely closes the server.
	 */
	@Override
	public void onStop()
	{
		receivedPackets.clear();
	}
}
