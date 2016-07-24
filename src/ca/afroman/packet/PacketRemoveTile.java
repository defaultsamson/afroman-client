package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketRemoveTile extends BytePacket
{
	private byte[] toSend;
	
	public PacketRemoveTile(int id, LevelType type, IPConnection... connection)
	{
		super(PacketType.REMOVE_LEVEL_TILE, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ByteUtil.INT_BYTE_COUNT + ByteUtil.SHORT_BYTE_COUNT);
		buf.putShort((short) type.ordinal());
		buf.putInt(id);
		
		toSend = buf.array();
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
