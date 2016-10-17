package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.level.LevelType;
import ca.afroman.light.FlickeringLight;
import ca.afroman.network.IPConnection;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ByteUtil;

public class PacketAddFlickeringLight extends BytePacket
{
	private byte[] toSend;
	
	public PacketAddFlickeringLight(LevelType level, FlickeringLight light, IPConnection... connection)
	{
		this(level, light.getID(), light.getPosition(), light.getRadius(), light.getRadius2(), light.getTicksPerFrame(), connection);
	}
	
	public PacketAddFlickeringLight(LevelType level, int id, Vector2DDouble pos, double radius1, double radius2, int ticksPerFrame, IPConnection... connection)
	{
		super(PacketType.ADD_LEVEL_FLICKERINGLIGHT, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ByteUtil.SHORT_BYTE_COUNT + (6 * ByteUtil.INT_BYTE_COUNT));
		
		buf.putShort((short) level.ordinal());
		buf.putInt(id);
		buf.putInt((int) pos.getX());
		buf.putInt((int) pos.getY());
		buf.putInt((int) radius1);
		buf.putInt((int) radius2);
		buf.putInt(ticksPerFrame);
		
		toSend = buf.array();
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
