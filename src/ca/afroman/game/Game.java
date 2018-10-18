package ca.afroman.game;

import java.util.ArrayList;

import ca.afroman.client.ClientGame;
import ca.afroman.client.ExitGameReason;
import ca.afroman.entity.PlayerEntity;
import ca.afroman.events.BattleScene;
import ca.afroman.interfaces.IPacketParser;
import ca.afroman.level.MainLevel;
import ca.afroman.level.api.Level;
import ca.afroman.level.api.LevelType;
import ca.afroman.network.IncomingPacketWrapper;
import ca.afroman.packet.technical.PacketStartServer;
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
	
	private boolean isPaused;
	private boolean isInGame;
	private ArrayList<Level> levels;
	private ArrayList<BattleScene> battles;
	private ArrayList<PlayerEntity> players;
	
	private SocketManager socketManager;
	private ArrayList<IncomingPacketWrapper> toProcess;
	
	public Game(boolean isServerSide, ThreadGroup threadGroup, String name, int ticks)
	{
		super(isServerSide, threadGroup, name, ticks);
		isPaused = false;
		isInGame = false;
		
		levels = new ArrayList<Level>();
		battles = new ArrayList<BattleScene>();
		players = new ArrayList<PlayerEntity>(2);
		
		toProcess = new ArrayList<IncomingPacketWrapper>();
	}
	
	@Override
	public void addPacketToParse(IncomingPacketWrapper pack)
	{
		synchronized (toProcess)
		{
			toProcess.add(pack);
		}
	}
	
	public BattleScene getBattle(int id)
	{
		for (BattleScene b : battles)
			if (b.getID() == id) return b;
		
		return null;
	}
	
	public ArrayList<BattleScene> getBattles()
	{
		return battles;
	}
	
	public Level getLevel(LevelType type)
	{
		for (Level level : getLevels())
		{
			if (level.getLevelType() == type) return level;
		}
		return null;
	}
	
	public ArrayList<Level> getLevels()
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
	
	public ArrayList<PlayerEntity> getPlayers()
	{
		return players;
	}
	
	public boolean isInGame()
	{
		return isInGame;
	}
	
	public boolean isPaused()
	{
		return isPaused;
	}
	
	public void loadLevels()
	{
		// TODO load level states
		levels.clear();
		levels.add(new MainLevel(isServerSide()));
		levels.add(new Level(isServerSide(), LevelType.SECOND));
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		isPaused = false;
	}
	
	@Override
	public void onUnpause()
	{
		super.onUnpause();
		isPaused = true;
	}
	
	public void setIsInGame(boolean isInGame)
	{
		this.isInGame = isInGame;
	}
	
	public SocketManager sockets()
	{
		return socketManager;
	}
	
	public boolean startSocket(String serverIpAddress, int port)
	{
		stopSocket();
		
		socketManager = new SocketManager(this);
		socketManager.startThis();
		
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
	public void stopThis()
	{
		super.stopThis();
		
		if (isServerSide())
		{
			sockets().sendPacketToAllClients(new PacketStartServer(false));
			
			// TODO make a more surefire way to ensure that all clients got the message
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			ClientGame.instance().exitFromGame(ExitGameReason.DISCONNECT);
		}
		
		stopSocket();
		
		getLevels().clear();
		getBattles().clear();
		getPlayers().clear();
		synchronized (toProcess)
		{
			toProcess.clear();
		}
	}
	
	@Override
	public void tick()
	{
		synchronized (toProcess)
		{
			for (IncomingPacketWrapper pack : toProcess)
			{
				parsePacket(pack);
			}
			
			toProcess.clear();
		}
	}
}
