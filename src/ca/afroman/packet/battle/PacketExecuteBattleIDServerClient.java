package ca.afroman.packet.battle;

import java.nio.ByteBuffer;

import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.util.ByteUtil;

public class PacketExecuteBattleIDServerClient extends BytePacket
{
	private static final int BUFFER_SIZE_1 = 1 + (ByteUtil.INT_BYTE_COUNT * 2);
	
	private static final int BUFFER_SIZE_2 = 1 + (ByteUtil.INT_BYTE_COUNT * 3);
	
	public PacketExecuteBattleIDServerClient(int battleID, int executeID, int deltaHealth, IPConnection... connection)
	{
		super(PacketType.BATTLE_EXECUTE_ID_HEALTH, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE_2).put(typeOrd());
		
		buf.putInt(battleID);
		buf.putInt(executeID);
		buf.putInt(deltaHealth);
		
		content = buf.array();
	}
	
	public PacketExecuteBattleIDServerClient(int battleID, int executeID, IPConnection... connection)
	{
		super(PacketType.BATTLE_EXECUTE_ID, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE_1).put(typeOrd());
		
		buf.putInt(battleID);
		buf.putInt(executeID);
		
		content = buf.array();
	}
}
