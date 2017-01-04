package ca.afroman.packet.technical;

import java.nio.ByteBuffer;

import ca.afroman.game.Role;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.util.ByteUtil;

public class PacketSetRole extends BytePacket
{
	private static final int ALLOCATE_SIZE = 2 + (2 * ByteUtil.INT_BYTE_COUNT);
	
	public PacketSetRole(short playerID, Role newRole, IPConnection... connection)
	{
		super(PacketType.SETROLE, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ALLOCATE_SIZE).put(typeOrd());
		
		buf.putShort(playerID);
		buf.put((byte) newRole.ordinal());
		
		content = buf.array();
	}
}
