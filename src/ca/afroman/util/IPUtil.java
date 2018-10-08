package ca.afroman.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import ca.afroman.network.IPConnection;

public class IPUtil
{
	public static String asReadable(InetAddress address, int port)
	{
		return (address != null ? address.getHostAddress() : "null") + ":" + port;
	}
	
	public static String asReadable(InetSocketAddress address)
	{
		return (address != null ? address.getAddress() + ":" + address.getPort() : "null");
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
			byte[] add1 = address.getAddress();
			byte[] add2 = connection.getIPAddress().getAddress();
			
			if (add1.length != add2.length) return false;
			
			for (byte i = 0; i < add1.length; i++)
			{
				if (add1[i] != add2[i]) return false;
			}
			
			// Old method
			// boolean ipMatch = address.getHostAddress().equals(connection.getIPAddress().getHostAddress());
			// if (!ipMatch) return false;
			
			// First check if the port matches with UDP socket
			if (port == connection.getPort()) return true;
			
			// If not, then check TCP socket
			if (connection.getSocket() != null)
			{
				if (port == connection.getSocket().socket().getPort()) return true;
			}
		}
		return false;
	}

}
