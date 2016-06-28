package ca.afroman.packet;

import java.util.ArrayList;
import java.util.List;

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
		
		List<Byte> send = new ArrayList<Byte>();
		
		// Level Type
		for (byte e : ByteUtil.shortAsBytes((short) level.ordinal()))
		{
			send.add(e);
		}
		
		// ID
		for (byte e : ByteUtil.intAsBytes(hitbox.getID()))
		{
			send.add(e);
		}
		
		// x
		for (byte e : ByteUtil.doubleAsBytes(hitbox.getX()))
		{
			send.add(e);
		}
		
		// y
		for (byte e : ByteUtil.doubleAsBytes(hitbox.getY()))
		{
			send.add(e);
		}
		
		// width
		for (byte e : ByteUtil.doubleAsBytes(hitbox.getWidth()))
		{
			send.add(e);
		}
		
		// height
		for (byte e : ByteUtil.doubleAsBytes(hitbox.getHeight()))
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
