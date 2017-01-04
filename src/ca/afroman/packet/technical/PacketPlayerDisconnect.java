package ca.afroman.packet.technical;

import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;

public class PacketPlayerDisconnect extends BytePacket
{
	public PacketPlayerDisconnect(IPConnection... connection)
	{
		super(PacketType.PLAYER_DISCONNECT, false, connection);
		
		content = new byte[] { typeOrd() };
	}
}
