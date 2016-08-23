package ca.afroman.resource;

import java.util.ArrayList;
import java.util.List;

public class IDCounter
{
	public static final int WASTE_ID = -1;
	
	private static List<IDCounter> all = new ArrayList<IDCounter>();
	
	public static void resetAll()
	{
		for (IDCounter counter : all)
		{
			counter.reset();
		}
	}
	
	private int nextAvailableID = 0;
	
	public IDCounter()
	{
		all.add(this);
	}
	
	/**
	 * @return the next available ID for use. (Ignored previous ID's that are now free for use. TODO?)
	 */
	public int getNext()
	{
		int toReturn = nextAvailableID;
		nextAvailableID++;
		return toReturn;
	}
	
	/**
	 * Resets the nextAvailableID so that it starts counting from 0 again.
	 * <p>
	 * <b>WARNING: </b>only intended for use on server shutdowns.
	 */
	public void reset()
	{
		nextAvailableID = 0;
	}
}
