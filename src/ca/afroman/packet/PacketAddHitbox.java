package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.entity.api.Hitbox;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketAddHitbox extends BytePacket
{
	private byte[] toSend;
	
	public PacketAddHitbox(LevelType level, Hitbox box, IPConnection... connection)
	{
		this(level, box.getID(), box.getX(), box.getY(), box.getWidth(), box.getHeight(), connection);
	}
	
	public PacketAddHitbox(LevelType level, int id, double x, double y, double width, double height, IPConnection... connection)
	{
		super(PacketType.ADD_LEVEL_HITBOX, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ByteUtil.SHORT_BYTE_COUNT + (5 * ByteUtil.INT_BYTE_COUNT));
		
		buf.putShort((short) level.ordinal());
		buf.putInt(id);
		buf.putInt((int) x);
		buf.putInt((int) y);
		buf.putInt((int) width);
		buf.putInt((int) height);
		
		toSend = buf.array();
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
