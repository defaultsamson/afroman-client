package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.level.LevelObjectType;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketRemoveLevelObject extends BytePacket
{
	private byte[] toSend;
	
	public PacketRemoveLevelObject(int id, LevelType type, LevelObjectType objType, IPConnection... connection)
	{
		super(PacketType.REMOVE_LEVEL_OBJECT, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ByteUtil.INT_BYTE_COUNT + (ByteUtil.SHORT_BYTE_COUNT * 2));
		buf.putShort((short) type.ordinal());
		buf.putShort((short) objType.ordinal());
		buf.putInt(id);
		
		toSend = buf.array();
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
