package ca.afroman.packet.battle;

import java.nio.ByteBuffer;

import ca.afroman.game.Role;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;
import ca.afroman.util.ByteUtil;

public class PacketUpdateTurn extends BytePacket
{
	private static final int BUFFER_SIZE = 2 + ByteUtil.INT_BYTE_COUNT;
	
	public PacketUpdateTurn(int battleID, Role whosTurnIsItMrWolf, IPConnection... connection)
	{
		super(PacketType.BATTLE_UPDATE_TURN, true, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE).put(typeOrd());
		
		buf.putInt(battleID);
		buf.put((byte) whosTurnIsItMrWolf.ordinal());
		
		content = buf.array();
	}
}
