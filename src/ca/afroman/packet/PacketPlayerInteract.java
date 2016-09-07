package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketPlayerInteract extends BytePacket
{
	public PacketPlayerInteract(IPConnection... connection)
	{
		super(PacketType.PLAYER_INTERACT, false, connection);
	}
}
