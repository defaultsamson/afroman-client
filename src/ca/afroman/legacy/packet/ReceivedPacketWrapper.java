package ca.afroman.legacy.packet;

import ca.afroman.network.IPConnection;

public class ReceivedPacketWrapper
{
	private byte[] bytes;
	private IPConnection sender;
	
	public ReceivedPacketWrapper(byte[] bytes, IPConnection sender)
	{
		this.bytes = bytes;
		this.sender = sender;
	}
	
	public byte[] getBytes()
	{
		return bytes;
	}
	
	public IPConnection getSender()
	{
		return sender;
	}
}
