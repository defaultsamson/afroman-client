package ca.afroman.network;

import java.net.InetAddress;

public class IPConnection
{
	private InetAddress address;
	private int port;
	
	/**
	 * A container for an IP address and port.
	 * 
	 * @param address the IP
	 * @param port the port
	 */
	public IPConnection(InetAddress address, int port)
	{
		this.address = address;
		this.port = port;
	}
	
	public void setIPAddress(InetAddress address)
	{
		this.address = address;
	}
	
	public InetAddress getIPAddress()
	{
		return address;
	}
	
	public void setPort(int newPort)
	{
		this.port = newPort;
	}
	
	public int getPort()
	{
		return port;
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
		if (address != null && connection != null)
		{
			return address.getHostAddress().equals(connection.getIPAddress().getHostAddress()) && port == connection.getPort();
		}
		else
		{
			return false;
		}
	}
}
