package ca.afroman.packet;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.client.ClientGame;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketLogin extends BytePacket
{
	private byte[] toSend;
	
	public PacketLogin(String username, String password, IPConnection... connection)
	{
		super(PacketType.REQUEST_CONNECTION, false, connection);
		
		List<Byte> send = new ArrayList<Byte>();
		
		for (byte e : ByteUtil.shortAsBytes(ClientGame.VERSION))
		{
			send.add(e);
		}
		
		for (byte e : username.getBytes())
		{
			send.add(e);
		}
		
		// The signal that the username is ending and password begins
		send.add(Byte.MIN_VALUE);
		send.add(Byte.MAX_VALUE);
		
		for (byte e : password.getBytes())
		{
			send.add(e);
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
