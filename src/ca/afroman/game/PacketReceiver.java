package ca.afroman.game;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketException;

import ca.afroman.client.ClientGame;
import ca.afroman.gui.GuiClickNotification;
import ca.afroman.gui.GuiJoinServer;
import ca.afroman.gui.GuiMainMenu;
import ca.afroman.log.ALogType;
import ca.afroman.log.ALogger;
import ca.afroman.network.IncomingPacketWrapper;
import ca.afroman.packet.BytePacket;
import ca.afroman.thread.DynamicThread;
import ca.afroman.util.IPUtil;

public class PacketReceiver extends DynamicThread
{
	private SocketManager manager;
	
	/**
	 * A socket that receives BytePackets and parses them through the provided game.
	 */
	public PacketReceiver(boolean isServerSide, SocketManager manager)
	{
		super(isServerSide, manager.getGame().getThread().getThreadGroup(), "Receive");
		
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
			
			BytePacket pack = new BytePacket(packet.getData());
			InetAddress address = packet.getAddress();
			int port = packet.getPort();
			if (ALogger.tracePackets) logger().log(ALogType.DEBUG, "[" + IPUtil.asReadable(address, port) + "] " + pack.getType());
			
			manager.getGame().addPacketToParse(new IncomingPacketWrapper(pack, address, port));
		}
		catch (PortUnreachableException e)
		{
			logger().log(ALogType.CRITICAL, "Port is unreachable: " + manager.socket().getPort(), e);
			if (!isServerSide())
			{
				ClientGame.instance().setCurrentScreen(new GuiJoinServer(new GuiMainMenu()));
				new GuiClickNotification(ClientGame.instance().getCurrentScreen(), -1, "PORT", "UNREACHABLE");
			}
		}
		catch (SocketException e)
		{
			// TODO this is invisible
			e.printStackTrace();
		}
		catch (IOException e)
		{
			if (isRunning) logger().log(ALogType.CRITICAL, "I/O error while receiving", e);
			
			e.printStackTrace();
		}
	}
}
