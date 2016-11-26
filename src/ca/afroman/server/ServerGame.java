package ca.afroman.server;

import java.nio.ByteBuffer;
import java.util.Arrays;

import ca.afroman.entity.PlayerEntity;
import ca.afroman.game.Game;
import ca.afroman.game.Role;
import ca.afroman.game.SocketManager;
import ca.afroman.level.api.Level;
import ca.afroman.level.api.LevelType;
import ca.afroman.log.ALogType;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.network.IncomingPacketWrapper;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketDenyJoin;
import ca.afroman.packet.PacketLoadLevels;
import ca.afroman.packet.PacketType;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ByteUtil;
import ca.afroman.util.CommandUtil;
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
	
	private String password;
	private boolean stopServer = false;
	private boolean isCommandLine;
	
	private boolean waitingForPlayersToLoad = false;;
	
	public ServerGame(boolean commandLine, String ip, String password, String port)
	{
		super(true, newDefaultThreadGroupInstance(), "Game", 60);
		
		if (game == null) game = this;
		
		isCommandLine = commandLine;
		
		if (commandLine && ConsoleListener.instance() == null)
		{
			// Starts the console listener
			new ConsoleListener(isServerSide()).startThis();
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
		return isCommandLine;
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
			
			if (sentByConnected && type != null)
			{
				switch (type)
				{
					default:
					case INVALID:
						logger().log(ALogType.CRITICAL, "INVALID PACKET");
						break;
					case LOAD_LEVELS:
					{
						boolean sendingLevels = (packet.getContent()[0] == 1);
						
						if (sendingLevels)
						{
							
						}
						else // Finished loading levels
						{
							sender.setIsLoadingLevels(false);
							
							waitingForPlayersToLoad = false;
							for (ConnectedPlayer con : sockets().getConnectedPlayers())
							{
								if (con.isLoadingLevels())
								{
									waitingForPlayersToLoad = true;
									break;
								}
							}
							
							if (!waitingForPlayersToLoad)
							{
								sockets().sender().sendPacketToAllClients(new PacketLoadLevels(false));
							}
						}
					}
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
					case START_SERVER:
						if (sentByHost)
						{
							byte x = packet.getContent()[0];
							
							// 1 if true, 0 if false
							setIsInGame(x == 1);
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
					case COMMAND:
					{
						ByteBuffer buf = ByteBuffer.wrap(packet.getContent());
						
						ConsoleCommand command = ConsoleCommand.fromOrdinal(buf.getInt());
						String[] params = new String[buf.get()];
						
						for (int i = 0; i < params.length; i++)
						{
							params[i] = new String(ByteUtil.extractBytes(buf, Byte.MAX_VALUE, Byte.MIN_VALUE, Byte.MAX_VALUE, Byte.MIN_VALUE));
						}
						
						CommandUtil.issueCommand(command, params, sentByHost);
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
			logger().log(ALogType.IMPORTANT, "Exception upon packet parsing", e);
		}
	}
	
	@Override
	public void render()
	{
		// Is never used because this is server side
	}
	
	@Override
	public void setIsInGame(boolean isInGame)
	{
		setIsInGame(isInGame, false);
	}
	
	private void setIsInGame(boolean isInGame, boolean isSafeStop)
	{
		super.setIsInGame(isInGame);
		
		if (isInGame)
		{
			sockets().sender().sendPacketToAllClients(new PacketLoadLevels(true));
			
			loadLevels();
			
			players.clear();
			players.add(new PlayerEntity(true, Role.PLAYER1, new Vector2DDouble(80, 50)));
			players.add(new PlayerEntity(true, Role.PLAYER2, new Vector2DDouble(20, 20)));
			
			for (int i = 0; i < players.size(); i++)
			{
				PlayerEntity player = players.get(i);
				player.addToLevel(getLevel(LevelType.MAIN));// TODO make the save files specify this
				player.setPosition(new Vector2DDouble(10 + (i * 18), 20));
			}
		}
		else
		{
			if (isSafeStop)
			{
				// TODO save levels
				
				game = null;
				
				super.stopThis();
			}
			else
			{
				stopThis();
			}
		}
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
		if (stopServer) setIsInGame(false, true);
		
		if (isInGame() && !waitingForPlayersToLoad)
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
