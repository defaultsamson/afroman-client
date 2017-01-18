package ca.afroman.packet.battle;

import java.nio.ByteBuffer;

import ca.afroman.battle.BattlePosition;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.util.ByteUtil;

public class PacketSelectEntityServerClient extends BytePacket
{
	private static final int BUFFER_SIZE = 2 + ByteUtil.INT_BYTE_COUNT;
	
	public PacketSelectEntityServerClient(int battleID, BattlePosition pos, IPConnection... connection)
	{
		super(PacketType.BATTLE_SELECT_ENTITY, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE).put(typeOrd());
		
		buf.putInt(battleID);
		buf.put((byte) pos.ordinal());
		
		content = buf.array();
	}
}
