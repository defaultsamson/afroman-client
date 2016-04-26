package ca.pixel.game.network.packet;

public enum PacketType
{
	INVALID,
	LOGIN,
	DISCONNECT;
	
	public static PacketType fromOrdinal(int ordinal)
	{
		return values()[ordinal];
	}
}
