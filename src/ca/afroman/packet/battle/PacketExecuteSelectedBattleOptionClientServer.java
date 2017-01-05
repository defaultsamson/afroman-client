package ca.afroman.packet.battle;

import java.nio.ByteBuffer;

import ca.afroman.battle.BattleOption;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;

public class PacketExecuteSelectedBattleOptionClientServer extends BytePacket
{
	private static final int BUFFER_SIZE = 2;
	
	public PacketExecuteSelectedBattleOptionClientServer(BattleOption option, IPConnection... connection)
	{
		super(PacketType.BATTLE_EXECUTE_SELECTED_OPTION, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE).put(typeOrd());
		
		buf.put((byte) option.ordinal());
		
		content = buf.array();
	}
}
