package ca.afroman.client;

public enum ExitGameReason
{
	VITAL_PLAYER_CONNECTION_LOST("Vital player", "lost connection"),
	VITAL_PLAYER_DISCONNECT("Vital player", "disconnected"),
	CONNECTION_LOST("Connection", "lost"),
	DISCONNECT("Disconnected", "from server"),
	SERVER_CLOSED("Server", "closed"),
	KICKED("Kicked", "from server"),
	BANNED("Banned", "from server");
	
	public static ExitGameReason fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
	
	private String firstStr;
	
	private String secondStr;
	
	ExitGameReason(String firstStr, String secondStr)
	{
		this.firstStr = firstStr;
		this.secondStr = secondStr;
	}
	
	public String getFirstString()
	{
		return firstStr;
	}
	
	public String getSecondString()
	{
		return secondStr;
	}
}
