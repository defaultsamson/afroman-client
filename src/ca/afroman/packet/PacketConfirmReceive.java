package ca.afroman.packet;

import ca.afroman.legacy.packet.PacketType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketConfirmReceive extends BytePacket
{
	private int receivedID;
	
	public PacketConfirmReceive(int receivedID, IPConnection... connection)
	{
		super(PacketType.CONFIRM_RECEIVED, false, connection);
		
		this.receivedID = receivedID;
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return ByteUtil.intAsBytes(receivedID);
	}
}
