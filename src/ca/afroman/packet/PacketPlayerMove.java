package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketPlayerMove extends BytePacket
{
	private byte[] toSend;
	
	public PacketPlayerMove(byte xa, byte ya, IPConnection... connection)
	{
		// TODO make false? typically movement packets should never be forces, and should only use UDP
		super(PacketType.REQUEST_PLAYER_MOVE, true, connection);
		
		toSend = new byte[] { xa, ya };
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
