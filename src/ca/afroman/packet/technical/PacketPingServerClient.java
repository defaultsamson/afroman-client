package ca.afroman.packet.technical;

import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;

public class PacketPingServerClient extends BytePacket
{
	public static final int MAX_SENDABLE = (Byte.MAX_VALUE * 2);
	public static final byte OVER_MAX = (byte) (-1);
	public static final byte NONE = 0;
	
	public PacketPingServerClient(int currentPing, int p1Ping, int p2Ping, IPConnection... connection)
	{
		super(PacketType.TEST_PING, false, connection);
		
		content = new byte[] { typeOrd(), normalizePing(currentPing), normalizePing(p1Ping), normalizePing(p2Ping) };
	}
	
	private byte normalizePing(int ping)
	{
		return (byte) ((ping < 0 ? NONE : ping > MAX_SENDABLE ? OVER_MAX : ping) - Byte.MAX_VALUE);
	}
}
