package ca.afroman.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.PortUnreachableException;

import ca.afroman.gui.GuiClickNotification;
import ca.afroman.gui.GuiJoinServer;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.thread.DynamicThread;

public class ClientSocketReceive extends DynamicThread
{
	private ClientSocketManager manager;
	
	public ClientSocketReceive(ClientSocketManager manager)
	{
		super(ClientSocketManager.threadGroupInstance(), "Receive");
		
		this.manager = manager;
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
			ClientGame.instance().addPacket(pack);
		}
		catch (PortUnreachableException e)
		{
			logger().log(ALogType.CRITICAL, "Port is unreachable", e);
			ClientGame.instance().setCurrentScreen(new GuiJoinServer(new GuiMainMenu()));
			new GuiClickNotification(ClientGame.instance().getCurrentScreen(), "PORT", "UNREACHABLE");
		}
		catch (IOException e)
		{
			logger().log(ALogType.CRITICAL, "I/O error while reading packet", e);
		}
	}
	
	@Override
	public void onStop()
	{
		
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
}
