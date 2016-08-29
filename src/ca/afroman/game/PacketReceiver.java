package ca.afroman.game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.PortUnreachableException;

import ca.afroman.client.ClientGame;
import ca.afroman.entity.api.IServerClient;
import ca.afroman.gui.GuiClickNotification;
import ca.afroman.gui.GuiJoinServer;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.IPConnection;
import ca.afroman.packet.BytePacket;
import ca.afroman.thread.DynamicThread;

public class PacketReceiver extends DynamicThread implements IServerClient
{
	private SocketManager manager;
	
	/**
	 * A socket that receives BytePackets and parses them through the provided game.
	 */
	public PacketReceiver(SocketManager manager)
	{
		super(manager.getGame().getThreadGroup(), "Receive");
		
		this.manager = manager;
	}
	
	@Override
	public boolean isServerSide()
	{
		return manager.isServerSide();
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
			manager.getGame().addPacketToParse(pack);
		}
		catch (PortUnreachableException e)
		{
			logger().log(ALogType.CRITICAL, "Port is unreachable: " + manager.socket().getPort(), e);
			if (!isServerSide())
			{
				ClientGame.instance().setCurrentScreen(new GuiJoinServer(new GuiMainMenu()));
				new GuiClickNotification(ClientGame.instance().getCurrentScreen(), "PORT", "UNREACHABLE");
			}
		}
		catch (IOException e)
		{
			if (isRunning) logger().log(ALogType.CRITICAL, "I/O error while receiving", e);
		}
	}
}
