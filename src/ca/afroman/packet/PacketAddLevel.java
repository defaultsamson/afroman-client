package ca.afroman.packet;

import ca.afroman.legacy.packet.PacketType;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketAddLevel extends BytePacket
{
	private LevelType lType;
	
	public PacketAddLevel(LevelType lType, IPConnection... connection)
	{
		super(PacketType.INSTANTIATE_LEVEL, true, connection);
		
		this.lType = lType;
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return ByteUtil.shortAsBytes((short) lType.ordinal());
	}
}
