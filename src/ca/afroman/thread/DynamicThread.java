package ca.afroman.thread;

public abstract class DynamicThread extends Thread
{
	private boolean exit = false;
	protected boolean isRunning = false;
	
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
				Thread.sleep(250);
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
	public void start()
	{
		if (exit)
		{
			System.out.println("[THREAD] [CRITICAL] Thread is trying to be started from a stopped state.");
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
			System.out.println("[THREAD] [WARNING] Thread is already running: " + this.toString());
		}
	}
	
	/**
	 * Runs when this thread initially starts.
	 */
	public abstract void onStart();
	
	public void pauseThread()
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
	public void stopThread()
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
