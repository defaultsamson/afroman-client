package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketPing extends BytePacket
{
	public PacketPing(IPConnection... connection)
	{
		super(PacketType.TEST_PING, false, connection);
	}
}
