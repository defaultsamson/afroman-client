package ca.afroman.packet;

import java.util.ArrayList;
import java.util.List;

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
		
		List<Byte> send = new ArrayList<Byte>();
		
		// Layer
		send.add(layer);
		
		// Level Type
		for (byte e : ByteUtil.shortAsBytes((short) level.ordinal()))
		{
			send.add(e);
		}
		
		// ID
		for (byte e : ByteUtil.intAsBytes(entity.getID()))
		{
			send.add(e);
		}
		
		// Asset Type
		for (byte e : ByteUtil.intAsBytes(entity.getAssetType() != null ? entity.getAssetType().ordinal() : AssetType.INVALID.ordinal()))
		{
			send.add(e);
		}
		
		// x
		for (byte e : ByteUtil.doubleAsBytes(entity.getX()))
		{
			send.add(e);
		}
		
		// y
		for (byte e : ByteUtil.doubleAsBytes(entity.getY()))
		{
			send.add(e);
		}
		
		toSend = new byte[send.size()];
		
		int i = 0;
		for (byte e : send)
		{
			toSend[i] = e;
			i++;
		}
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
