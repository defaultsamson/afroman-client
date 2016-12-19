package ca.afroman.packet;

import java.util.ArrayList;
import java.util.List;

import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;
import ca.afroman.util.ListUtil;
import ca.afroman.util.VersionUtil;

public class PacketLogin extends BytePacket
{
	public PacketLogin(String username, String password, IPConnection... connection)
	{
		super(PacketType.REQUEST_CONNECTION, false, connection);
		
		List<Byte> send = new ArrayList<Byte>();
		
		send.add(typeOrd());
		
		for (byte e : ByteUtil.intAsBytes(VersionUtil.SERVER_TEST_VERSION))
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
		
		content = ListUtil.toByteArray(send);
	}
}
