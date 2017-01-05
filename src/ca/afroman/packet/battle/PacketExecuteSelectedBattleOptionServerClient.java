package ca.afroman.packet.battle;

import java.nio.ByteBuffer;

import ca.afroman.battle.BattleOption;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.util.ByteUtil;

public class PacketExecuteSelectedBattleOptionServerClient extends BytePacket
{
	private static final int BUFFER_SIZE = 2 + ByteUtil.INT_BYTE_COUNT;
	
	public PacketExecuteSelectedBattleOptionServerClient(int battleID, BattleOption option, IPConnection... connection)
	{
		super(PacketType.BATTLE_EXECUTE_SELECTED_OPTION, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE).put(typeOrd());
		
		buf.putInt(battleID);
		buf.put((byte) option.ordinal());
		
		content = buf.array();
	}
}
