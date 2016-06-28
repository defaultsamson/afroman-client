package ca.afroman.packet;

import ca.afroman.legacy.packet.PacketType;
import ca.afroman.network.IPConnection;

public class PacketSendLevels extends BytePacket
{
	private boolean isSending;
	
	public PacketSendLevels(boolean isSending, IPConnection... connection)
	{
		super(PacketType.SEND_LEVELS, true, connection);
		
		this.isSending = isSending;
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return new byte[] { (byte) (isSending ? 1 : 0) };
	}
}
