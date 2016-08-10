package ca.afroman.server;

import java.io.IOException;
import java.net.DatagramPacket;

import ca.afroman.client.ClientGame;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.thread.DynamicThread;

public class ServerSocketReceive extends DynamicThread
{
	public String password;
	
	private ServerSocketManager manager;
	
	/**
	 * A new server instance.
	 * 
	 * @param password the password for the server. Enter "" for no password.
	 */
	public ServerSocketReceive(ServerSocketManager manager, String password)
	{
		super(ServerGame.instance().getThreadGroup(), "Receive");
		
		this.manager = manager;
		this.password = password;
	}
	
	@Override
	public void onRun()
	{
		byte[] buffer = new byte[ClientGame.RECEIVE_PACKET_BUFFER_LIMIT];
		
		// Loads up the buffer with incoming data
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		try
		{
			manager.socket().receive(packet);
			
			BytePacket pack = new BytePacket(packet.getData(), new IPConnection(packet.getAddress(), packet.getPort()));
			if (ALogger.tracePackets) logger().log(ALogType.DEBUG, "[" + pack.getConnections().get(0).asReadable() + "] " + pack.getType());
			ServerGame.instance().parsePacket(pack);
		}
		catch (IOException e)
		{
			logger().log(ALogType.CRITICAL, "I/O error while receiving", e);
		}
	}
	
	@Override
	public void onStart()
	{
		
	}
	
	@Override
	public void onPause()
	{
		
	}
	
	@Override
	public void onUnpause()
	{
		
	}
	
	@Override
	public void onStop()
	{
		
	}
}
