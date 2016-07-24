package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketBeginGame extends BytePacket
{
	public PacketBeginGame(IPConnection... connection)
	{
		super(PacketType.START_SERVER, true, connection);
	}
}
