package ca.afroman.events;

import ca.afroman.util.IDCounter;

public class IEventCounter
{
	private static IDCounter idCounter;
	
	public static IDCounter getIDCounter()
	{
		if (idCounter == null)
		{
			idCounter = new IDCounter();
		}
		
		return idCounter;
	}
}
