package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.level.api.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ByteUtil;

public class PacketSetEntityLocation extends BytePacket
{
	private static final int ALLOCATE_SIZE = 2 + (3 * ByteUtil.INT_BYTE_COUNT) + ByteUtil.SHORT_BYTE_COUNT;
	
	/**
	 * Designed to be only sent from the server to client
	 * 
	 * @param player
	 * @param pos
	 * @param connection
	 */
	public PacketSetEntityLocation(LevelType level, int id, Vector2DDouble pos, boolean forcePos, IPConnection... connection)
	{
		super(PacketType.SET_ENTITY_POSITION, forcePos, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ALLOCATE_SIZE).put(typeOrd());
		
		buf.putShort((short) level.ordinal());
		buf.putInt(id);
		buf.put(forcePos ? 1 : (byte) 0);
		buf.putInt((int) pos.getX());
		buf.putInt((int) pos.getY());
		
		content = buf.array();
	}
}
