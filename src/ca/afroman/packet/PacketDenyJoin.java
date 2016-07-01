package ca.afroman.packet;

import ca.afroman.legacy.packet.PacketType;
import ca.afroman.network.IPConnection;
import ca.afroman.server.DenyJoinReason;
import ca.afroman.util.ByteUtil;

public class PacketDenyJoin extends BytePacket
{
	private byte[] toSend;
	
	public PacketDenyJoin(DenyJoinReason reason, IPConnection... connection)
	{
		super(PacketType.DENY_JOIN, false, connection);
		
		toSend = ByteUtil.shortAsBytes((short) reason.ordinal());
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
