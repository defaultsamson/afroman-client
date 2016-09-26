package ca.afroman.network;

import java.net.InetAddress;

import ca.afroman.packet.BytePacket;

public class IncomingPacketWrapper
{
	private BytePacket packet;
	private InetAddress address;
	private int port;
	
	/**
	 * A container for an IP address and port.
	 * 
	 * @param address the IP
	 * @param port the port
	 */
	public IncomingPacketWrapper(BytePacket packet, InetAddress address, int port)
	{
		this.packet = packet;
		this.address = address;
		this.port = port;
	}
	
	public InetAddress getIPAddress()
	{
		return address;
	}
	
	public BytePacket getPacket()
	{
		return packet;
	}
	
	public int getPort()
	{
		return port;
	}
}
