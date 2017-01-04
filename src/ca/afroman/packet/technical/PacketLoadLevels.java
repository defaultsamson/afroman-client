package ca.afroman.packet.technical;

import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.packet.PacketType;

public class PacketLoadLevels extends BytePacket
{
	public PacketLoadLevels(boolean isSending, IPConnection... connection)
	{
		super(PacketType.LOAD_LEVELS, true, connection);
		
		content = new byte[] { typeOrd(), (byte) (isSending ? 1 : 0) };
	}
}
