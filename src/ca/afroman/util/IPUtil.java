package ca.afroman.util;

import java.net.InetAddress;
import java.nio.channels.SocketChannel;

import ca.afroman.network.IPConnection;

public class IPUtil
{
	public static String asReadable(InetAddress address, int port)
	{
		return (address != null ? address.getHostAddress() : "null") + ":" + port;
	}
	
	public static boolean equals(InetAddress address, int port, InetAddress address2, int port2)
	{
		if (address != null && address2 != null)
		{
			return address.getHostAddress().equals(address2.getHostAddress()) && port == port2;
		}
		return false;
	}
	
	public static boolean equals(InetAddress address, int port, IPConnection connection)
	{
		if (address != null && connection != null && connection.getIPAddress() != null)
		{
			boolean ipMatch = address.getHostAddress().equals(connection.getIPAddress().getHostAddress());
			
			if (!ipMatch) return false;
			
			// First check if the port matches with UDP socket
			if (port == connection.getPort()) return true;
			
			// If not, then check TCP socket
			if (connection.getTCPSocketChannel() != null && connection.getTCPSocketChannel().getSocket() != null)
			{
				if (port == connection.getTCPSocketChannel().getSocket().socket().getPort()) return true;
			}
		}
		return false;
	}
}
