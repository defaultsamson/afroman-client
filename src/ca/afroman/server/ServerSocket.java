package ca.afroman.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import ca.afroman.assets.AssetType;
import ca.afroman.client.ClientGame;
import ca.afroman.entity.ServerPlayerEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.level.Level;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.DenyJoinReason;
import ca.afroman.packet.Packet;
import ca.afroman.packet.PacketAddLevelHitbox;
import ca.afroman.packet.PacketAddLevelTile;
import ca.afroman.packet.PacketAssignClientID;
import ca.afroman.packet.PacketDenyJoin;
import ca.afroman.packet.PacketRemoveLevelHitboxID;
import ca.afroman.packet.PacketRemoveLevelTileID;
import ca.afroman.packet.PacketStopServer;
import ca.afroman.packet.PacketType;
import ca.afroman.packet.PacketUpdatePlayerList;
import ca.afroman.player.Role;
import ca.afroman.thread.DynamicThread;

public class ServerSocket extends DynamicThread
{
	public static final boolean TRACE_PACKETS = false;
	public static final String IPv4_LOCALHOST = "127.0.0.1";
	public static final int PORT = 2413;
	public static final int MAX_PLAYERS = 8;
	
	private DatagramSocket socket;
	private List<IPConnectedPlayer> clientConnections;
	private String password;
	
	// private ServerGame game;
	
