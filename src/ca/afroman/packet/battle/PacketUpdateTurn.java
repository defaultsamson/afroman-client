package ca.afroman.packet.battle;

import java.nio.ByteBuffer;

import ca.afroman.game.Role;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.util.ByteUtil;

public class PacketUpdateTurn extends BytePacket
{
	private static final int BUFFER_SIZE1 = 2 + ByteUtil.INT_BYTE_COUNT;
	
	private static final int BUFFER_SIZE2 = 1 + (ByteUtil.INT_BYTE_COUNT * 2);
	
	public PacketUpdateTurn(int battleID, int roleOrd, IPConnection... connection)
	{
		super(PacketType.BATTLE_UPDATE_TURN_ORD, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE2).put(typeOrd());
		
		buf.putInt(battleID);
		buf.putInt(roleOrd);
		
		content = buf.array();
	}
	
	public PacketUpdateTurn(int battleID, Role whosTurnIsItMrWolf, IPConnection... connection)
	{
		super(PacketType.BATTLE_UPDATE_TURN_ROLE, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE1).put(typeOrd());
		
		buf.putInt(battleID);
		buf.put((byte) whosTurnIsItMrWolf.ordinal());
		
		content = buf.array();
	}
}
