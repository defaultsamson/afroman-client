package ca.afroman.entity;

public enum Direction
{
	NONE,
	UP,
	DOWN,
	LEFT,
	RIGHT;
	
	public static Direction fromOrdinal(int ordinal)
	{
		return values()[ordinal];
	}
}
