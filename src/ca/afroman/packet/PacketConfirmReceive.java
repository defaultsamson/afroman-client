package ca.afroman.packet;

import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketConfirmReceive extends BytePacket
{
	private byte[] toSend;
	
	public PacketConfirmReceive(int receivedID, IPConnection... connection)
	{
		super(PacketType.CONFIRM_RECEIVED, false, connection);
		
		toSend = ByteUtil.intAsBytes(receivedID);
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
