package ca.afroman.resource;

public class ModulusCounter
{
	private int numCounter;
	private int interval;
	
	public ModulusCounter(int interval)
	{
		numCounter = 0;
		this.interval = Math.abs(interval);
	}
	
	public int getInterval()
	{
		return interval;
	}
	
	/**
	 * Tells if this counter has hit the interval yet. It will count up every time this is checked.
	 */
	public boolean isAtInterval()
	{
		numCounter++;
		if (numCounter >= interval)
		{
			numCounter = 0;
			return true;
		}
		return false;
	}
	
	/**
	 * Resets the numCounter so that it starts counting from 0 again.
	 */
	public void reset()
	{
		numCounter = 0;
	}
	
	public void setAtInterval()
	{
		numCounter = interval;
	}
	
	public void setInterval(int newInterval)
	{
		interval = newInterval;
	}
}
