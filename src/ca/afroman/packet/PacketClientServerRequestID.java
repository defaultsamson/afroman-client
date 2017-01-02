package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketClientServerRequestID extends BytePacket
{
	public PacketClientServerRequestID(IPConnection... connection)
	{
		super(PacketType.START_TCP, true, connection);
		
		content = new byte[] { typeOrd() };
	}
}
