package ca.afroman.packet;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketEditTPTrigger extends BytePacket
{
	private byte[] toSend;
	
	public PacketEditTPTrigger(LevelType level, int id, LevelType toTpTo, double x, double y, List<Integer> inTriggers, List<Integer> outTriggers, IPConnection... connection)
	{
		super(PacketType.EDIT_EVENT_TP_TRIGGER, true, connection);
		
		List<Byte> send = new ArrayList<Byte>();
		
		// Level
		for (byte e : ByteUtil.shortAsBytes((short) level.ordinal()))
		{
			send.add(e);
		}
		
		// ID
		for (byte e : ByteUtil.intAsBytes(id))
		{
			send.add(e);
		}
		
		// toTpTo
		for (byte e : ByteUtil.shortAsBytes((short) toTpTo.ordinal()))
		{
			send.add(e);
		}
		
		// x
		for (byte e : ByteUtil.intAsBytes((int) x))
		{
			send.add(e);
		}
		
		// y
		for (byte e : ByteUtil.intAsBytes((int) y))
		{
			send.add(e);
		}
		
		for (int e : inTriggers)
		{
			for (byte b : ByteUtil.intAsBytes(e))
			{
				send.add(b);
			}
		}
		
		send.add(Byte.MIN_VALUE);
		send.add(Byte.MAX_VALUE);
		
		for (int e : outTriggers)
		{
			for (byte b : ByteUtil.intAsBytes(e))
			{
				send.add(b);
			}
		}
		
		send.add(Byte.MIN_VALUE);
		send.add(Byte.MAX_VALUE);
		
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
