package ca.afroman.packet;

import ca.afroman.legacy.packet.PacketType;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ArrayUtil;
import ca.afroman.util.ByteUtil;

public class PacketRemoveHitbox extends BytePacket
{
	private byte[] toSend;
	
	public PacketRemoveHitbox(int id, LevelType type, IPConnection... connection)
	{
		super(PacketType.REMOVE_LEVEL_HITBOX, true, connection);
		
		toSend = ArrayUtil.concatByteArrays(ByteUtil.intAsBytes(type.ordinal()), ByteUtil.intAsBytes(id));
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
