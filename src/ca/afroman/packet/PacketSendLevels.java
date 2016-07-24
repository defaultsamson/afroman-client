package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketSendLevels extends BytePacket
{
	private byte[] toSend;
	
	public PacketSendLevels(boolean isSending, IPConnection... connection)
	{
		super(PacketType.SEND_LEVELS, true, connection);
		
		toSend = new byte[] { (byte) (isSending ? 1 : 0) };
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
