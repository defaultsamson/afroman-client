package ca.afroman.packet;

import ca.afroman.legacy.packet.PacketType;
import ca.afroman.network.IPConnection;
import ca.afroman.server.DenyJoinReason;
import ca.afroman.util.ByteUtil;

public class PacketDenyJoin extends BytePacket
{
	private DenyJoinReason reason;
	
	public PacketDenyJoin(DenyJoinReason reason, IPConnection... connection)
	{
		super(PacketType.DENY_JOIN, false, connection);
		
		this.reason = reason;
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return ByteUtil.shortAsBytes((short) reason.ordinal());
	}
}
