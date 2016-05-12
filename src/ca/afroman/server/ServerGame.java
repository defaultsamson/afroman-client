package ca.afroman.server;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.Entity;
import ca.afroman.entity.Hitbox;
import ca.afroman.entity.ServerPlayerEntity;
import ca.afroman.level.Level;
import ca.afroman.level.LevelType;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.packet.PacketAddLevelHitbox;
import ca.afroman.packet.PacketAddLevelTile;
import ca.afroman.packet.PacketInstantiateLevel;
import ca.afroman.packet.PacketSendLevels;
import ca.afroman.player.Role;
import ca.afroman.thread.DynamicTickThread;

public class ServerGame extends DynamicTickThread
{
	private static ServerGame game = null;
	
	private boolean isInGame = false;
	
	public static ServerGame instance()
	{
		return game;
	}
	
	public List<Level> levels;
	public List<ServerPlayerEntity> players;
	
	private ServerSocket socketServer = null;
	
	public ServerGame(String password)
	{
		super(60);
		
		if (game == null) game = this;
		
		socketServer = new ServerSocket(password);
	}
	
	public void loadGame()
	{
		socket().sendPacketToAllClients(new PacketSendLevels(true));
		
		levels = new ArrayList<Level>();
		levels.add(Level.fromFile(LevelType.MAIN));
		
		players = new ArrayList<ServerPlayerEntity>();
		
		// Sends the levels to everyone else
		for (Level level : levels)
		{
			PacketInstantiateLevel levelPack = new PacketInstantiateLevel(level.getType());
			
			socketServer.sendPacketToAllClients(levelPack);
			
			for (Entity tile : level.getTiles())
			{
				socketServer.sendPacketToAllClients(new PacketAddLevelTile(tile));
			}
			
			for (Hitbox box : level.getHitboxes())
			{
				socketServer.sendPacketToAllClients(new PacketAddLevelHitbox(level.getType(), box));
			}
		}
		
		players = new ArrayList<ServerPlayerEntity>();
		players.add(new ServerPlayerEntity(Role.PLAYER1, 80, 50));
		players.add(new ServerPlayerEntity(Role.PLAYER2, 20, 20));
		
		for (ServerPlayerEntity player : players)
		{
			player.addToLevel(levels.get(0));
		}
		
		/*
		 * TODO add level loading
		 * player = new PlayerMPEntity(100, 120, 1, input, null, -1);
		 * player.addToLevel(blankLevel);
		 * // player = new PlayerMPEntity(blankLevel, 100, 100, 1, input, null, -1);
		 * // player.setCameraToFollow(true);
		 * // blankLevel.putPlayer();
		 */
		
		// TODO only start ticking once the game has loaded for all clients
		// isInGame = true;
		
		socket().sendPacketToAllClients(new PacketSendLevels(false));
	}
	
	@Override
	public synchronized void onStart()
	{
		super.onStart();
		
		socketServer.start();
	}
	
	@Override
	public synchronized void onStop()
	{
		// TODO save levels?
		
		socketServer.stopThread();
		
		game = null;
		
		if (levels != null) levels.clear();
		ConnectedPlayer.resetNextAvailableID();
		Hitbox.resetNextAvailableID();
		Entity.resetNextAvailableID();
	}
	
	@Override
	public synchronized void tick()
	{
		if (isInGame)
		{
			if (levels != null)
			{
				for (Level level : levels)
				{
					level.tick();
				}
			}
		}
	}
	
	public Level getLevelByType(LevelType type)
	{
		for (Level level : levels)
		{
			if (level.getType() == type) return level;
		}
		return null;
	}
	
	// TODO add server-wide build mode? probably not
	// public boolean isBuildMode()
	// {
	// return buildMode;
	// }
	
	@Override
	public void onPause()
	{
		isInGame = false;
	}
	
	@Override
	public void onUnpause()
	{
		isInGame = true;
	}
	
	public void beginGame()
	{
		isInGame = true;
	}
	
	public ServerSocket socket()
	{
		return socketServer;
	}
	
	/**
	 * Gets the player with the given role.
	 * 
	 * @param role whether it's player 1 or 2
	 * @return the player.
	 */
	public synchronized ServerPlayerEntity getPlayer(Role role)
	{
		for (ServerPlayerEntity entity : players)
		{
			if (entity.getRole() == role) return entity;
		}
		return null;
	}
}
