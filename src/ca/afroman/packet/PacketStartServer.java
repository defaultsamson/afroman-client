package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketStartServer extends BytePacket
{
	public PacketStartServer(boolean shouldStart, IPConnection... connection)
	{
		super(PacketType.START_SERVER, true, connection);
		
		content = new byte[] { typeOrd(), shouldStart ? 1 : (byte) 0 };
	}
}
