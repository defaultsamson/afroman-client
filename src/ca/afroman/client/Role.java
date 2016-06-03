package ca.afroman.client;

public enum Role
{
	PLAYER1,
	PLAYER2,
	SPECTATOR;
	
	public static Role fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
}
