package ca.afroman.server;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.client.Role;
import ca.afroman.entity.ServerPlayerEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.gfx.PointLight;
import ca.afroman.level.Level;
import ca.afroman.level.LevelType;
import ca.afroman.network.ConnectedPlayer;
import ca.afroman.packet.PacketAddLevelHitbox;
import ca.afroman.packet.PacketAddLevelLight;
import ca.afroman.packet.PacketAddLevelTile;
import ca.afroman.packet.PacketInstantiateLevel;
import ca.afroman.packet.PacketSendLevels;
import ca.afroman.thread.DynamicTickThread;

public class ServerGame extends DynamicTickThread
{
	private static ServerGame game = null;
	
	private boolean isInGame = false;
	
	public static ServerGame instance()
	{
		return game;
	}
	
	private static ThreadGroup newDefaultThreadGroupInstance()
	{
		return new ThreadGroup("Server");
	}
	
	private List<Level> levels;
	private List<ServerPlayerEntity> players;
	
	private ServerSocketManager socketManager;
	
	public ServerGame(String password)
	{
		super(newDefaultThreadGroupInstance(), "Game", 60);
		
		if (game == null) game = this;
		
		socketManager = new ServerSocketManager(password);
	}
	
	public void loadGame()
	{
		sockets().sender().sendPacketToAllClients(new PacketSendLevels(true));
		
		levels = new ArrayList<Level>();
		getLevels().add(Level.fromFile(LevelType.MAIN));
		
		players = new ArrayList<ServerPlayerEntity>();
		
		// Sends the levels to everyone else
		for (Level level : getLevels())
		{
			PacketInstantiateLevel levelPack = new PacketInstantiateLevel(level.getType());
			
			sockets().sender().sendPacketToAllClients(levelPack);
			
			int layer = 0;
			for (List<Entity> tileList : level.getTiles())
			{
				for (Entity tile : tileList)
				{
					sockets().sender().sendPacketToAllClients(new PacketAddLevelTile(layer, level.getType(), tile));
				}
				
				layer++;
			}
			
			for (Hitbox box : level.getHitboxes())
			{
				sockets().sender().sendPacketToAllClients(new PacketAddLevelHitbox(level.getType(), box));
			}
			
			for (PointLight light : level.getLights())
			{
				sockets().sender().sendPacketToAllClients(new PacketAddLevelLight(level.getType(), light));
			}
		}
		
		players = new ArrayList<ServerPlayerEntity>();
		getPlayers().add(new ServerPlayerEntity(Role.PLAYER1, 80, 50));
		getPlayers().add(new ServerPlayerEntity(Role.PLAYER2, 20, 20));
		
		for (ServerPlayerEntity player : getPlayers())
		{
			player.addToLevel(getLevels().get(0));
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
		
		sockets().sender().sendPacketToAllClients(new PacketSendLevels(false));
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		sockets().startThis();
	}
	
	@Override
	public void onStop()
	{
		// TODO save levels?
		
		sockets().stopThis();
		
		if (getLevels() != null) getLevels().clear();
		ConnectedPlayer.resetNextAvailableID();
		Hitbox.resetNextAvailableID();
		Entity.resetNextAvailableID();
		
		game = null;
	}
	
	@Override
	public void tick()
	{
		if (isInGame)
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
	
	public Level getLevelByType(LevelType type)
	{
		for (Level level : getLevels())
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
	
	public ServerSocketManager sockets()
	{
		return socketManager;
	}
	
	/**
	 * Gets the player with the given role.
	 * 
	 * @param role whether it's player 1 or 2
	 * @return the player.
	 */
	public ServerPlayerEntity getPlayer(Role role)
	{
		for (ServerPlayerEntity entity : getPlayers())
		{
			if (entity.getRole() == role) return entity;
		}
		return null;
	}
	
	public List<Level> getLevels()
	{
		return levels;
	}
	
	public List<ServerPlayerEntity> getPlayers()
	{
		return players;
	}
}
