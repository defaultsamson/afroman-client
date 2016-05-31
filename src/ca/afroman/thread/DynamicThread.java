package ca.afroman.thread;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.interfaces.IDynamicRunning;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;

public abstract class DynamicThread extends Thread implements IDynamicRunning
{
	private boolean exit = false;
	protected boolean isRunning = false;
	private ALogger logger;
	
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
	
	public DynamicThread(ThreadGroup group, String name)
	{
		super(group, getNameWithParents(group, name));
		
		logger = new ALogger(this.getName());
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
					Thread.sleep(2);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			try
			{
				Thread.sleep(200);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Runs every time that the thread loops.
	 */
	public abstract void onRun();
	
	@Override
	public void startThis()
	{
		this.start();
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
			logger().log(ALogType.CRITICAL, "Thread is trying to be started from a stopped state.");
			return;
		}
		
		if (!isRunning)
		{
			isRunning = true;
			
			// If it's not already started and hasn't been exited, start it
			if (!this.isAlive())
			{
				onStart();
				super.start();
			}
			else
			{
				onUnpause();
			}
		}
		else
		{
			logger().log(ALogType.WARNING, "This thread is already running: " + this.toString());
		}
	}
	
	public ALogger logger()
	{
		return logger;
	}
	
	/**
	 * Runs when this thread initially starts.
	 */
	public abstract void onStart();
	
	@Override
	public void pauseThis()
	{
		isRunning = false;
	}
	
	/**
	 * Runs when this thread comes to a pause.
	 */
	public abstract void onPause();
	
	/**
	 * Runs when this thread comes back from being paused.
	 */
	public abstract void onUnpause();
	
	/**
	 * Completely stops this thread from running.
	 */
	@Override
	public void stopThis()
	{
		exit = true;
		isRunning = false;
		onStop();
	}
	
	/**
	 * Runs when this thread comes to a stop.
	 */
	public abstract void onStop();
}
