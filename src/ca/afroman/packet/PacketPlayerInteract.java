package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.network.IPConnection;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ByteUtil;

public class PacketPlayerInteract extends BytePacket
{
	private static final int ALLOCATE_SIZE = 1 + (ByteUtil.DOUBLE_BYTE_COUNT * 2);
	
	public PacketPlayerInteract(Vector2DDouble pos, IPConnection... connection)
	{
		super(PacketType.PLAYER_INTERACT, false, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ALLOCATE_SIZE).put(typeOrd());
		
		buf.putDouble(pos.getX());
		buf.putDouble(pos.getY());
		
		content = buf.array();
	}
}
