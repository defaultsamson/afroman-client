package ca.afroman.gfx;

public enum LightMapState
{
	ON,
	CHEAP,
	OFF;
	
	public static LightMapState fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
}
