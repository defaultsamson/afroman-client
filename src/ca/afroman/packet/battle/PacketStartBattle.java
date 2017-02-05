package ca.afroman.packet.battle;

import java.nio.ByteBuffer;

import ca.afroman.level.api.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.util.ByteUtil;

public class PacketStartBattle extends BytePacket
{
	private static final int BUFFER_SIZE = 2 + ByteUtil.INT_BYTE_COUNT + ByteUtil.SHORT_BYTE_COUNT;
	
	public PacketStartBattle(LevelType levelType, int entityFighting, boolean p1, boolean p2, IPConnection... connection)
	{
		super(PacketType.START_BATTLE, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE).put(typeOrd());
		
		buf.put((byte) ((p1 ? 1 : 0) + (p2 ? 2 : 0)));
		buf.putShort((short) levelType.ordinal());
		buf.putInt(entityFighting);
		
		content = buf.array();
	}
}
