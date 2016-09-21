package ca.afroman.game;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.client.ClientGame;
import ca.afroman.client.Role;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.interfaces.IPacketParser;
import ca.afroman.level.Level;
import ca.afroman.level.LevelType;
import ca.afroman.packet.BytePacket;
import ca.afroman.resource.IDCounter;
import ca.afroman.server.ServerGame;
import ca.afroman.thread.DynamicTickRenderThread;

public abstract class Game extends DynamicTickRenderThread implements IPacketParser
{
	public static final int MAX_PLAYERS = 8;
	public static final int DEFAULT_PORT = 2143;
	public static final String IPv4_LOCALHOST = "127.0.0.1";
	
	public static Game instance(boolean serverSide)
	{
		return serverSide ? ServerGame.instance() : ClientGame.instance();
	}
	
	protected boolean isInGame;
	protected List<Level> levels;
	
	protected List<PlayerEntity> players;
	private SocketManager socketManager;
	
	private List<BytePacket> toProcess;
	
	public Game(ThreadGroup threadGroup, String name, boolean isServerSide, int ticks)
	{
		super(threadGroup, name, isServerSide, ticks);
		isInGame = false;
		
		levels = new ArrayList<Level>();
		players = new ArrayList<PlayerEntity>(2);
		
		// socketManager = new NetworkManager(this);
		toProcess = new ArrayList<BytePacket>();
	}
	
	@Override
	public void addPacketToParse(BytePacket pack)
	{
		synchronized (toProcess)
		{
			toProcess.add(pack);
		}
	}
	
	public void beginGame()
	{
		isInGame = true;
	}
	
	public Level getLevel(LevelType type)
	{
		for (Level level : getLevels())
		{
			if (level.getType() == type) return level;
		}
		return null;
	}
	
	public List<Level> getLevels()
	{
		return levels;
	}
	
	/**
	 * Gets the player with the given role.
	 * 
	 * @param role whether it's player 1 or 2
	 * @return the player.
	 */
	public PlayerEntity getPlayer(Role role)
	{
		for (PlayerEntity entity : getPlayers())
		{
			if (entity.getRole() == role) return entity;
		}
		return null;
	}
	
	public List<PlayerEntity> getPlayers()
	{
		return players;
	}
	
	public boolean isInGame()
	{
		return isInGame;
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		isInGame = false;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
	}
	
	@Override
	public void onStop()
	{
		super.onStop();
		
		// TODO save level states
		synchronized (toProcess)
		{
			toProcess.clear();
		}
		
		if (socketManager != null) socketManager.stopThis();
		
		if (getLevels() != null) getLevels().clear();
		IDCounter.resetAll();
	}
	
	@Override
	public void onUnpause()
	{
		super.onUnpause();
		isInGame = true;
	}
	
	public SocketManager sockets()
	{
		return socketManager;
	}
	
	public boolean startSocket(String serverIpAddress, int port)
	{
		if (socketManager != null) socketManager.stopThis();
		socketManager = new SocketManager(this);
		boolean successful = socketManager.setServerConnection(serverIpAddress, SocketManager.validatedPort(port));
		
		if (!successful) socketManager = null;
		
		return successful;
	}
	
	public void stopSocket()
	{
		if (socketManager != null) socketManager.stopThis();
		socketManager = null;
	}
	
	@Override
	public void tick()
	{
		synchronized (toProcess)
		{
			for (BytePacket pack : toProcess)
			{
				parsePacket(pack);
			}
			
			toProcess.clear();
		}
	}
}
