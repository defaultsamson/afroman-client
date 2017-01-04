package ca.afroman.packet.technical;

import ca.afroman.client.ExitGameReason;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;

public class PacketReturnToLobby extends BytePacket
{
	public PacketReturnToLobby(ExitGameReason reason, IPConnection... connection)
	{
		super(PacketType.RETURN_TO_LOBBY, true, connection);
		
		content = new byte[] { typeOrd(), (byte) reason.ordinal() };
	}
}
