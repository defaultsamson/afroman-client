package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketPingClientServer extends BytePacket
{
	public PacketPingClientServer(IPConnection... connection)
	{
		super(PacketType.TEST_PING, false, connection);
	}
}
