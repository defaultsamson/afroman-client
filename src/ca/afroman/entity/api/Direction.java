package ca.afroman.entity.api;

public enum Direction
{
	NONE,
	UP,
	DOWN,
	LEFT,
	RIGHT;
	
	public static Direction fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
}
