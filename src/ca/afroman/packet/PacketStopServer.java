package ca.afroman.packet;

import ca.afroman.legacy.packet.PacketType;
import ca.afroman.network.IPConnection;

public class PacketStopServer extends BytePacket
{
	public PacketStopServer(IPConnection... connection)
	{
		super(PacketType.STOP_SERVER, true, connection);
	}
}
