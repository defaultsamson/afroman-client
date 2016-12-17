package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.game.Role;
import ca.afroman.network.IPConnection;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ByteUtil;

public class PacketSetPlayerLocation extends BytePacket
{
	private static final int ALLOCATE_SIZE = 2 + (2 * ByteUtil.INT_BYTE_COUNT);
	
	/**
	 * Designed to be only sent from the server to client
	 * 
	 * @param player
	 * @param pos
	 * @param connection
	 */
	public PacketSetPlayerLocation(Role player, Vector2DDouble pos, boolean forcePos, IPConnection... connection)
	{
		super(PacketType.SET_PLAYER_POSITION, forcePos, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ALLOCATE_SIZE).put(typeOrd());
		
		buf.put((byte) (player.ordinal() + (forcePos ? Role.values().length : 0)));
		
		buf.putInt((int) pos.getX());
		buf.putInt((int) pos.getY());
		
		// buf.put(forcePos ? (byte) 1 : 0);
		
		content = buf.array();
	}
}
