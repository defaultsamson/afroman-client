package ca.afroman.packet;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.entity.ServerPlayerEntity;
import ca.afroman.legacy.packet.PacketType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketSetPlayerLocation extends BytePacket
{
	private byte[] toSend;
	
	public PacketSetPlayerLocation(ServerPlayerEntity player, IPConnection... connection)
	{
		super(PacketType.SET_PLAYER_LOCATION, false, connection);
		
		List<Byte> send = new ArrayList<Byte>();
		
		send.add((byte) player.getRole().ordinal());
		send.add((byte) player.getDirection().ordinal());
		send.add((byte) player.getLastDirection().ordinal());
		
		for (byte e : ByteUtil.doubleAsBytes(player.getX()))
		{
			send.add(e);
		}
		
		for (byte e : ByteUtil.doubleAsBytes(player.getY()))
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
