package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.game.Role;
import ca.afroman.level.api.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketItemPickup extends BytePacket
{
	private static final int BUFFER_SIZE = 2 + ByteUtil.SHORT_BYTE_COUNT + ByteUtil.INT_BYTE_COUNT;
	
	public PacketItemPickup(Role role, LevelType levelType, int itemID, IPConnection... connection)
	{
		super(PacketType.PLAYER_PICKUP_ITEM, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE).put(typeOrd());
		
		buf.putShort((short) levelType.ordinal());
		buf.putInt(itemID);
		buf.put((byte) role.ordinal());
		
		content = buf.array();
	}
}
