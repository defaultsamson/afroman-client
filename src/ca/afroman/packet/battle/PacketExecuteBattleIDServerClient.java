package ca.afroman.packet.battle;

import java.nio.ByteBuffer;

import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.util.ByteUtil;

public class PacketExecuteBattleIDServerClient extends BytePacket
{
	private static final int BUFFER_SIZE = 1 + (ByteUtil.INT_BYTE_COUNT * 2);
	
	public PacketExecuteBattleIDServerClient(int battleID, int executeID, IPConnection... connection)
	{
		super(PacketType.BATTLE_EXECUTE_ID, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE).put(typeOrd());
		
		buf.putInt(battleID);
		buf.putInt(executeID);
		
		content = buf.array();
	}
}
