package ca.afroman.packet;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.TriggerType;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketEditTrigger extends BytePacket
{
	private byte[] toSend;
	
	public PacketEditTrigger(LevelType level, int id, List<TriggerType> triggers, List<Integer> inTriggers, List<Integer> outTriggers, IPConnection... connection)
	{
		super(PacketType.EDIT_EVENT_HITBOX_TRIGGER, true, connection);
		
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
		
		for (TriggerType e : triggers)
		{
			send.add((byte) e.ordinal());
		}
		
		send.add(Byte.MIN_VALUE);
		send.add(Byte.MAX_VALUE);
		
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
