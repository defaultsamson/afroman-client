package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketServerClientStartTCP extends BytePacket
{
	public PacketServerClientStartTCP(IPConnection... connection)
	{
		super(PacketType.START_TCP, false, connection);
		
		content = new byte[] { typeOrd() };
	}
}
