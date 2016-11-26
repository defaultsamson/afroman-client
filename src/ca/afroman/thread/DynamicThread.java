package ca.afroman.thread;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.interfaces.IDynamicRunning;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.resource.ServerClientObject;

public abstract class DynamicThread extends ServerClientObject implements IDynamicRunning
{
	private static List<String> excludeNames;
	static
	{
		excludeNames = new ArrayList<String>();
		excludeNames.add("system");
		excludeNames.add("main");
	}
	
	private static String getNameWithParents(ThreadGroup group, String name)
	{
		String toReturn = group.getName() + "/" + name;
		
		while ((group = group.getParent()) != null)
		{
			boolean exclude = false;
			
			for (String outName : excludeNames)
			{
				if (group.getName().equals(outName)) exclude = true;
			}
			
			if (!exclude) toReturn = group.getName() + "/" + toReturn;
		}
		
		return toReturn;
	}
	
	private Thread thread;
	private ALogger logger;
	
	private boolean exit = false;
	protected boolean isRunning = false;
	
	public DynamicThread(boolean isServerSide, ThreadGroup group, String name)
	{
		super(isServerSide);
		
		thread = new Thread(group, getNameWithParents(group, name))
		{
			@Override
			public void run()
			{
				dynamicRun();
			}
		};
		
		logger = new ALogger(thread.getName());
	}
	
	public void dynamicRun()
	{
		while (!exit)
		{
			while (isRunning)
			{
				onRun();
				
				try
				{
					Thread.sleep(3);
				}
				catch (InterruptedException e)
				{
					logger().log(ALogType.CRITICAL, "Couldn't sleep dynamic thread during runtime", e);
				}
			}
			
			try
			{
				Thread.sleep(200);
			}
			catch (InterruptedException e)
			{
				logger().log(ALogType.CRITICAL, "Couldn't sleep dynamic thread while paused", e);
			}
		}
	}
	
	public Thread getThread()
	{
		return thread;
	}
	
	public ALogger logger()
	{
		return logger;
	}
	
	/**
	 * Runs when this thread comes to a pause.
	 */
	public void onPause()
	{
		
	}
	
	/**
	 * Runs every time that the thread loops.
	 */
	public abstract void onRun();
	
	/**
	 * Runs when this thread comes back from being paused.
	 */
	public void onUnpause()
	{
		
	}
	
	@Override
	public void pauseThis()
	{
		isRunning = false;
	}
	
	/**
	 * Starts this thread, unpausing it if it's only paused,
	 * and starting it completely if it's not yet been started.
	 * <p>
	 * <b>NOTE:</b> When overriding, put <code>super.startThis()</code>
	 * at the end so that everything is initialised before the thread begins.
	 */
	@Override
	public void startThis()
	{
		if (exit)
		{
			logger().log(ALogType.CRITICAL, "Thread is trying to be started from a stopped state");
			return;
		}
		
		if (!isRunning)
		{
			isRunning = true;
			
			// If it's not already started and hasn't been exited, start it
			if (!thread.isAlive())
			{
				logger().log(ALogType.DEBUG, "Starting Thread");
				thread.start();
			}
			else
			{
				onUnpause();
			}
		}
		else
		{
			logger().log(ALogType.WARNING, "This thread is already running");
		}
	}
	
	/**
	 * Stops this thread from running, allowing it to finish any operations before quitting.
	 */
	@Override
	public void stopThis()
	{
		exit = true;
		isRunning = false;
		logger().log(ALogType.DEBUG, "Stopping Thread");
	}
}
