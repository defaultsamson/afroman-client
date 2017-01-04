package ca.afroman.packet.level;

import java.nio.ByteBuffer;

import ca.afroman.level.api.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.util.ByteUtil;

public class PacketEntityMove extends BytePacket
{
	private static final int ALLOCATE_SIZE = 3 + ByteUtil.INT_BYTE_COUNT + ByteUtil.SHORT_BYTE_COUNT;
	
	public PacketEntityMove(LevelType levelType, int id, byte dXa, byte dYa, IPConnection... connection)
	{
		super(PacketType.ENTITY_MOVE, false, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ALLOCATE_SIZE).put(typeOrd());
		
		buf.putShort((short) levelType.ordinal());
		buf.putInt(id);
		buf.put(dXa).put(dYa);
		
		content = buf.array();
	}
}
