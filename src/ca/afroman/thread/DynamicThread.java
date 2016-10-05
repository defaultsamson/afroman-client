package ca.afroman.thread;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.interfaces.IDynamicRunning;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;

public abstract class DynamicThread extends Thread implements IDynamicRunning
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
	
	private boolean exit = false;
	protected boolean isRunning = false;
	
	private ALogger logger;
	
	public DynamicThread(ThreadGroup group, String name)
	{
		super(group, getNameWithParents(group, name));
		
		logger = new ALogger(this.getName());
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
	
	@Override
	public void run()
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
	
	/**
	 * @deprecated Use startThis()
	 */
	@Deprecated
	@Override
	public void start()
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
			if (!this.isAlive())
			{
				logger().log(ALogType.DEBUG, "Starting Thread");
				super.start();
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
	 * Starts this thread, unpausing it if it's only paused,
	 * and starting it completely if it's not yet been started.
	 * <p>
	 * <b>NOTE:</b> When overriding, put <code>super.startThis()</code>
	 * at the end so that everything is initialised before the thread begins.
	 */
	@Override
	public void startThis()
	{
		this.start();
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
