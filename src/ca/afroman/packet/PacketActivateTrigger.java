package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketActivateTrigger extends BytePacket
{
	private byte[] toSend;
	
	public PacketActivateTrigger(int id, LevelType level, IPConnection... connection)
	{
		super(PacketType.ACTIVATE_TRIGGER, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ByteUtil.SHORT_BYTE_COUNT + ByteUtil.INT_BYTE_COUNT);
		
		buf.putShort((short) level.ordinal());
		buf.putInt(id);
		
		toSend = buf.array();
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
