package ca.afroman.resource;


/**
 * Allows application of lockfiles to resources that are not file transfer related.
 * Thread-safe through synchronized methods
 * @see java.nio.channels.FileLock
 *
 */
public class LockFile
{
	private boolean locked;
	
	public LockFile(boolean locked)
	{
		this.locked = locked;
	}
	
	public LockFile()
	{
		locked = false;
	}
	
	public synchronized boolean checkLock()
	{
		return locked;
	}
	
	public synchronized boolean lock()
	{
		if (locked)
		{
			return false;
		}
		else
		{
			locked = true;
			return true;
		}
	}
	
	public synchronized boolean unlock()
	{
		if (locked)
		{
			locked = false;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public synchronized boolean switchLock()
	{
		locked = !locked;
		return locked;
	}
}
