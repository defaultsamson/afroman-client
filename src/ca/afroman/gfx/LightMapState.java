package ca.afroman.gfx;

public enum LightMapState
{
	ON,
	CHEAP,
	OFF;
	
	public static LightMapState fromOrdinal(int ordinal)
	{
		return values()[ordinal];
	}
}
