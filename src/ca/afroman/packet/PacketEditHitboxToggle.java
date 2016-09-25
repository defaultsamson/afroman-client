package ca.afroman.packet;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.events.HitboxToggle;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketEditHitboxToggle extends BytePacket
{
	private byte[] toSend;
	
	public PacketEditHitboxToggle(LevelType level, boolean enabled, int id, List<Integer> inTriggers, List<Integer> outTriggers, IPConnection... connection)
	{
		super(PacketType.EDIT_EVENT_HITBOX_TOGGLE, true, connection);
		
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
		
		// is enabled
		send.add(enabled ? (byte) 1 : 0);
		
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
	
	public PacketEditHitboxToggle(LevelType level, HitboxToggle hitbox, IPConnection... connection)
	{
		this(level, hitbox.isEnabled(), hitbox.getID(), hitbox.getInTriggers(), hitbox.getOutTriggers(), connection);
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
