package ca.afroman.packet.level;

import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;

public class PacketPlayerMoveClientServer extends BytePacket
{
	public PacketPlayerMoveClientServer(byte dXa, byte dYa, IPConnection... connection)
	{
		super(PacketType.PLAYER_MOVE, false, connection);
		
		content = new byte[] { typeOrd(), dXa, dYa };
	}
}
