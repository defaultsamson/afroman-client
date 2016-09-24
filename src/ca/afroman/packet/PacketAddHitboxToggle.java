package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.events.HitboxToggleReceiver;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketAddHitboxToggle extends BytePacket
{
	private byte[] toSend;
	
	public PacketAddHitboxToggle(LevelType level, HitboxToggleReceiver hitbox, IPConnection... connection)
	{
		this(level, hitbox.getID(), (int) hitbox.getX(), (int) hitbox.getY(), (int) hitbox.getWidth(), (int) hitbox.getHeight(), connection);
	}
	
	public PacketAddHitboxToggle(LevelType level, int id, int x, int y, int width, int height, IPConnection... connection)
	{
		super(PacketType.ADD_EVENT_HITBOX_TOGGLE, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ByteUtil.SHORT_BYTE_COUNT + (5 * ByteUtil.INT_BYTE_COUNT));
		
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
