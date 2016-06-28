package ca.afroman.packet;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.legacy.packet.PacketType;
import ca.afroman.network.IPConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketUpdatePlayerList extends BytePacket
{
	private byte[] toSend;
	
	public PacketUpdatePlayerList(List<IPConnectedPlayer> connections, IPConnection... connection)
	{
		super(PacketType.UPDATE_PLAYERLIST, true, connection);
		
		List<Byte> send = new ArrayList<Byte>();
		
		for (IPConnectedPlayer con : connections)
		{
			for (byte e : ByteUtil.shortAsBytes(con.getID()))
			{
				send.add(e);
			}
			
			send.add((byte) con.getRole().ordinal());
			
			for (byte e : con.getUsername().getBytes())
			{
				send.add(e);
			}
			
			// The signal that a new player is being declared after the username is finished sending
			send.add(Byte.MIN_VALUE);
			send.add(Byte.MAX_VALUE);
		}
		
		send.add(Byte.MIN_VALUE);
		send.add(Byte.MAX_VALUE);
		send.add(Byte.MAX_VALUE);
		send.add(Byte.MIN_VALUE);
		
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
