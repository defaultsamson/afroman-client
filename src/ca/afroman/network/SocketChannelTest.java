package ca.afroman.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import ca.afroman.client.ClientGame;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;

public class SocketChannelTest
{
	
	public static void main(String[] args) throws IOException
	{
		ALogger.logA(ALogType.DEBUG, TCPSocketChannel.class.getSimpleName());
	}
	
}
