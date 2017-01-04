package ca.afroman.packet.level;

import java.nio.ByteBuffer;

import ca.afroman.game.Role;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.util.ByteUtil;

public class PacketItemPickup extends BytePacket
{
	private static final int BUFFER_SIZE = 2 + ByteUtil.INT_BYTE_COUNT;
	
	public PacketItemPickup(Role role, int itemID, IPConnection... connection)
	{
		super(PacketType.PLAYER_PICKUP_ITEM, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE).put(typeOrd());
		
		buf.put((byte) role.ordinal());
		buf.putInt(itemID);
		
		content = buf.array();
	}
}
