package ca.afroman.packet;

import ca.afroman.legacy.packet.PacketType;
import ca.afroman.network.IPConnection;
import ca.afroman.util.ByteUtil;

public class PacketAssignClientID extends BytePacket
{
	private short receivedID;
	
	public PacketAssignClientID(short receivedID, IPConnection... connection)
	{
		super(PacketType.ASSIGN_CLIENTID, true, connection);
		
		this.receivedID = receivedID;
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return ByteUtil.shortAsBytes(receivedID);
	}
}
