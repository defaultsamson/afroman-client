package ca.afroman.network;

import java.net.InetAddress;

import ca.afroman.util.IPUtil;

public class IPConnection
{
	private TCPSocketChannel tcpSocketChannel;
	private InetAddress address;
	private int port;
	
	/**
	 * A container for an IP address and port.
	 * 
	 * @param address the IP
	 * @param port the port
	 */
	public IPConnection(InetAddress address, int port, TCPSocketChannel tcpSocketChannel)
	{
		this.tcpSocketChannel = tcpSocketChannel;
		this.address = address;
		this.port = port;
	}
	
	/**
	 * @return this IP and port in a readable form. (ex. "127.0.0.1:25565")
	 */
	public String asReadable()
	{
		return address.getHostAddress() + ":" + port;
	}
	
	public boolean equals(IPConnection connection)
	{
		return connection != null ? IPUtil.equals(address, port, connection.getIPAddress(), connection.getPort()) : false;
	}
	
	public InetAddress getIPAddress()
	{
		return address;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public TCPSocketChannel getTCPSocketChannel()
	{
		return tcpSocketChannel;
	}
	
	public void setIPAddress(InetAddress address)
	{
		this.address = address;
	}
	
	public void setPort(int newPort)
	{
		this.port = newPort;
	}
	
	public void setTCPSocketChannel(TCPSocketChannel socket)
	{
		tcpSocketChannel = socket;
	}
}
