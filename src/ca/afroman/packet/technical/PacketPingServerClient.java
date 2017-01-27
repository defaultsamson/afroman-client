package ca.afroman.packet.technical;

import java.nio.ByteBuffer;

import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.util.ByteUtil;

public class PacketPingServerClient extends BytePacket
{
	private static final int ALLOCATE_SIZE = 1 + (ByteUtil.SHORT_BYTE_COUNT * 3);
	
	public static final byte NONE = (byte) (-1);
	
	public PacketPingServerClient(int currentPing, int p1Ping, int p2Ping, IPConnection... connection)
	{
		super(PacketType.TEST_PING, false, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ALLOCATE_SIZE).put(typeOrd());
		
		buf.putShort(normalizePing(currentPing));
		buf.putShort(normalizePing(p1Ping));
		buf.putShort(normalizePing(p2Ping));
		
		content = buf.array();
	}
	
	private short normalizePing(int ping)
	{
		return (short) (ping < 0 ? NONE : ping);
	}
}
