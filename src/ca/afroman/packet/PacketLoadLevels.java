package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketLoadLevels extends BytePacket
{
	public PacketLoadLevels(boolean isSending, IPConnection... connection)
	{
		super(PacketType.LOAD_LEVELS, true, connection);
		
		content = new byte[] { typeOrd(), (byte) (isSending ? 1 : 0) };
	}
}
