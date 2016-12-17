package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketPingServerClient extends BytePacket
{
	public static final int MAX_SENDABLE = (Byte.MAX_VALUE * 2);
	public static final byte OVER_MAX = (byte) (-1);
	public static final byte NONE = 0;
	
	byte[] toSend;
	
	public PacketPingServerClient(int currentPing, int p1Ping, int p2Ping, IPConnection... connection)
	{
		super(PacketType.TEST_PING, false, connection);
		
		toSend = new byte[3];
		
		toSend[0] = normalizePing(currentPing);
		toSend[1] = normalizePing(p1Ping);
		toSend[2] = normalizePing(p2Ping);
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
	
	private byte normalizePing(int ping)
	{
		return (byte) ((ping < 0 ? NONE : ping > MAX_SENDABLE ? OVER_MAX : ping) - Byte.MAX_VALUE);
	}
}
