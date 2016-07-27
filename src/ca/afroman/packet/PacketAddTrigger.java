package ca.afroman.packet;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.TriggerType;
import ca.afroman.events.HitboxTrigger;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketAddTrigger extends BytePacket
{
	private byte[] toSend;
	
	public PacketAddTrigger(LevelType level, HitboxTrigger hitbox, IPConnection... connection)
	{
		super(PacketType.ADD_EVENT_HITBOX_TRIGGER, true, connection);
		
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
		for (byte e : ByteUtil.intAsBytes((int) hitbox.getX()))
		{
			send.add(e);
		}
		
		// y
		for (byte e : ByteUtil.intAsBytes((int) hitbox.getY()))
		{
			send.add(e);
		}
		
		// width
		for (byte e : ByteUtil.intAsBytes((int) hitbox.getWidth()))
		{
			send.add(e);
		}
		
		// height
		for (byte e : ByteUtil.intAsBytes((int) hitbox.getHeight()))
		{
			send.add(e);
		}
		
		for (TriggerType e : hitbox.getTriggerTypes())
		{
			send.add((byte) e.ordinal());
		}
		
		send.add(Byte.MIN_VALUE);
		send.add(Byte.MAX_VALUE);
		
		for (int e : hitbox.getInTriggers())
		{
			for (byte b : ByteUtil.intAsBytes(e))
			{
				send.add(b);
			}
		}
		
		send.add(Byte.MIN_VALUE);
		send.add(Byte.MAX_VALUE);
		
		for (int e : hitbox.getOutTriggers())
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
