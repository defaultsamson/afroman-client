package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketStartServer extends BytePacket
{
	public PacketStartServer(IPConnection... connection)
	{
		super(PacketType.START_SERVER, true, connection);
	}
}
