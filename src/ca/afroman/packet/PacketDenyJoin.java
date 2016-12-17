package ca.afroman.packet;

import ca.afroman.network.IPConnection;
import ca.afroman.server.DenyJoinReason;

public class PacketDenyJoin extends BytePacket
{
	public PacketDenyJoin(DenyJoinReason reason, IPConnection... connection)
	{
		super(PacketType.DENY_JOIN, false, connection);
		
		content = new byte[] { typeOrd(), (byte) reason.ordinal() };
	}
}
