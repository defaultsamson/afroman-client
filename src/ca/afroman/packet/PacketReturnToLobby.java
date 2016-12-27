package ca.afroman.packet;

import ca.afroman.client.ExitGameReason;
import ca.afroman.network.IPConnection;

public class PacketReturnToLobby extends BytePacket
{
	public PacketReturnToLobby(ExitGameReason reason, IPConnection... connection)
	{
		super(PacketType.RETURN_TO_LOBBY, true, connection);
		
		content = new byte[] { typeOrd(), (byte) reason.ordinal() };
	}
}
