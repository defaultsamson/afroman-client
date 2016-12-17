package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketPlayerMoveClientServer extends BytePacket
{
	public PacketPlayerMoveClientServer(byte dXa, byte dYa, IPConnection... connection)
	{
		super(PacketType.PLAYER_MOVE, false, connection);
		
		content = new byte[] { typeOrd(), dXa, dYa };
	}
}
