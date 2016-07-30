package ca.afroman.events;

import ca.afroman.util.IDCounter;

public class IEventCounter
{
	private static IDCounter idCounter = new IDCounter();
	
	public static IDCounter getIDCounter()
	{
		return idCounter;
	}
}
