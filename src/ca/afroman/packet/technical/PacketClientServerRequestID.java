package ca.afroman.packet.technical;

import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;

public class PacketClientServerRequestID extends BytePacket
{
	public PacketClientServerRequestID(IPConnection... connection)
	{
		super(PacketType.START_TCP, true, connection);
		
		content = new byte[] { typeOrd() };
	}
}
