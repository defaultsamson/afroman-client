package ca.afroman.server;

public enum DenyJoinReason
{
	NEED_PASSWORD,
	DUPLICATE_USERNAME,
	FULL_SERVER,
	OLD_CLIENT,
	OLD_SERVER;
	
	public static DenyJoinReason fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
}
