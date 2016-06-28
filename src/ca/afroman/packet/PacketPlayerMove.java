package ca.afroman.packet;

import ca.afroman.legacy.packet.PacketType;
import ca.afroman.network.IPConnection;

public class PacketPlayerMove extends BytePacket
{
	private byte[] toSend;
	
	public PacketPlayerMove(byte xa, byte ya, IPConnection... connection)
	{
		super(PacketType.REQUEST_PLAYER_MOVE, false, connection);
		
		toSend = new byte[] { xa, ya };
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
