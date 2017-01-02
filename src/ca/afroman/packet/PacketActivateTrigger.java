package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.game.Role;
import ca.afroman.level.api.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketActivateTrigger extends BytePacket
{
	private static final int ALLOCATE_SIZE = ByteUtil.SHORT_BYTE_COUNT + ByteUtil.INT_BYTE_COUNT + 2;
	
	public PacketActivateTrigger(int id, LevelType level, Role player, IPConnection... connection)
	{
		super(PacketType.ACTIVATE_TRIGGER, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ALLOCATE_SIZE).put(typeOrd());
		
		buf.putShort((short) level.ordinal());
		buf.putInt(id);
		buf.put((byte) player.ordinal());
		
		content = buf.array();
	}
}
