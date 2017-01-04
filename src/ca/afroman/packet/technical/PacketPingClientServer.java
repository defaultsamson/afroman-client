package ca.afroman.packet.technical;

import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;

public class PacketPingClientServer extends BytePacket
{
	public PacketPingClientServer(IPConnection... connection)
	{
		super(PacketType.TEST_PING, false, connection);
		
		content = new byte[] { typeOrd() };
	}
}
