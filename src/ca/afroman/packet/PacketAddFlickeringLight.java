package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ByteUtil;

public class PacketAddFlickeringLight extends BytePacket
{
	private byte[] toSend;
	
	public PacketAddFlickeringLight(LevelType level, int id, Vector2DDouble pos, double radius1, double radius2, int ticksPerFrame, IPConnection... connection)
	{
		super(PacketType.ADD_LEVEL_FLICKERINGLIGHT, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ByteUtil.SHORT_BYTE_COUNT + (6 * ByteUtil.INT_BYTE_COUNT));
		
		// Level Type
		buf.putShort((short) level.ordinal());
		
		// ID
		buf.putInt(id);
		
		// x
		buf.putInt((int) pos.getX());
		
		// y
		buf.putInt((int) pos.getY());
		
		// radius1
		buf.putInt((int) radius1);
		
		// radius2
		buf.putInt((int) radius2);
		
		// ticks per frame
		buf.putInt(ticksPerFrame);
		
		toSend = buf.array();
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
