package ca.afroman.packet;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.gfx.PointLight;
import ca.afroman.legacy.packet.PacketType;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketAddPointLight extends BytePacket
{
	private byte[] toSend;
	
	public PacketAddPointLight(LevelType level, PointLight light, IPConnection... connection)
	{
		super(PacketType.ADD_LEVEL_POINTLIGHT, true, connection);
		
		List<Byte> send = new ArrayList<Byte>();
		
		// Level Type
		for (byte e : ByteUtil.shortAsBytes((short) level.ordinal()))
		{
			send.add(e);
		}
		
		// ID
		for (byte e : ByteUtil.intAsBytes(light.getID()))
		{
			send.add(e);
		}
		
		// x
		for (byte e : ByteUtil.doubleAsBytes(light.getX()))
		{
			send.add(e);
		}
		
		// y
		for (byte e : ByteUtil.doubleAsBytes(light.getY()))
		{
			send.add(e);
		}
		
		// radius
		for (byte e : ByteUtil.doubleAsBytes(light.getRadius()))
		{
			send.add(e);
		}
		
		toSend = new byte[send.size()];
		
		int i = 0;
		for (byte e : send)
		{
			toSend[i] = e;
			i++;
		}
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
