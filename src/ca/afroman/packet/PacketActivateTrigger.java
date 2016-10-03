package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.game.Role;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketActivateTrigger extends BytePacket
{
	private byte[] toSend;
	
	public PacketActivateTrigger(int id, LevelType level, Role player, IPConnection... connection)
	{
		super(PacketType.ACTIVATE_TRIGGER, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ByteUtil.SHORT_BYTE_COUNT + ByteUtil.INT_BYTE_COUNT + 1);
		
		buf.putShort((short) level.ordinal());
		buf.putInt(id);
		buf.put((byte) player.ordinal());
		// buf.put((byte) triggerType.ordinal());
		
		toSend = buf.array();
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
