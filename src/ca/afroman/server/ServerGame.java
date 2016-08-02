package ca.afroman.server;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.client.Role;
import ca.afroman.entity.ServerPlayerEntity;
import ca.afroman.entity.api.Entity;
import ca.afroman.entity.api.Hitbox;
import ca.afroman.events.HitboxTrigger;
import ca.afroman.events.IEvent;
import ca.afroman.gfx.PointLight;
import ca.afroman.level.Level;
import ca.afroman.level.LevelType;
import ca.afroman.packet.PacketAddHitbox;
import ca.afroman.packet.PacketAddLevel;
import ca.afroman.packet.PacketAddPointLight;
import ca.afroman.packet.PacketAddTile;
import ca.afroman.packet.PacketAddTrigger;
import ca.afroman.packet.PacketEditTrigger;
import ca.afroman.packet.PacketSendLevels;
import ca.afroman.thread.DynamicTickThread;
import ca.afroman.util.IDCounter;

public class ServerGame extends DynamicTickThread
{
	private static ServerGame game = null;
	
	private boolean isInGame = false;
	private boolean isSendingLevels = false;
	
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
	
	public ServerGame(String password, String port)
	{
		super(newDefaultThreadGroupInstance(), "Game", 60);
		
		if (game == null) game = this;
		
		socketManager = new ServerSocketManager(password, port);
	}
	
	public void loadGame()
	{
		isSendingLevels = true;
		
		sockets().sender().sendPacketToAllClients(new PacketSendLevels(true));
		
		levels = new ArrayList<Level>();
		
		for (LevelType type : LevelType.values())
		{
			if (type != LevelType.NULL) getLevels().add(Level.fromFile(true, type));
		}
		
		players = new ArrayList<ServerPlayerEntity>();
		
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
				sockets().sender().sendPacketToAllClients(new PacketAddHitbox(level.getType(), box));
			}
			
			for (PointLight light : level.getLights())
			{
				sockets().sender().sendPacketToAllClients(new PacketAddPointLight(level.getType(), light));
			}
			
			for (IEvent event : level.getScriptedEvents())
			{
				if (event instanceof HitboxTrigger)
				{
					HitboxTrigger e = (HitboxTrigger) event;
					sockets().sender().sendPacketToAllClients(new PacketAddTrigger(level.getType(), e));
					sockets().sender().sendPacketToAllClients(new PacketEditTrigger(level.getType(), e.getID(), e.getTriggerTypes(), e.getInTriggers(), e.getOutTriggers()));
				}
			}
		}
		
		players = new ArrayList<ServerPlayerEntity>();
		getPlayers().add(new ServerPlayerEntity(Role.PLAYER1, 80, 50));
		getPlayers().add(new ServerPlayerEntity(Role.PLAYER2, 20, 20));
		
		for (int i = 0; i < getPlayers().size(); i++)
		{
			ServerPlayerEntity player = getPlayers().get(i);
			player.addToLevel(this.getLevelByType(LevelType.MAIN));
			player.setLocation(10 + (i * 18), 20);
		}
		
		// TODO only start ticking once the game has loaded for all clients
		
		isInGame = true;
		isSendingLevels = false;
		
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
		IDCounter.resetAll();
		
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
	
	public boolean isInGame()
	{
		return isInGame;
	}
	
	public boolean isSendingLevels()
	{
		return isSendingLevels;
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