	/**
	 * A new server instance.
	 * 
	 * @param password the password for the server. Enter "" for no password.
	 */
	public ServerSocket(String password)
	{
		// game = new ServerGame(password);
		
		try
		{
			this.socket = new DatagramSocket(PORT);
		}
		catch (SocketException e)
		{
			e.printStackTrace();
			System.out.println("[SERVER] [CRITICAL] Server already running on this IP and PORT.");
		}
		
		clientConnections = new ArrayList<IPConnectedPlayer>();
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
			socket.receive(packet);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		this.parsePacket(packet.getData(), new IPConnection(packet.getAddress(), packet.getPort()));
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
		IPConnectedPlayer sender = getPlayerByIP(connection.getIPAddress(), connection.getPort());
		boolean sentByConnected = sender != null;
		boolean sentByHost = (sentByConnected ? (sender.getID() == 0) : false);
		
		if (TRACE_PACKETS) System.out.println("[SERVER] [RECIEVE] [" + connection.asReadable() + "] " + type.toString());
		if (sentByConnected)
		{
			switch (type)
			{
				default:
				case INVALID:
					System.out.println("[SERVER] [CRITICAL] INVALID PACKET");
					break;
				case SETROLE:
				{
					if (sentByHost)
					{
						String[] split = Packet.readContent(data).split(",");
						IPConnectedPlayer player = getPlayerByID(Integer.parseInt(split[0]));
						Role newRole = Role.fromOrdinal(Integer.parseInt(split[1]));
						
						// The player who is currently holding that role
						IPConnectedPlayer currentPlayerWithRole = getPlayerByRole(newRole);
						
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
									IPConnectedPlayer newForRole1 = getPlayerByRole(Role.SPECTATOR);
									if (newForRole1 != null) newForRole1.setRole(Role.PLAYER1);
									break;
								case PLAYER2:
									IPConnectedPlayer newForRole2 = getPlayerByRole(Role.SPECTATOR);
									if (newForRole2 != null) newForRole2.setRole(Role.PLAYER2);
									break;
							}
							
							player.setRole(newRole);
							updateClientsPlayerList();
						}
					}
					else
					{
						System.out.print("[SERVER] [CRITICAL] A non-host user was trying to change the roles: " + connection.asReadable());
					}
				}
					break;
				case PLAYER_DISCONNECT:
				{
					if (sender != null)
					{
						this.removeConnection(sender);
					}
				}
					break;
				case STOP_SERVER:
					if (sentByHost)
					{
						ServerGame.instance().stopThread();
					}
					else
					{
						System.out.print("[SERVER] [CRITICAL] A non-host user was trying to stop the server: " + connection.asReadable());
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
						System.out.print("[SERVER] [CRITICAL] A non-host user was trying to start the server: " + connection.asReadable());
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
						AssetType asset = AssetType.fromOrdinal(Integer.parseInt(split[2]));
						
						double x = Double.parseDouble(split[3]);
						double y = Double.parseDouble(split[4]);
						double width = Double.parseDouble(split[5]);
						double height = Double.parseDouble(split[6]);
						
						// If it has custom hitboxes defined
						if (split.length > 7)
						{
							List<Hitbox> tileHitboxes = new ArrayList<Hitbox>();
							
							for (int i = 8; i < split.length; i += 4)
							{
								tileHitboxes.add(new Hitbox(Double.parseDouble(split[i]), Double.parseDouble(split[i + 1]), Double.parseDouble(split[i + 2]), Double.parseDouble(split[i + 3])));
							}
							
							// Create entity with next available ID. Ignore any sent ID, and it isn't trusted
							Entity tile = new Entity(Entity.getNextAvailableID(), level, asset, x, y, width, height, Entity.hitBoxListToArray(tileHitboxes));
							level.getTiles().add(tile); // Adds tile to the server's level
							sendPacketToAllClients(new PacketAddLevelTile(tile)); // Adds the tile to all the clients' levels
						}
						else
						{
							// Create entity with next available ID. Ignore any sent ID, and it isn't trusted
							Entity tile = new Entity(Entity.getNextAvailableID(), level, asset, x, y, width, height);
							level.getTiles().add(tile);
							sendPacketToAllClients(new PacketAddLevelTile(tile));
						}
					}
					else
					{
						System.out.println("[SERVER] No level with type " + levelType);
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
						double x = Double.parseDouble(split[1]);
						double y = Double.parseDouble(split[2]);
						
						Entity tile = level.getTile(x, y);
						
						if (tile != null)
						{
							PacketRemoveLevelTileID pack = new PacketRemoveLevelTileID(tile.getLevel().getType(), tile.getID());
							
							sendPacketToAllClients(pack);
						}
						
						level.getTiles().remove(tile);
					}
					else
					{
						System.out.println("[SERVER] No level with type " + levelType);
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
						sendPacketToAllClients(new PacketAddLevelHitbox(levelType, box));
					}
					else
					{
						System.out.println("[SERVER] No level with type " + levelType);
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
							
							sendPacketToAllClients(pack);
						}
						
						level.getHitboxes().remove(box);
					}
					else
					{
						System.out.println("[SERVER] No level with type " + levelType);
					}
				}
					break;
				case REQUEST_PLAYER_MOVE:
				{
					String[] split = Packet.readContent(data).split(",");
					Role role = Role.fromOrdinal(Integer.parseInt(split[0]));
					
					if (role == getPlayerByIP(connection.getIPAddress(), connection.getPort()).getRole())
					{
						ServerPlayerEntity player = ServerGame.instance().getPlayer(role);
						if (player != null)
						{
							player.move(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
						}
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
			if (this.clientConnections.size() >= MAX_PLAYERS)
			{
				PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.FULL_SERVER);
				this.sendPacket(passPacket, connection);
				return;
			}
			
			int sentGameVersion = Integer.parseInt(sent[2]);
			
			// Checks that the client's game version is not above or below this version
			if (sentGameVersion > ClientGame.VERSION)
			{
				PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.OLD_SERVER);
				this.sendPacket(passPacket, connection);
				return;
			}
			
			if (sentGameVersion < ClientGame.VERSION)
			{
				PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.OLD_CLIENT);
				this.sendPacket(passPacket, connection);
				return;
			}
			
			// Checks that there's no duplicated usernames
			if (this.getPlayerByUsername(sentUsername) != null)
			{
				PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.DUPLICATE_USERNAME);
				this.sendPacket(passPacket, connection);
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
					this.addConnection(connection, sentUsername);
				}
				// If got the wrong person, let the client know
				else
				{
					PacketDenyJoin passPacket = new PacketDenyJoin(DenyJoinReason.NEED_PASSWORD);
					this.sendPacket(passPacket, connection);
				}
			}
			else // Allow the player to join
			{
				this.addConnection(connection, sentUsername);
			}
		}
	}
	
	/**
	 * Safely closes the server.
	 */
	@Override
	public void onStop()
	{
		// Tell all clients that the server stopped
		sendPacketToAllClients(new PacketStopServer());
		
		socket.close();
		this.clientConnections.clear();
	}
	
	/**
	 * Gets a player by their IP.
	 * 
	 * @param ip the player's IP address
	 * @param port the client's port for the IP address
	 * @return the player.
	 */
	public IPConnectedPlayer getPlayerByIP(InetAddress ip, int port)
	{
		for (IPConnectedPlayer player : clientConnections)
		{
			// If the IP and port equal those that were specified, return the player
			if (player.getConnection().getIPAddress().getHostAddress().equals(ip.getHostAddress()) && port == player.getConnection().getPort()) return player;
		}
		return null;
	}
	
	/**
	 * Gets the first player with the given role.
	 * 
	 * @param role the role
	 * @return the player.
	 */
	public IPConnectedPlayer getPlayerByRole(Role role)
	{
		for (IPConnectedPlayer player : clientConnections)
		{
			if (player.getRole() == role) return player;
		}
		return null;
	}
	
	/**
	 * Gets a player by their username.
	 * 
	 * @param username the username
	 * @return the player.
	 */
	public IPConnectedPlayer getPlayerByUsername(String username)
	{
		for (IPConnectedPlayer player : clientConnections)
		{
			if (player.getUsername().equals(username)) return player;
		}
		return null;
	}
	
	/**
	 * Gets a player by their ID number.
	 * 
	 * @param id the ID number
	 * @return the player.
	 */
	public IPConnectedPlayer getPlayerByID(int id)
	{
		for (IPConnectedPlayer player : clientConnections)
		{
			if (player.getID() == id) return player;
		}
		return null;
	}
	
	/**
	 * @return all the client connections.
	 */
	public List<IPConnectedPlayer> clientConnections()
	{
		return clientConnections;
	}
	
	/**
	 * Removes a player's IPConnectedPlayer for a leaving connection. Makes the player disconnect from the server.
	 * 
	 * @param connection the connection to remove.
	 */
	public void removeConnection(IPConnectedPlayer connection)
	{
		this.clientConnections.remove(connection);
		updateClientsPlayerList();
	}
	
	/**
	 * Sets up a IPConnectedPlayer for a new connection. Makes the player join the server.
	 * 
	 * @param connection the connection to set up for
	 * @param username the desired username
	 */
	public void addConnection(IPConnection connection, String username)
	{
		// Gives player a default role based on what critical roles are still required
		Role role = (this.getPlayerByRole(Role.PLAYER1) == null ? Role.PLAYER1 : (this.getPlayerByRole(Role.PLAYER2) == null ? Role.PLAYER2 : Role.SPECTATOR));
		
		IPConnectedPlayer newConnection = new IPConnectedPlayer(connection.getIPAddress(), connection.getPort(), role, username);
		clientConnections.add(newConnection);
		
		// Tells the newly added connection their ID
		sendPacket(new PacketAssignClientID(newConnection.getID()), newConnection.getConnection());
		
		updateClientsPlayerList();
	}
	
	/**
	 * Updates the player list for all the connected clients.
	 */
	public void updateClientsPlayerList()
	{
		// Sends all the connections the updated list
		PacketUpdatePlayerList updateList = new PacketUpdatePlayerList(clientConnections);
		for (IPConnectedPlayer con : clientConnections)
		{
			sendPacket(updateList, con.getConnection());
		}
	}
	
	/**
	 * Sends data to a Client.
	 * 
	 * @param packet the packet to send
	 * @param connection the Connection of the Client to send to
	 */
	public void sendPacket(Packet packet, IPConnection connection)
	{
		sendData(packet.getData(), connection.getIPAddress(), connection.getPort());
	}
	
	/**
	 * Sends data to a Client.
	 * 
	 * @param data the data to send
	 * @param connection the Connection of the Client to send to
	 * 
	 * @deprecated Still works to send raw data, but sendPacket() is preferred.
	 */
	@Deprecated
	public void sendData(byte[] data, IPConnection connection)
	{
		sendData(data, connection.getIPAddress(), connection.getPort());
	}
	
	/**
	 * Sends data to a Client.
	 * 
	 * @param data the data to send
	 * @param ipAddress the Client's IP address
	 * @param port the Client's port
	 * 
	 * @deprecated Still works to send raw data, but sendPacket() is preferred.
	 */
	@Deprecated
	public void sendData(byte[] data, InetAddress ipAddress, int port)
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		
		if (TRACE_PACKETS) System.out.println("[SERVER] [SEND] [" + ipAddress.getHostAddress() + ":" + port + "] " + new String(data));
		
		try
		{
			socket.send(packet);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a packet to all the connected clients.
	 * 
	 * @param packet the packet to send
	 */
	public void sendPacketToAllClients(Packet packet)
	{
		for (IPConnectedPlayer connection : clientConnections)
		{
			sendPacket(packet, connection.getConnection());
		}
	}
	
	/**
	 * Sends data to all the connections.
	 * 
	 * @param data the data to send
	 * 
	 * @deprecated Still works to send raw data, but sendPacketToAllClients() is preferred.
	 */
	@Deprecated
	public void sendDataToAllClients(byte[] data)
	{
		for (IPConnectedPlayer connection : clientConnections)
		{
			sendData(data, connection.getConnection());
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
}
