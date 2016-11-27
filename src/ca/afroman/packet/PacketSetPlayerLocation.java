package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.entity.PlayerEntity;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketSetPlayerLocation extends BytePacket
{
	private byte[] toSend;
	
	@Deprecated
	public PacketSetPlayerLocation(PlayerEntity player, IPConnection... connection)
	{
		// TODO make false? typically movement packets should never be forces, and should only use UDP
		super(PacketType.SET_PLAYER_LOCATION, true, connection);
		
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
