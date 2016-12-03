package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.game.Role;
import ca.afroman.network.IPConnection;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ByteUtil;

public class PacketSetPlayerLocation extends BytePacket
{
	private byte[] toSend;
	
	/**
	 * Designed to be only sent from the server to client
	 * 
	 * @param player
	 * @param pos
	 * @param connection
	 */
	public PacketSetPlayerLocation(Role player, Vector2DDouble pos, IPConnection... connection)
	{
		super(PacketType.SET_PLAYER_POSITION, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(1 + (2 * ByteUtil.INT_BYTE_COUNT));
		
		buf.put((byte) player.ordinal());
		
		buf.putInt((int) pos.getX());
		buf.putInt((int) pos.getY());
		
		toSend = buf.array();
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
