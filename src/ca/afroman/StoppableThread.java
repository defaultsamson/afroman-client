package ca.afroman;

public class StoppableThread extends Thread
{
	protected boolean isStopped = false;
	
	public void stopThread()
	{
		isStopped = true;
	}
}
