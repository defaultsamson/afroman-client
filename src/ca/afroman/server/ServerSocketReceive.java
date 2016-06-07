package ca.afroman.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.client.ClientGame;
import ca.afroman.client.Role;
import ca.afroman.entity.ServerPlayerEntity;
import ca.afroman.entity.TriggerType;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.events.HitboxTrigger;
import ca.afroman.events.IEventCounter;
import ca.afroman.gfx.PointLight;
import ca.afroman.level.Level;
import ca.afroman.level.LevelType;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.Packet;
import ca.afroman.packet.PacketAddLevelHitbox;
import ca.afroman.packet.PacketAddLevelLight;
import ca.afroman.packet.PacketAddLevelTile;
import ca.afroman.packet.PacketConfirmReceived;
import ca.afroman.packet.PacketDenyJoin;
import ca.afroman.packet.PacketRemoveLevelHitboxID;
import ca.afroman.packet.PacketRemoveLevelLightID;
import ca.afroman.packet.PacketRemoveLevelTileID;
import ca.afroman.packet.PacketType;
import ca.afroman.thread.DynamicThread;

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
		byte[] buffer = new byte[1024];
		
		// Loads up the buffer with incoming data
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		try
		{
			manager.socket().receive(packet);
			
			this.parsePacket(packet.getData(), new IPConnection(packet.getAddress(), packet.getPort()));
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
	public void parsePacket(byte[] data, IPConnection connection)
	{
		PacketType type = Packet.readType(data);
		// String message = Packet.readContent(data);
		
		// Finds if this packet was send by a connected player and.or the host
		IPConnectedPlayer sender = manager.getPlayerByConnection(connection);
		boolean sentByConnected = sender != null;
		boolean sentByHost = (sentByConnected ? (sender.getID() == 0) : false);
		
		if (ALogger.tracePackets) logger().log(ALogType.DEBUG, "[" + connection.asReadable() + "] " + type.toString());
		if (sentByConnected)
		{
			int packetID = Packet.readID(data);
			
			if (packetID != -1)
			{
				List<Integer> receivedBySender = receivedPackets.get(sender.getConnection());
				
				// Gets the packet ID's received from the sender
				for (Integer packID : receivedBySender)
				{
					if (packID == packetID)
					{
						// If the packet with this ID has already been received, tell the client to stop sending it, and don't parse it
						manager.sender().sendPacket(new PacketConfirmReceived(packetID), sender.getConnection());
						return;
					}
				}
				
				manager.sender().sendPacket(new PacketConfirmReceived(packetID), sender.getConnection());
				receivedBySender.add(packetID);
			}
			
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
						String[] split = Packet.readContent(data).split(",");
						IPConnectedPlayer player = manager.getPlayerByID(Integer.parseInt(split[0]));
						Role newRole = Role.fromOrdinal(Integer.parseInt(split[1]));
						
						// The player who is currently holding that role
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
						logger().log(ALogType.CRITICAL, "A non-host user was trying to change the roles: " + connection.asReadable());
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
							logger().log(ALogType.IMPORTANT, "Tries to stop a null instance of a ServerGame: " + connection.asReadable());
						}
					}
					else
					{
						logger().log(ALogType.CRITICAL, "A non-host user was trying to stop the server: " + connection.asReadable());
					}
					break;
				case START_SERVER:
				case SEND_LEVELS:
					if (sentByHost)
					{
						ServerGame.instance().loadGame();
					}
					else
					{
						logger().log(ALogType.CRITICAL, "A non-host user was trying to start the server: " + connection.asReadable());
					}
					break;
				case ADD_LEVEL_TILE:
				{
					String[] split = Packet.readContent(data).split(",");
					// int id = Integer.parseInt(split[0]); // Unused because it is assigned by the server
					LevelType levelType = LevelType.fromOrdinal(Integer.parseInt(split[1]));
					Level level = ServerGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						int layer = Integer.parseInt(split[2]);
						
						AssetType asset = AssetType.fromOrdinal(Integer.parseInt(split[3]));
						
						double x = Double.parseDouble(split[4]);
						double y = Double.parseDouble(split[5]);
						
						// If it has custom hitboxes defined
						if (split.length > 6)
						{
							List<Hitbox> tileHitboxes = new ArrayList<Hitbox>();
							
							for (int i = 6; i < split.length; i += 4)
							{
								tileHitboxes.add(new Hitbox(Double.parseDouble(split[i]), Double.parseDouble(split[i + 1]), Double.parseDouble(split[i + 2]), Double.parseDouble(split[i + 3])));
							}
							
							// Create entity with next available ID. Ignore any sent ID, and it isn't trusted
							Entity tile = new Entity(Entity.getNextAvailableID(), asset, x, y, Entity.hitBoxListToArray(tileHitboxes));
							tile.addTileToLevel(level, layer); // Adds tile to the server's level
							manager.sender().sendPacketToAllClients(new PacketAddLevelTile(layer, level.getType(), tile)); // Adds the tile to all the clients' levels
						}
						else
						{
							// Create entity with next available ID. Ignore any sent ID, and it isn't trusted
							Entity tile = new Entity(Entity.getNextAvailableID(), asset, x, y);
							tile.addTileToLevel(level, layer);
							manager.sender().sendPacketToAllClients(new PacketAddLevelTile(layer, level.getType(), tile));
						}
					}
					else
					{
						logger().log(ALogType.WARNING, "No level with type " + levelType);
					}
				}
					break;
				case REMOVE_LEVEL_TILE:
				{
					String[] split = Packet.readContent(data).split(",");
					LevelType levelType = LevelType.fromOrdinal(Integer.parseInt(split[0]));
					Level level = ServerGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						int layer = Integer.parseInt(split[1]);
						double x = Double.parseDouble(split[2]);
						double y = Double.parseDouble(split[3]);
						
						Entity tile = level.getTile(layer, x, y);
						
						if (tile != null)
						{
							PacketRemoveLevelTileID pack = new PacketRemoveLevelTileID(tile.getLevel().getType(), tile.getID());
							
							manager.sender().sendPacketToAllClients(pack);
							
							tile.removeTileFromLevel();
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
					String[] split = Packet.readContent(data).split(",");
					// int id = Integer.parseInt(split[0]); // Unused because it is assigned by the server
					LevelType levelType = LevelType.fromOrdinal(Integer.parseInt(split[0]));
					Level level = ServerGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						double x = Double.parseDouble(split[2]);
						double y = Double.parseDouble(split[3]);
						double width = Double.parseDouble(split[4]);
						double height = Double.parseDouble(split[5]);
						
						// Create entity with next available ID. Ignore any sent ID, and it isn't trusted
						Hitbox box = new Hitbox(Hitbox.getNextAvailableID(), x, y, width, height);
						level.getHitboxes().add(box);
						manager.sender().sendPacketToAllClients(new PacketAddLevelHitbox(levelType, box));
					}
					else
					{
						logger().log(ALogType.WARNING, "No level with type " + levelType);
					}
				}
					break;
				case REMOVE_LEVEL_HITBOX:
				{
					String[] split = Packet.readContent(data).split(",");
					LevelType levelType = LevelType.fromOrdinal(Integer.parseInt(split[0]));
					Level level = ServerGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						double x = Double.parseDouble(split[1]);
						double y = Double.parseDouble(split[2]);
						
						Hitbox box = level.getHitbox(x, y);
						
						if (box != null)
						{
							PacketRemoveLevelHitboxID pack = new PacketRemoveLevelHitboxID(level.getType(), box.getID());
							
							manager.sender().sendPacketToAllClients(pack);
							
							level.getHitboxes().remove(box);
						}
					}
					else
					{
						logger().log(ALogType.WARNING, "No level with type " + levelType);
					}
				}
					break;
				case ADD_LEVEL_POINTLIGHT: // TODO
				{
					String[] split = Packet.readContent(data).split(",");
					// int id = Integer.parseInt(split[0]); // Unused because it is assigned by the server
					LevelType levelType = LevelType.fromOrdinal(Integer.parseInt(split[0]));
					Level level = ServerGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						double x = Double.parseDouble(split[2]);
						double y = Double.parseDouble(split[3]);
						double radius = Double.parseDouble(split[4]);
						
						// Create entity with next available ID. Ignore any sent ID, and it isn't trusted
						PointLight light = new PointLight(Entity.getNextAvailableID(), x, y, radius);
						light.addToLevel(level);
						manager.sender().sendPacketToAllClients(new PacketAddLevelLight(level.getType(), light));
					}
					else
					{
						logger().log(ALogType.WARNING, "No level with type " + levelType);
					}
				}
					break;
				case REMOVE_LEVEL_POINTLIGHT: // TODO
				{
					String[] split = Packet.readContent(data).split(",");
					LevelType levelType = LevelType.fromOrdinal(Integer.parseInt(split[0]));
					Level level = ServerGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						double x = Double.parseDouble(split[1]);
						double y = Double.parseDouble(split[2]);
						
						PointLight light = level.getLight(x, y);
						
						if (light != null)
						{
							PacketRemoveLevelLightID pack = new PacketRemoveLevelLightID(level.getType(), light.getID());
							
							manager.sender().sendPacketToAllClients(pack);
							
							light.removeFromLevel();
						}
					}
					else
					{
						logger().log(ALogType.WARNING, "No level with type " + levelType);
					}
				}
					break;
				case REQUEST_PLAYER_MOVE:
				{
					String[] split = Packet.readContent(data).split(",");
					Role role = Role.fromOrdinal(Integer.parseInt(split[0]));
					
					if (role == sender.getRole())
					{
						ServerPlayerEntity player = ServerGame.instance().getPlayer(role);
						if (player != null)
						{
							player.move(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
						}
					}
				}
					break;
				case CONFIRM_RECEIVED:
				{
					int sentID = Integer.parseInt(Packet.readContent(data));
					
					manager.sender().removePacketFromQueue(connection, sentID);
				}
					break;
				case ADD_EVENT_HITBOX_TRIGGER:
				{
					String[] split = Packet.readContent(data).split(",");
					LevelType levelType = LevelType.fromOrdinal(Integer.parseInt(split[0]));
					Level level = ServerGame.instance().getLevelByType(levelType);
					
					if (level != null)
					{
						logger().log(ALogType.WARNING, "Hell yeah I received that shit");
						int id = IEventCounter.getNextAvailableID();
						
						double x = Double.parseDouble(split[2]);
						double y = Double.parseDouble(split[3]);
						double width = Double.parseDouble(split[4]);
						double height = Double.parseDouble(split[5]);
						
						List<TriggerType> triggerTypes = new ArrayList<TriggerType>();
						List<Integer> triggers = new ArrayList<Integer>();
						List<Integer> chainedTriggers = new ArrayList<Integer>();
						
						int mode = 0;
						
						
						// If there's further arguments
						for (int i = 6; i < split.length; i++)
						{
							if (split[i].equals("a")) // triggerTypesAsSendable
							{
								mode = 1;
							}
							else if (split[i].equals("b")) // triggersAsSendable
							{
								mode = 2;
							}
							else if (split[i].equals("c")) // chainTriggersAsSendable
							{
								mode = 3;
							}
							else if (mode != 0)
							{
								switch (mode)
								{
									case 1:
										triggerTypes.add(TriggerType.fromOrdinal(Integer.parseInt(split[i])));
										break;
									case 2:
										triggers.add(Integer.parseInt(split[i]));
										break;
									case 3:
										chainedTriggers.add(Integer.parseInt(split[i]));
										break;
								}
							}
							else
							{
								logger().log(ALogType.WARNING, "No mode selected during ADD_EVENT_HITBOX_TRIGGER");
							}
						}
						HitboxTrigger hitbox = new HitboxTrigger(id, x, y, width, height, triggerTypes, triggers, chainedTriggers);
						
						hitbox.addToLevel(level);
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
			String[] sent = Packet.readContent(data).trim().split(",");
			
			String sentUsername = sent[0];
			
			// Checks if there's space for the user on the server
			if (manager.clientConnections().size() >= ServerSocketManager.MAX_PLAYERS)
			{
				PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.FULL_SERVER);
				manager.sender().sendPacket(passPacket, connection);
				return;
			}
			
			int sentGameVersion = Integer.parseInt(sent[2]);
			
			// Checks that the client's game version is not above or below this version
			if (sentGameVersion > ClientGame.VERSION)
			{
				PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.OLD_SERVER);
				manager.sender().sendPacket(passPacket, connection);
				return;
			}
			
			if (sentGameVersion < ClientGame.VERSION)
			{
				PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.OLD_CLIENT);
				manager.sender().sendPacket(passPacket, connection);
				return;
			}
			
			// Checks that there's no duplicated usernames
			if (manager.getPlayerByUsername(sentUsername) != null)
			{
				PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.DUPLICATE_USERNAME);
				manager.sender().sendPacket(passPacket, connection);
				return;
			}
			
			String sentPassword = "";
			if (sent.length > 1) sentPassword = sent[1];
			
			// If there's a password
			if (!password.equals(""))
			{
				// If got the correct password, allow the player to join
				if (sentPassword.equals(password))
				{
					manager.addConnection(connection, sentUsername);
				}
				// If got the wrong person, let the client know
				else
				{
					PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.NEED_PASSWORD);
					manager.sender().sendPacket(passPacket, connection);
				}
			}
			else // Allow the player to join
			{
				manager.addConnection(connection, sentUsername);
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
