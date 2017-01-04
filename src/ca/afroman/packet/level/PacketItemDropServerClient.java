package ca.afroman.packet.level;

import java.nio.ByteBuffer;

import ca.afroman.game.Role;
import ca.afroman.inventory.ItemType;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.resource.Vector2DDouble;
import ca.afroman.util.ByteUtil;

public class PacketItemDropServerClient extends BytePacket
{
	private static final int BUFFER_SIZE = 3 + (ByteUtil.DOUBLE_BYTE_COUNT * 2);
	
	public PacketItemDropServerClient(Role role, ItemType type, Vector2DDouble pos, IPConnection... connection)
	{
		super(PacketType.PLAYER_DROP_ITEM, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE).put(typeOrd());
		
		buf.put((byte) role.ordinal());
		buf.put((byte) type.ordinal());
		buf.putDouble(pos.getX());
		buf.putDouble(pos.getY());
		
		content = buf.array();
	}
}
