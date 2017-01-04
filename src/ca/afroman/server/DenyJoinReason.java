package ca.afroman.server;

public enum DenyJoinReason
{
	NEED_PASSWORD("Invalid", "Password"),
	DUPLICATE_USERNAME("Duplicate", "Username"),
	FULL_SERVER("Server", "Full"),
	OLD_CLIENT("Client", "Outdated"),
	OLD_SERVER("Server", "Outdated");
	
	public static DenyJoinReason fromOrdinal(int ordinal)
	{
		if (ordinal < 0 || ordinal > values().length - 1) return null;
		
		return values()[ordinal];
	}
	
	private String line1;
	private String line2;
	
	DenyJoinReason(String line1, String line2)
	{
		this.line1 = line1;
		this.line2 = line2;
	}
	
	public String getBottomText()
	{
		return line2;
	}
	
	public String getTopText()
	{
		return line1;
	}
}
