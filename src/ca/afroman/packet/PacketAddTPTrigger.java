package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.events.TPTrigger;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketAddTPTrigger extends BytePacket
{
	private byte[] toSend;
	
	public PacketAddTPTrigger(LevelType level, int id, double x, double y, double width, double height, IPConnection... connection)
	{
		super(PacketType.ADD_EVENT_TP_TRIGGER, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ByteUtil.SHORT_BYTE_COUNT + (ByteUtil.INT_BYTE_COUNT * 5));
		
		buf.putShort((short) level.ordinal());
		buf.putInt(id);
		buf.putInt((int) x);
		buf.putInt((int) y);
		buf.putInt((int) width);
		buf.putInt((int) height);
		
		toSend = buf.array();
	}
	
	public PacketAddTPTrigger(LevelType level, TPTrigger trig, IPConnection... connection)
	{
		this(level, trig.getID(), (int) trig.getHitbox().getX(), (int) trig.getHitbox().getY(), (int) trig.getHitbox().getWidth(), (int) trig.getHitbox().getHeight(), connection);
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
