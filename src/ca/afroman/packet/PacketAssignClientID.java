package ca.afroman.packet;

import ca.afroman.legacy.packet.PacketType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketAssignClientID extends BytePacket
{
	private byte[] toSend;
	
	public PacketAssignClientID(short receivedID, IPConnection... connection)
	{
		super(PacketType.ASSIGN_CLIENTID, true, connection);
		
		toSend = ByteUtil.shortAsBytes(receivedID);
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
