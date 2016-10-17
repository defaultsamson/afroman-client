package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.events.HitboxToggle;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketAddHitboxToggle extends BytePacket
{
	private byte[] toSend;
	
	public PacketAddHitboxToggle(LevelType level, HitboxToggle tog, IPConnection... connection)
	{
		this(level, tog.getID(), tog.getHitbox().getX(), tog.getHitbox().getY(), tog.getHitbox().getWidth(), tog.getHitbox().getHeight(), connection);
	}
	
	public PacketAddHitboxToggle(LevelType level, int id, double x, double y, double width, double height, IPConnection... connection)
	{
		super(PacketType.ADD_EVENT_HITBOX_TOGGLE, true, connection);
		
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
