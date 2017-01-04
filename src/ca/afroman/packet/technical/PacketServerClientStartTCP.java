package ca.afroman.packet.technical;

import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;

public class PacketServerClientStartTCP extends BytePacket
{
	public PacketServerClientStartTCP(IPConnection... connection)
	{
		super(PacketType.START_TCP, false, connection);
		
		content = new byte[] { typeOrd() };
	}
}
