package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.events.HitboxTrigger;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketAddTrigger extends BytePacket
{
	private byte[] toSend;
	
	public PacketAddTrigger(LevelType level, HitboxTrigger trig, IPConnection... connection)
	{
		this(level, trig.getID(), (int) trig.getX(), (int) trig.getY(), (int) trig.getWidth(), (int) trig.getHeight(), connection);
	}
	
	public PacketAddTrigger(LevelType level, int id, int x, int y, int width, int height, IPConnection... connection)
	{
		super(PacketType.ADD_EVENT_HITBOX_TRIGGER, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ByteUtil.SHORT_BYTE_COUNT + (ByteUtil.INT_BYTE_COUNT * 5));
		
		// Level Type
		buf.putShort((short) level.ordinal());
		
		// ID
		buf.putInt(id);
		
		// x
		buf.putInt(x);
		
		// y
		buf.putInt(y);
		
		// width
		buf.putInt(width);
		
		// height
		buf.putInt(height);
		
		toSend = buf.array();
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
