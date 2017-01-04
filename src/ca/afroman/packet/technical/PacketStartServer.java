package ca.afroman.packet.technical;

import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;

public class PacketStartServer extends BytePacket
{
	public PacketStartServer(boolean shouldStart, IPConnection... connection)
	{
		super(PacketType.START_SERVER, true, connection);
		
		content = new byte[] { typeOrd(), shouldStart ? 1 : (byte) 0 };
	}
}
