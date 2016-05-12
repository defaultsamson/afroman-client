package ca.afroman.player;

public enum Role
{
	PLAYER1,
	PLAYER2,
	SPECTATOR;
	
	public static Role fromOrdinal(int ordinal)
	{
		return values()[ordinal];
	}
}
