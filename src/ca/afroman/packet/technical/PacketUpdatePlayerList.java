package ca.afroman.packet.technical;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.network.ConnectedPlayer;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.util.ByteUtil;
import ca.afroman.util.ListUtil;

public class PacketUpdatePlayerList extends BytePacket
{
	public PacketUpdatePlayerList(List<ConnectedPlayer> connections, IPConnection... connection)
	{
		super(PacketType.UPDATE_PLAYERLIST, false, connection);
		
		List<Byte> send = new ArrayList<Byte>();
		
		send.add(typeOrd());
		
		for (ConnectedPlayer con : connections)
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
		
		content = ListUtil.toByteArray(send);
	}
}
