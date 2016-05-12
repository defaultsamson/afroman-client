package ca.afroman.packet;

public enum DenyJoinReason
{
	NEED_PASSWORD,
	DUPLICATE_USERNAME,
	FULL_SERVER,
	OLD_CLIENT,
	OLD_SERVER;
	
	public static DenyJoinReason fromOrdinal(int ordinal)
	{
		return values()[ordinal];
	}
}
