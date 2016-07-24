package ca.afroman.packet;

import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketAddLevel extends BytePacket
{
	private byte[] toSend;
	
	public PacketAddLevel(LevelType lType, IPConnection... connection)
	{
		super(PacketType.INSTANTIATE_LEVEL, true, connection);
		
		toSend = ByteUtil.shortAsBytes((short) lType.ordinal());
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
