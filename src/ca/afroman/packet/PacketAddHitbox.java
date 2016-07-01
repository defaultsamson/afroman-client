package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.entity.api.Hitbox;
import ca.afroman.legacy.packet.PacketType;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketAddHitbox extends BytePacket
{
	private byte[] toSend;
	
	public PacketAddHitbox(LevelType level, Hitbox hitbox, IPConnection... connection)
	{
		super(PacketType.ADD_LEVEL_HITBOX, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ByteUtil.SHORT_BYTE_COUNT + (5 * ByteUtil.INT_BYTE_COUNT));
		
		// Level Type
		buf.putShort((short) level.ordinal());
		
		// ID
		buf.putInt(hitbox.getID());
		
		// x
		buf.putInt((int) hitbox.getX());
		
		// y
		buf.putInt((int) hitbox.getY());
		
		// width
		buf.putInt((int) hitbox.getWidth());
		
		// height
		buf.putInt((int) hitbox.getHeight());
		
		toSend = buf.array();
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
