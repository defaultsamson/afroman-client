package ca.afroman.packet;

import ca.afroman.network.IPConnection;

public class PacketStartServer extends BytePacket
{
	private byte[] toSend;
	
	public PacketStartServer(boolean shouldStart, IPConnection... connection)
	{
		super(PacketType.START_SERVER, true, connection);
		
		toSend = new byte[] { shouldStart ? 1 : (byte) 0 };
	}
	
	@Override
	public byte[] getUniqueData()
	{
		return toSend;
	}
}
