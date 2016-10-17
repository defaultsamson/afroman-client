package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.level.LevelType;
import ca.afroman.light.PointLight;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketAddPointLight extends BytePacket
{
	private byte[] toSend;
	
	public PacketAddPointLight(LevelType level, PointLight light, IPConnection... connection)
	{
		super(PacketType.ADD_LEVEL_POINTLIGHT, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ByteUtil.SHORT_BYTE_COUNT + (4 * ByteUtil.INT_BYTE_COUNT));
		
		// Level Type
		buf.putShort((short) level.ordinal());
		
		// ID
		buf.putInt(light.getID());
		
		// x
		buf.putInt((int) light.getPosition().getX());
		
		// y
		buf.putInt((int) light.getPosition().getY());
		
		// radius
		buf.putInt((int) light.getRadius());
		
		toSend = buf.array();
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
