package ca.afroman.packet;

import java.nio.ByteBuffer;

import ca.afroman.network.IPConnection;

public class PacketAssignClientID extends BytePacket
{
	private static final int ALLOCATE_SIZE = 3;
	
	public PacketAssignClientID(short receivedID, IPConnection... connection)
	{
		super(PacketType.ASSIGN_CLIENTID, false, connection);
		
		ByteBuffer buf = ByteBuffer.allocate(ALLOCATE_SIZE).put(typeOrd());
		
		buf.putShort(receivedID);
		
		content = buf.array();
	}
}
