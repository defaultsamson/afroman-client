package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.inventory.ItemType;
import ca.afroman.network.IPConnection;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ByteUtil;

public class PacketItemDropClientServer extends BytePacket
{
	private static final int ALLOCATE_SIZE = 2 + (ByteUtil.DOUBLE_BYTE_COUNT * 2);
	
	public PacketItemDropClientServer(ItemType type, Vector2DDouble pos, IPConnection... connection)
	{
		// TODO make false?
		super(PacketType.PLAYER_DROP_ITEM, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ALLOCATE_SIZE).put(typeOrd());
		
		buf.put((byte) type.ordinal());
		buf.putDouble(pos.getX());
		buf.putDouble(pos.getY());
		
		content = buf.array();
	}
}
