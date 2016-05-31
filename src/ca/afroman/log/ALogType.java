package ca.afroman.log;

import java.util.logging.Level;

public enum ALogType
{
	DEBUG(Level.FINER),
	IMPORTANT(Level.FINE),
	WARNING(Level.WARNING),
	CRITICAL(Level.SEVERE);
	
	ALogType(Level level)
	{
		this.level = level;
	}
	
	private Level level;
	
	public Level getLevel()
	{
		return level;
	}
}
