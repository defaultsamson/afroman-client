package ca.afroman.packet;

import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketPingServerClient extends BytePacket
{
	byte[] toSend;
	
	public PacketPingServerClient(int currentPing, IPConnection... connection)
	{
		super(PacketType.TEST_PING, false, connection);
		
		toSend = new byte[2];
		
		byte[] ping = ByteUtil.shortAsBytes((short) currentPing);
		toSend[0] = ping[0];
		toSend[1] = ping[1];
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
