package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.network.IPConnection;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ByteUtil;

public class PacketSetPlayerLocationClientServer extends BytePacket
{
	private static final int ALLOCATE_SIZE = 1 + (2 * ByteUtil.DOUBLE_BYTE_COUNT);
	
	/**
	 * Designed to be only sent from the server to client
	 * 
	 * @param player
	 * @param pos
	 * @param connection
	 */
	public PacketSetPlayerLocationClientServer(Vector2DDouble pos, IPConnection... connection)
	{
		super(PacketType.SET_PLAYER_POSITION, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ALLOCATE_SIZE).put(typeOrd());
		
		buf.putDouble(pos.getX());
		buf.putDouble(pos.getY());
		
		content = buf.array();
	}
}
