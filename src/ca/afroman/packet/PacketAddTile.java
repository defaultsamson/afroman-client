package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.assets.AssetType;
import ca.afroman.entity.api.Entity;
import ca.afroman.legacy.packet.PacketType;
import ca.afroman.level.LevelType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketAddTile extends BytePacket
{
	private byte[] toSend;
	
	public PacketAddTile(byte layer, LevelType level, Entity entity, IPConnection... connection)
	{
		super(PacketType.ADD_LEVEL_TILE, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(1 + ByteUtil.SHORT_BYTE_COUNT + (4 * ByteUtil.INT_BYTE_COUNT));
		
		// Level Type
		buf.putShort((short) level.ordinal());
		
		// Layer
		buf.put(layer);
		
		// ID
		buf.putInt(entity.getID());
		
		// Asset Type
		buf.putInt(entity.getAssetType() != null ? entity.getAssetType().ordinal() : AssetType.INVALID.ordinal());
		
		// x
		buf.putInt((int) entity.getX());
		
		// y
		buf.putInt((int) entity.getY());
		
		toSend = buf.array();
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
