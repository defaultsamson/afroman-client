package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.entity.ServerPlayerEntity;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketSetPlayerLocation extends BytePacket
{
	private byte[] toSend;
	
	public PacketSetPlayerLocation(ServerPlayerEntity player, IPConnection... connection)
	{
		super(PacketType.SET_PLAYER_LOCATION, false, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(3 + (2 * ByteUtil.DOUBLE_BYTE_COUNT));
		
		buf.put((byte) player.getRole().ordinal());
		buf.put((byte) player.getDirection().ordinal());
		buf.put((byte) player.getLastDirection().ordinal());
		
		buf.putInt((int) player.getPosition().getX());
		buf.putInt((int) player.getPosition().getY());
		
		toSend = buf.array();
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
