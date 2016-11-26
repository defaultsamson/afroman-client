package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketLoadLevels extends BytePacket
{
	private byte[] toSend;
	
	public PacketLoadLevels(boolean isSending, IPConnection... connection)
	{
		super(PacketType.LOAD_LEVELS, true, connection);
		
		toSend = new byte[] { (byte) (isSending ? 1 : 0) };
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
